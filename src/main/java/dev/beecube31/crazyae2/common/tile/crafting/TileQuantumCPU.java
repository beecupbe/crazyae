package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.storage.MEStorage;
import appeng.api.stacks.AEKey;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.block.crafting.ICraftingUnitType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.block.crafting.BlockCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.interfaces.crafting.IQuantumCraftingProvider;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileQuantumCPU extends TileCraftingUnitsCombiner implements IQuantumCraftingProvider, ISubMenuHost {
    public record RecoverySummary(long returned, long dropped, long discarded, long unrecovered) {
        private static final RecoverySummary EMPTY = new RecoverySummary(0, 0, 0, 0);
    }

    public static final int UNIT_SLOTS = 18;
    public static final int PATTERN_SLOTS = 1024;
    public static final double IDLE_POWER = 4096.0;

    private static final long MAX_THREADS = 1L << 30;
    private static final String NBT_ACCELERATORS = "acceleratorsInv";
    private static final String NBT_STORAGE = "storageInv";

    private static final String NBT_PATTERNS = "patterns";
    private static final String NBT_PENDING = "pendingOutputs";
    private static final String NBT_PRIORITY = "priority";

    private final AppEngInternalInventory acceleratorsInv =
            new AppEngInternalInventory(this, UNIT_SLOTS, 64, new UnitFilter(true));
    private final AppEngInternalInventory storageInv =
            new AppEngInternalInventory(this, UNIT_SLOTS, 64, new UnitFilter(false));
    private long storageBytes;
    private int acceleratorThreads;
    private boolean unitsDirty;
    private boolean loading;

    private final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, PATTERN_SLOTS, 1, new PatternFilter());

    private final KeyCounter pendingOutputs = new KeyCounter();

    private List<IPatternDetails> patterns = Collections.emptyList();
    private Set<IPatternDetails> patternSet = Collections.emptySet();
    private boolean patternsDirty = true;
    private boolean providerUpdateRequired = false;
    private boolean outputBlocked = false;
    private int priority = 1;

    private final IActionSource actionSource = IActionSource.ofMachine(this);
    private boolean recoveryStarted;
    private RecoverySummary recoverySummary = RecoverySummary.EMPTY;

    public TileQuantumCPU(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.getMainNode().setIdlePowerUsage(IDLE_POWER);
        this.getMainNode().addService(ICraftingProvider.class, this);
    }

    public int getUnitSlots() {
        return UNIT_SLOTS;
    }

    @Override
    public AppEngInternalInventory getAcceleratorsInv() {
        return acceleratorsInv;
    }

    @Override
    public AppEngInternalInventory getStorageInv() {
        return storageInv;
    }

    @Override
    public long getStorageBytes() {
        return storageBytes;
    }

    @Override
    public int getAcceleratorThreads() {
        return acceleratorThreads;
    }

    private boolean isCpuBusy() {
        var cluster = getCluster();
        return cluster != null && cluster.isBusy();
    }

    private static ICraftingUnitType quantumUnitTypeOf(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof BlockCraftingUnitsCombiner) {
            return null;
        }
        return unitTypeOf(stack);
    }

    private void recalculateUnits() {
        long bytes = 0;
        long threads = 0;
        for (var stack : storageInv) {
            var type = quantumUnitTypeOf(stack);
            if (type != null && type.getStorageBytes() > 0) {
                long perItem = type.getStorageBytes();
                int count = stack.getCount();
                bytes = perItem > (Long.MAX_VALUE - bytes) / count ? Long.MAX_VALUE : bytes + perItem * count;
            }
        }
        for (var stack : acceleratorsInv) {
            var type = quantumUnitTypeOf(stack);
            if (type != null && type.getAcceleratorThreads() > 0) {
                threads = Math.min(MAX_THREADS, threads + (long) type.getAcceleratorThreads() * stack.getCount());
            }
        }
        this.storageBytes = bytes;
        this.acceleratorThreads = (int) threads;
        this.unitsDirty = false;
    }

    public CrazyAEInternalInv getPatternsInv() {
        return patternsInv;
    }

    public int getPatternCount() {
        int count = 0;
        for (var stack : patternsInv) {
            if (!stack.isEmpty()) count++;
        }
        return count;
    }

    public static boolean isSupportedPattern(ItemStack stack, Level level) {
        if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) {
            return false;
        }
        if (level == null) {
            return true;
        }
        return PatternDetailsHelper.decodePattern(stack, level) instanceof IMolecularAssemblerSupportedPattern;
    }

    private void rebuildPatterns() {
        if (level == null) {
            return;
        }

        var list = new ArrayList<IPatternDetails>();
        for (var stack : patternsInv) {
            if (stack.isEmpty()) continue;
            var details = PatternDetailsHelper.decodePattern(stack, level);
            if (details instanceof IMolecularAssemblerSupportedPattern) {
                list.add(details);
            }
        }

        this.patterns = list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list);
        this.patternSet = list.isEmpty() ? Collections.emptySet() : new HashSet<>(list);
        this.patternsDirty = false;
    }

    public ItemStack acceptPattern(ItemStack pattern) {
        if (!isSupportedPattern(pattern, level)) {
            return pattern;
        }
        return patternsInv.addItems(pattern);
    }

    public boolean hasFreePatternSlot() {
        for (var stack : patternsInv) {
            if (stack.isEmpty()) return true;
        }
        return false;
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        if (patternsDirty) {
            rebuildPatterns();
        }
        return patterns;
    }

    private boolean hasPattern(IPatternDetails details) {
        if (patternsDirty) {
            rebuildPatterns();
        }
        return patternSet.contains(details);
    }

    @Override
    public int getPatternPriority() {
        return priority;
    }

    @Override
    public boolean isBusy() {
        return outputBlocked && !pendingOutputs.isEmpty();
    }

    @Override
    public boolean pushPattern(IPatternDetails details, KeyCounter[] inputHolder) {
        if (!(details instanceof IMolecularAssemblerSupportedPattern) || !getMainNode().isActive()
                || !hasPattern(details)) {
            return false;
        }

        for (var out : details.getOutputs()) {
            pendingOutputs.add(out.what(), out.amount());
        }

        var inputs = details.getInputs();
        for (int i = 0; i < inputs.length && i < inputHolder.length; i++) {
            var possible = inputs[i].getPossibleInputs();
            long unit = possible.length > 0 ? Math.max(1, possible[0].amount()) : 1;
            for (var entry : inputHolder[i]) {
                var remaining = inputs[i].getRemainingKey(entry.getKey());
                if (remaining != null) {
                    pendingOutputs.add(remaining, entry.getLongValue() / unit);
                }
            }
        }

        saveChanges();
        return true;
    }

    @Override
    public boolean pushBatch(IPatternDetails details, long crafts, KeyCounter expectedOutputs,
                             KeyCounter expectedContainerItems) {
        if (crafts <= 0 || !(details instanceof IMolecularAssemblerSupportedPattern)
                || !getMainNode().isActive() || !hasPattern(details)) {
            return false;
        }

        pendingOutputs.addAll(expectedOutputs);
        pendingOutputs.addAll(expectedContainerItems);
        saveChanges();
        return true;
    }

    @Override
    public void flushOutputs() {
        if (pendingOutputs.isEmpty()) {
            outputBlocked = false;
            return;
        }

        var grid = getMainNode().getGrid();
        if (grid == null || !getMainNode().isActive()) {
            outputBlocked = true;
            return;
        }

        var storage = grid.getStorageService().getInventory();
        boolean blocked = false;
        boolean changed = false;

        for (var entry : pendingOutputs) {
            long amount = entry.getLongValue();
            if (amount <= 0) continue;

            long inserted = storage.insert(entry.getKey(), amount, Actionable.MODULATE, actionSource);
            entry.setValue(amount - inserted);
            changed |= inserted > 0;
            if (inserted < amount) {
                blocked = true;
            }
        }

        pendingOutputs.removeZeros();
        this.outputBlocked = blocked;
        if (changed) {
            saveChanges();
        }
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (recoveryStarted) {
            return TickRateModulation.SLEEP;
        }

        if (unitsDirty) {
            long oldBytes = storageBytes;
            int oldThreads = acceleratorThreads;
            recalculateUnits();
            saveChanges();
            if (oldBytes != storageBytes || oldThreads != acceleratorThreads) {
                rebuildCluster(true);
            }
        }

        if (patternsDirty) {
            rebuildPatterns();
            providerUpdateRequired = true;
        }

        if (providerUpdateRequired) {
            providerUpdateRequired = false;
            ICraftingProvider.requestUpdate(getMainNode());
        }

        if (!pendingOutputs.isEmpty()) {
            flushOutputs();
        }

        return TickRateModulation.SAME;
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        if (!loading && !recoveryStarted) {
            saveChanges();
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (loading || recoveryStarted) {
            return;
        }
        if (inv == patternsInv) {
            this.patternsDirty = true;
            this.providerUpdateRequired = true;
            saveChanges();
            return;
        }
        if (inv == acceleratorsInv || inv == storageInv) {
            this.unitsDirty = true;
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        this.patternsDirty = true;
        this.providerUpdateRequired = true;
        rebuildCluster(false);
    }

    public void openMenu(Player player) {
        ContainerOpener.open(CrazyContainers.QUANTUM_CPU.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ContainerOpener.returnTo(CrazyContainers.QUANTUM_CPU.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(CrazyBlocks.QUANTUM_CPU.get());
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);

        acceleratorsInv.writeToNBT(data, NBT_ACCELERATORS);
        storageInv.writeToNBT(data, NBT_STORAGE);
        patternsInv.writeToNBT(data, NBT_PATTERNS);
        data.putInt(NBT_PRIORITY, priority);

        var list = data.childrenList(NBT_PENDING);
        for (var entry : pendingOutputs) {
            if (entry.getLongValue() > 0) {
                GenericStack.writeTag(list.addChild(), new GenericStack(entry.getKey(), entry.getLongValue()));
            }
        }
    }

    @Override
    public void loadTag(ValueInput data) {
        loading = true;
        try {
            acceleratorsInv.clear();
            storageInv.clear();
            patternsInv.clear();
            super.getAcceleratorsInv().clear();
            super.getStorageInv().clear();

            acceleratorsInv.readFromNBT(data, NBT_ACCELERATORS);
            storageInv.readFromNBT(data, NBT_STORAGE);
            recalculateUnits();
            patternsInv.readFromNBT(data, NBT_PATTERNS);
            priority = data.getIntOr(NBT_PRIORITY, 1);

            pendingOutputs.clear();
            for (var child : data.childrenListOrEmpty(NBT_PENDING)) {
                var stack = GenericStack.readTag(child);
                if (stack != null && stack.amount() > 0) {
                    pendingOutputs.add(stack.what(), stack.amount());
                }
            }
            outputBlocked = !pendingOutputs.isEmpty();
            patternsDirty = true;
            providerUpdateRequired = true;

            super.loadTag(data);
            super.getAcceleratorsInv().clear();
            super.getStorageInv().clear();
        } finally {
            loading = false;
        }
    }

    public RecoverySummary recoverContents(@Nullable Player player) {
        if (level == null || level.isClientSide() || recoveryStarted) {
            return recoverySummary;
        }
        recoveryStarted = true;

        var result = new RecoveryAccumulator();
        var cluster = getCluster();
        IGrid grid = cluster != null ? cluster.getGrid() : null;
        if (grid == null) {
            grid = getMainNode().getGrid();
        }
        var storageService = grid == null ? null : grid.getStorageService().getInventory();

        var held = new ArrayList<KeyCounter>();
        if (cluster != null) {
            var cpuInventory = cluster.craftingLogic.getInventory().list;
            var snapshot = new KeyCounter();
            snapshot.addAll(cpuInventory);
            held.add(snapshot);
            cpuInventory.clear();
        } else if (getPreviousState() != null) {
            var snapshot = new KeyCounter();
            for (var child : getPreviousState().childrenListOrEmpty("inventory")) {
                var key = AEKey.fromTagGeneric(child);
                long amount = child.getLongOr("#", 0);
                if (key != null && amount > 0) snapshot.add(key, amount);
            }
            held.add(snapshot);
        }

        setPreviousState(null);
        var pending = new KeyCounter();
        pending.addAll(pendingOutputs);
        held.add(pending);
        pendingOutputs.clear();
        var installed = new KeyCounter();
        detachInventory(acceleratorsInv, installed);
        detachInventory(storageInv, installed);
        detachInventory(patternsInv, installed);
        held.add(installed);
        if (cluster != null) {
            cluster.cancelJob();
        }

        for (var source : held) {
            for (var entry : source) {
                long amount = entry.getLongValue();
                if (amount <= 0) continue;
                long inserted = insert(entry.getKey(), amount, storageService);
                if (inserted < 0) {
                    if (entry.getKey() instanceof AEItemKey) {
                        result.discarded = addSaturated(result.discarded, amount);
                    } else {
                        result.unrecovered = addSaturated(result.unrecovered, amount);
                    }
                    entry.setValue(0);
                } else {
                    result.returned = addSaturated(result.returned, inserted);
                    entry.setValue(amount - inserted);
                }
            }
        }
        for (var source : held) {
            for (var entry : source) {
                recoverRemainder(entry.getKey(), entry.getLongValue(), result);
            }
        }

        saveChanges();
        outputBlocked = false;
        patternsDirty = true;
        providerUpdateRequired = false;
        unitsDirty = false;

        recoverySummary = new RecoverySummary(result.returned, result.dropped, result.discarded, result.unrecovered);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer
                && (result.dropped > 0 || result.discarded > 0 || result.unrecovered > 0)) {
            serverPlayer.sendSystemMessage(Component.translatable("chat.crazyae.quantum_cpu.recovery",
                    result.returned, result.dropped, result.discarded, result.unrecovered));
        }
        return recoverySummary;
    }

    private void detachInventory(AppEngInternalInventory inventory, KeyCounter held) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            var stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                held.add(AEItemKey.of(stack), stack.getCount());
                inventory.setItemDirect(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void rebuildCluster(boolean force) {
        if (!recoveryStarted) {
            super.rebuildCluster(force);
        }
    }

    private void recoverRemainder(AEKey key, long remaining, RecoveryAccumulator result) {
        if (remaining <= 0) return;
        if (key instanceof AEItemKey itemKey) {
            long dropped = dropStack(itemKey.toStack(1), remaining, result);
            result.dropped = addSaturated(result.dropped, dropped);
            result.discarded = addSaturated(result.discarded, remaining - dropped);
        } else {
            result.unrecovered = addSaturated(result.unrecovered, remaining);
        }
    }

    private long insert(AEKey key, long amount, MEStorage storage) {
        if (storage == null || amount <= 0) return 0;
        try {
            long inserted = storage.insert(key, amount, Actionable.MODULATE, actionSource);
            return Math.max(0, Math.min(amount, inserted));
        } catch (RuntimeException exception) {
            CrazyAE.logger().error(
                    "Quantum CPU recovery insertion failed; suppressing ambiguous remainder drops at {}", worldPosition,
                    exception);
            return -1;
        }
    }

    private long dropStack(ItemStack stack, long amount, RecoveryAccumulator result) {
        if (stack.isEmpty() || amount <= 0 || !(level instanceof net.minecraft.server.level.ServerLevel serverLevel)
                || !serverLevel.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.BLOCK_DROPS)) return 0;
        long budget = Math.max(0, CrazyAEConfig.COMMON.quantumCpuMaxDropItemsOnBreak.get()) - result.dropped;
        long toDrop = Math.min(amount, Math.max(0, budget));
        long dropped = 0;
        int maxStack = Math.max(1, stack.getMaxStackSize());

        while (toDrop > 0) {
            int count = (int) Math.min(maxStack, toDrop);
            var part = stack.copy();
            part.setCount(count);
            var entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, part);
            entity.setDefaultPickUpDelay();
            if (!level.addFreshEntity(entity)) {
                break;
            }
            dropped += count;
            toDrop -= count;
        }
        return dropped;
    }

    private static long addSaturated(long a, long b) {
        return b > 0 && a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }

    @Override
    public InteractionResult disassembleWithWrench(Player player, Level level, BlockHitResult hitResult, ItemStack wrench) {
        recoverContents(player);
        return super.disassembleWithWrench(player, level, hitResult, wrench);
    }

    @Override
    public void preRemoveSideEffects(BlockPos blockPos, BlockState blockState) {
        recoverContents(null);
        super.preRemoveSideEffects(blockPos, blockState);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        recoverContents(null);
    }

    private static final class RecoveryAccumulator {
        private long returned;
        private long dropped;
        private long discarded;
        private long unrecovered;
    }

    @Override
    public void clearContent() {
        super.clearContent();
        acceleratorsInv.clear();
        storageInv.clear();
        recalculateUnits();
        patternsInv.clear();
        pendingOutputs.clear();
        outputBlocked = false;
        patternsDirty = true;
        providerUpdateRequired = true;
    }

    private class UnitFilter implements IAEItemFilter {
        private final boolean accelerator;

        private UnitFilter(boolean accelerator) {
            this.accelerator = accelerator;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            if (isCpuBusy()) {
                return false;
            }
            var type = quantumUnitTypeOf(stack);
            return type != null && (accelerator ? type.getAcceleratorThreads() > 0 : type.getStorageBytes() > 0);
        }

        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return !isCpuBusy();
        }
    }

    private class PatternFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return isSupportedPattern(stack, level);
        }
    }
}
