package dev.beecube31.crazyae2.common.crafting;

import appeng.api.config.Actionable;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.execution.CraftingCpuHelper;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ICraftingInventory;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.service.CraftingService;
import dev.beecube31.crazyae2.common.interfaces.crafting.IQuantumCraftingProvider;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.mixins.crafting.AccessorElapsedTimeTracker;
import dev.beecube31.crazyae2.mixins.crafting.AccessorExecutingCraftingJob;
import dev.beecube31.crazyae2.mixins.crafting.AccessorTaskProgress;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class QuantumBatchExecutor {
    private QuantumBatchExecutor() {}

    public static int execute(CraftingCPUCluster cluster, ExecutingCraftingJob job, ListCraftingInventory inventory,
                              int maxOps, CraftingService craftingService, IEnergyService energyService, Level level) {
        if (maxOps <= 0) return 0;

        var jobAccess = (AccessorExecutingCraftingJob) job;
        var tasks = jobAccess.crazyae$getTasks();
        if (tasks.isEmpty()) {
            return 0;
        }

        final boolean quantumCpu = isQuantumCluster(cluster);
        final boolean batchToProviders = quantumCpu && CrazyAEConfig.COMMON.quantumCpuBatchToPatternProviders.get();
        final long maxPerProvider = Math.max(1, CrazyAEConfig.COMMON.quantumCpuMaxBatchPerProvider.get());

        int used = 0;
        Set<IQuantumCraftingProvider> touched = null;

        var it = tasks.entrySet().iterator();
        taskLoop:
        while (it.hasNext()) {
            if (used >= maxOps) break;

            var task = it.next();
            var details = task.getKey();
            var progress = (AccessorTaskProgress) task.getValue();

            if (progress.crazyae$getValue() <= 0) {
                it.remove();
                continue;
            }

            for (var provider : craftingService.getProviders(details)) {
                long left = progress.crazyae$getValue();
                if (left <= 0 || used >= maxOps) break;

                long limit;
                boolean quantumProvider = provider instanceof IQuantumCraftingProvider;
                if (quantumProvider) {
                    limit = Long.MAX_VALUE;
                } else if (batchToProviders && isBatchablePatternProvider(provider, details)) {
                    limit = maxPerProvider;
                } else {
                    continue;
                }

                if (provider.isBusy()) continue;

                long want = Math.min(Math.min(left, maxOps - used), limit);
                if (!quantumProvider && want <= 1) continue;

                var batch = extractBatch(details, inventory, level, want, energyService);
                if (batch == null) {
                    continue taskLoop;
                }

                if (batch.crafts <= 0) {
                    break taskLoop;
                }

                boolean accepted;
                if (quantumProvider) {
                    accepted = ((IQuantumCraftingProvider) provider).pushBatch(details, batch.crafts,
                            batch.outputs, batch.containerItems);
                } else {
                    accepted = provider.pushPattern(details, batch.inputs);
                }

                if (!accepted) {
                    CraftingCpuHelper.reinjectPatternInputs(inventory, batch.inputs);
                    continue;
                }

                energyService.extractAEPower(batch.power, Actionable.MODULATE, PowerMultiplier.CONFIG);
                used += (int) batch.crafts;

                var waitingFor = jobAccess.crazyae$getWaitingFor();
                var timeTracker = (AccessorElapsedTimeTracker) jobAccess.crazyae$getTimeTracker();
                for (var out : batch.outputs) {
                    waitingFor.insert(out.getKey(), out.getLongValue(), Actionable.MODULATE);
                }
                for (var container : batch.containerItems) {
                    waitingFor.insert(container.getKey(), container.getLongValue(), Actionable.MODULATE);
                    timeTracker.crazyae$addMaxItems(container.getLongValue(), container.getKey().getType());
                }
                cluster.markDirty();

                progress.crazyae$setValue(left - batch.crafts);

                if (quantumProvider) {
                    if (touched == null) touched = Collections.newSetFromMap(new IdentityHashMap<>());
                    touched.add((IQuantumCraftingProvider) provider);
                }

                if (progress.crazyae$getValue() <= 0) {
                    it.remove();
                    continue taskLoop;
                }
            }
        }

        if (touched != null) {
            for (var provider : touched) {
                provider.flushOutputs();
            }
        }

        return used;
    }

    private static boolean isQuantumCluster(CraftingCPUCluster cluster) {
        var it = cluster.getBlockEntities();
        return it.hasNext() && it.next() instanceof TileQuantumCPU;
    }

    private static boolean isBatchablePatternProvider(ICraftingProvider provider, IPatternDetails details) {
        if (!(provider instanceof PatternProviderLogic logic)) return false;
        if (details instanceof IMolecularAssemblerSupportedPattern) return false;
        if (!details.supportsPushInputsToExternalInventory()) return false;
        if (details instanceof AEProcessingPattern processing
                && processing.getSparseInputs().size() != details.getInputs().length) return false;
        if (logic.isBlocking()) return false;
        return logic.getConfigManager().getSetting(Settings.LOCK_CRAFTING_MODE) == LockCraftingMode.NONE;
    }

    private record Batch(long crafts, KeyCounter[] inputs, KeyCounter outputs, KeyCounter containerItems,
                         double power) {}

    @Nullable
    private static Batch extractBatch(IPatternDetails details, ListCraftingInventory inventory, Level level,
                                      long want, IEnergyService energyService) {
        long crafts = Math.min(want, estimateMaxCrafts(details, inventory, level, want));
        if (crafts <= 0) return null;

        for (int attempt = 0; attempt < 2; attempt++) {
            Batch batch = null;
            while (crafts > 0) {
                batch = tryExtract(details, inventory, level, crafts);
                if (batch != null) break;
                crafts /= 2;
            }
            if (batch == null) return null;

            double available = energyService.extractAEPower(batch.power, Actionable.SIMULATE, PowerMultiplier.CONFIG);
            if (available >= batch.power - 0.01) {
                return batch;
            }

            CraftingCpuHelper.reinjectPatternInputs(inventory, batch.inputs);
            crafts = batch.power <= 0 ? 0 : (long) Math.floor(batch.crafts * (available / batch.power));
            if (crafts <= 0) {
                return new Batch(0, new KeyCounter[0], new KeyCounter(), new KeyCounter(), 0);
            }
        }

        return new Batch(0, new KeyCounter[0], new KeyCounter(), new KeyCounter(), 0);
    }

    private static long estimateMaxCrafts(IPatternDetails details, ICraftingInventory inv, Level level, long limit) {
        long max = limit;
        var outputsPerCraft = new KeyCounter();
        for (var output : details.getOutputs()) {
            if (output.amount() <= 0) return 0;
            long previous = outputsPerCraft.get(output.what());
            if (previous > Long.MAX_VALUE - output.amount()) return 0;
            outputsPerCraft.add(output.what(), output.amount());
        }
        for (var output : outputsPerCraft) {
            max = Math.min(max, Long.MAX_VALUE / output.getLongValue());
        }
        for (var input : details.getInputs()) {
            long multiplier = input.getMultiplier();
            if (multiplier <= 0) return 0;
            max = Math.min(max, Long.MAX_VALUE / multiplier);
            long needed = saturatedMul(max, multiplier);
            long units = 0;
            for (var template : CraftingCpuHelper.getValidItemTemplates(inv, input, level)) {
                long amount = Math.max(1, template.amount());
                long available = inv.extract(template.key(), saturatedMul(needed, amount), Actionable.SIMULATE) / amount;
                // Cap at the requested quantity instead of overflowing the estimate.
                units += Math.min(available, needed - units);
                if (units >= needed) break;
            }
            max = Math.min(max, units / multiplier);
            if (max <= 0) return 0;
        }
        return max;
    }

    @Nullable
    private static Batch tryExtract(IPatternDetails details, ListCraftingInventory inv, Level level, long crafts) {
        var inputs = details.getInputs();
        var holder = new KeyCounter[inputs.length];
        for (int i = 0; i < holder.length; i++) holder[i] = new KeyCounter();
        var outputs = new KeyCounter();
        var containers = new KeyCounter();
        boolean found = true;

        for (int x = 0; x < inputs.length; x++) {
            var list = holder[x];
            long remainingMultiplier = saturatedMul(inputs[x].getMultiplier(), crafts);

            for (var template : CraftingCpuHelper.getValidItemTemplates(inv, inputs[x], level)) {
                long safeMultiplier = Math.min(remainingMultiplier, Long.MAX_VALUE / template.amount());
                long extracted = CraftingCpuHelper.extractTemplates(inv, template, safeMultiplier);
                list.add(template.key(), extracted * template.amount());

                var containerItem = inputs[x].getRemainingKey(template.key());
                if (containerItem != null) {
                    if (containers.get(containerItem) > Long.MAX_VALUE - extracted) {
                        found = false;
                        break;
                    }
                    containers.add(containerItem, extracted);
                }

                remainingMultiplier -= extracted;
                if (remainingMultiplier == 0) break;
            }

            if (!found || remainingMultiplier > 0) {
                found = false;
                break;
            }
        }

        if (!found) {
            CraftingCpuHelper.reinjectPatternInputs(inv, holder);
            return null;
        }

        for (var out : details.getOutputs()) {
            outputs.add(out.what(), out.amount() * crafts);
        }

        return new Batch(crafts, holder, outputs, containers, CraftingCpuHelper.calculatePatternPower(holder));
    }

    private static long saturatedMul(long a, long b) {
        long hi = Math.multiplyHigh(a, b);
        long lo = a * b;
        if ((hi == 0 && lo >= 0) || (hi == -1 && lo < 0)) return lo;
        return Long.MAX_VALUE;
    }
}
