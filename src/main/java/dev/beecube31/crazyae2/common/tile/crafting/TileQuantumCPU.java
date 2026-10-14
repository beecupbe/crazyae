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
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.interfaces.crafting.IQuantumCraftingProvider;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private boolean patternsDirty = true;
    private boolean providerUpdateRequired = false;
    private boolean outputBlocked = false;
    private int priority = 1;

    private final IActionSource actionSource = IActionSource.ofMachine(this);

    public TileQuantumCPU(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, UNIT_SLOTS, IDLE_POWER);
        this.getMainNode().addService(ICraftingProvider.class, this);
    }

    public CrazyAEInternalInv getPatternsInv() {
        return patternsInv;
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
                || !getAvailablePatterns().contains(details)) {
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
        if (crafts <= 0 || !getMainNode().isActive() || !getAvailablePatterns().contains(details)) {
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
    public void onChangeInventory(InternalInventory inv, int slot) {
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
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        patternsInv.writeToNBT(data, NBT_PATTERNS);
        data.putInt(NBT_PRIORITY, priority);

        if (!pendingOutputs.isEmpty()) {
            var list = new ListTag();
            for (var entry : pendingOutputs) {
                list.add(GenericStack.writeTag(new GenericStack(entry.getKey(), entry.getLongValue())));
            }
            data.put(NBT_PENDING, list);
        }
    }

    @Override
    public void loadTag(CompoundTag data) {
        patternsInv.readFromNBT(data, NBT_PATTERNS);
        if (data.contains(NBT_PRIORITY)) {
            priority = data.getInt(NBT_PRIORITY);
        }

        pendingOutputs.clear();
        var list = data.getList(NBT_PENDING, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            var stack = GenericStack.readTag(list.getCompound(i));
            if (stack != null) {
                pendingOutputs.add(stack.what(), stack.amount());
            }
        }

        patternsDirty = true;
        super.loadTag(data);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var stack : patternsInv) {
            if (!stack.isEmpty()) drops.add(stack);
        }
        for (var entry : pendingOutputs) {
            if (entry.getKey() instanceof AEItemKey itemKey) {
                long amount = entry.getLongValue();
                int max = itemKey.getItem().getMaxStackSize();
                while (amount > 0) {
                    int count = (int) Math.min(max, amount);
                    drops.add(itemKey.toStack(count));
                    amount -= count;
                }
            }
        }
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
