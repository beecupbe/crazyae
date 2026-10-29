package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.interfaces.crafting.IQuantumCraftingProvider;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import dev.beecube31.crazyae2.mixins.crafting.AccessorCraftingCpuLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TileQuantumCPU extends TileCraftingUnitsCombiner implements IQuantumCraftingProvider, ISubMenuHost {
    public static final int UNIT_SLOTS = 18;
    public static final int PATTERN_SLOTS = 1024;
    public static final double IDLE_POWER = 4096.0;

    private static final String NBT_PATTERNS = "patterns";
    private static final String NBT_PENDING = "pendingOutputs";
    private static final String NBT_PRIORITY = "priority";

    private final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, PATTERN_SLOTS, 1, new PatternFilter());

    private final KeyCounter pendingOutputs = new KeyCounter();

    private List<IPatternDetails> patterns = Collections.emptyList();
    private Set<IPatternDetails> patternSet = Collections.emptySet();
    private boolean patternsDirty = true;
    private boolean providerUpdateRequired = false;
    private boolean outputBlocked = false;
    private int priority = 1;
    private boolean contentsRecovered;

    private final IActionSource actionSource = IActionSource.ofMachine(this);

    public TileQuantumCPU(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, UNIT_SLOTS, IDLE_POWER);
        this.getMainNode().addService(ICraftingProvider.class, this);
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
        if (contentsRecovered || !(details instanceof IMolecularAssemblerSupportedPattern) || !getMainNode().isActive()
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
        if (contentsRecovered || crafts <= 0 || !getMainNode().isActive() || !hasPattern(details)) {
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

        for (var entry : pendingOutputs) {
            long amount = entry.getLongValue();
            if (amount <= 0) continue;

            long inserted = storage.insert(entry.getKey(), amount, Actionable.MODULATE, actionSource);
            entry.setValue(amount - inserted);
            if (inserted < amount) {
                blocked = true;
            }
        }

        pendingOutputs.removeZeros();
        this.outputBlocked = blocked;
        saveChanges();
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var result = super.tickingRequest(node, ticksSinceLastCall);

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

        return result;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (inv == patternsInv) {
            this.patternsDirty = true;
            this.providerUpdateRequired = true;
            saveChanges();
            return;
        }
        super.onChangeInventory(inv, slot);
    }

    @Override
    public void onReady() {
        super.onReady();
        this.patternsDirty = true;
        this.providerUpdateRequired = true;
    }

    @Override
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
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        patternsInv.writeToNBT(data, NBT_PATTERNS, registries);
        data.putInt(NBT_PRIORITY, priority);

        if (!pendingOutputs.isEmpty()) {
            var list = new ListTag();
            for (var entry : pendingOutputs) {
                list.add(GenericStack.writeTag(registries, new GenericStack(entry.getKey(), entry.getLongValue())));
            }
            data.put(NBT_PENDING, list);
        }
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        patternsInv.readFromNBT(data, NBT_PATTERNS, registries);
        if (data.contains(NBT_PRIORITY)) {
            priority = data.getInt(NBT_PRIORITY);
        }

        pendingOutputs.clear();
        var list = data.getList(NBT_PENDING, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            var stack = GenericStack.readTag(registries, list.getCompound(i));
            if (stack != null) {
                pendingOutputs.add(stack.what(), stack.amount());
            }
        }

        patternsDirty = true;
        super.loadTag(data, registries);
    }

    public void recoverContents(@Nullable Player breaker) {
        if (level == null || level.isClientSide() || contentsRecovered) return;
        contentsRecovered = true;

        var held = new ArrayList<KeyCounter>();
        var installed = new KeyCounter();
        var cluster = getCluster();
        if (cluster != null) {
            ListCraftingInventory crafting = ((AccessorCraftingCpuLogic) cluster.craftingLogic)
                    .crazyae$getInventory();
            var snapshot = new KeyCounter();
            snapshot.addAll(crafting.list);
            held.add(snapshot);
            crafting.clear();
        } else if (getPreviousState() != null) {
            var stored = new ListCraftingInventory(key -> {});
            stored.readFromNBT(getPreviousState().getList("inventory", Tag.TAG_COMPOUND),
                    level.registryAccess());
            held.add(stored.list);
        }

        setPreviousState(null);
        var pending = new KeyCounter();
        pending.addAll(pendingOutputs);
        held.add(pending);
        pendingOutputs.clear();
        takeInstalledInventory(patternsInv, installed);
        takeInstalledInventory(getAcceleratorsInv(), installed);
        takeInstalledInventory(getStorageInv(), installed);
        held.add(installed);
        patterns = Collections.emptyList();
        patternSet = Collections.emptySet();
        outputBlocked = false;
        if (cluster != null) cluster.cancelJob();

        var grid = getMainNode().getGrid();
        var storage = grid == null ? null : grid.getStorageService().getInventory();
        long returnedItems = 0;
        long returnedOther = 0;
        for (var source : held) for (var entry : source) {
            long amount = Math.max(0, entry.getLongValue());
            long inserted = storage == null || amount == 0 ? 0
                    : storage.insert(entry.getKey(), amount, Actionable.MODULATE, actionSource);
            inserted = Math.max(0, Math.min(amount, inserted));
            entry.setValue(amount - inserted);
            if (entry.getKey() instanceof AEItemKey) returnedItems = recoveryAdd(returnedItems, inserted);
            else returnedOther = recoveryAdd(returnedOther, inserted);
        }

        long budget = CrazyAEConfig.COMMON.quantumCpuMaxDropItemsOnBreak.get();
        long dropped = 0;
        long lostItems = 0;
        long lostOther = 0;
        boolean dropsEnabled = level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);

        for (var source : held) for (var entry : source) {
            long remaining = entry.getLongValue();
            if (remaining <= 0) continue;
            if (entry.getKey() instanceof AEItemKey key) {
                long toDrop = dropsEnabled ? Math.min(remaining, budget) : 0;
                budget -= toDrop;
                long spawned = 0;
                while (toDrop > 0) {
                    int count = (int) Math.clamp(key.getMaxStackSize(), 1, toDrop);
                    var entity = new ItemEntity(level,
                            worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                            worldPosition.getZ() + 0.5, key.toStack(count));
                    entity.setDefaultPickUpDelay();
                    if (level.addFreshEntity(entity)) spawned += count;
                    toDrop -= count;
                }
                dropped = recoveryAdd(dropped, spawned);
                lostItems = recoveryAdd(lostItems, remaining - spawned);
            } else {
                lostOther = recoveryAdd(lostOther, remaining);
            }
        }
        held.clear();
        if (cluster != null) cluster.destroy();
        saveChanges();

        if (dropped > 0 || lostItems > 0 || lostOther > 0) {
            if (breaker != null) {
                breaker.displayClientMessage(Component.translatable("message.crazyae.quantum_cpu.recovery_warning",
                        returnedItems, dropped, lostItems, returnedOther, lostOther), false);
            } else {
                CrazyAE.logger().warn(
                        "Quantum CPU recovery at {}: ME items={}, dropped={}, discarded items={}, ME non-item units={}, discarded non-item units={}",
                        worldPosition, returnedItems, dropped, lostItems, returnedOther, lostOther);
            }
        }
    }

    private static long recoveryAdd(long a, long b) {
        return b > Long.MAX_VALUE - a ? Long.MAX_VALUE : a + b;
    }

    private static void takeInstalledInventory(InternalInventory inventory, KeyCounter held) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            var stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                held.add(AEItemKey.of(stack), stack.getCount());
                inventory.setItemDirect(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public InteractionResult disassembleWithWrench(Player player, Level level,
                                                   BlockHitResult hit, ItemStack wrench) {
        // WrenchHook already checked permissions; AE2 otherwise collects inventory BEFORE onRemove.
        recoverContents(player);
        return super.disassembleWithWrench(player, level, hit, wrench);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        recoverContents(null);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        patternsInv.clear();
        pendingOutputs.clear();
    }

    private class PatternFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return isSupportedPattern(stack, level);
        }
    }
}
