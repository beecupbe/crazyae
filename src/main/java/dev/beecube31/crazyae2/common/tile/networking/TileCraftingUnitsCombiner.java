package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.MBCalculator;
import appeng.me.cluster.implementations.CraftingCPUCalculator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;


public class TileCraftingUnitsCombiner extends CraftingBlockEntity implements InternalInventoryHost, IGridTickable {
    public static final int SLOTS = 12;

    private static final long MAX_THREADS = 1L << 30;

    private final AppEngInternalInventory accelerators = new AppEngInternalInventory(this, SLOTS, 64);
    private final AppEngInternalInventory storage = new AppEngInternalInventory(this, SLOTS, 64);

    private final CraftingCPUCalculator ownCalc = new CraftingCPUCalculator(this);

    private long storageBytes;
    private int threads;

    private boolean recalculate = false;
    private boolean preservingClusterState;

    public TileCraftingUnitsCombiner(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.getMainNode().setIdlePowerUsage(1024.0);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().addService(IGridTickable.class, this);
        this.accelerators.setFilter(new UnitFilter(true));
        this.storage.setFilter(new UnitFilter(false));
    }

    @Nullable
    public static ICraftingUnitType unitTypeOf(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof AbstractCraftingUnitBlock<?> unit
                && unit.type != CrazyAECraftingUnitType.COMBINER) {
            return unit.type;
        }
        return null;
    }

    private boolean isBusy() {
        var cluster = getCluster();
        return cluster != null && cluster.isBusy();
    }

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode iGridNode) {
        return new TickingRequest(1, 1, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.recalculate) {
            long oldBytes = storageBytes;
            int oldThreads = threads;
            recalculate();
            saveChanges();
            if (oldBytes != storageBytes || oldThreads != threads) {
                rebuildCluster(true);
            }
        }

        this.recalculate = false;
        return TickRateModulation.SAME;
    }

    private class UnitFilter implements IAEItemFilter {
        private final boolean acceleratorSlots;

        UnitFilter(boolean acceleratorSlots) {
            this.acceleratorSlots = acceleratorSlots;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            if (isBusy()) {
                return false;
            }
            var type = unitTypeOf(stack);
            if (type == null) {
                return false;
            }
            return acceleratorSlots ? type.getAcceleratorThreads() > 0 : type.getStorageBytes() > 0;
        }

        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return !isBusy();
        }
    }

    private void recalculate() {
        long bytes = 0;
        long thr = 0;
        for (var stack : storage) {
            var type = unitTypeOf(stack);
            if (type != null) {
                bytes = saturatedAdd(bytes, saturatedMul(type.getStorageBytes(), stack.getCount()));
            }
        }
        for (var stack : accelerators) {
            var type = unitTypeOf(stack);
            if (type != null) {
                thr = Math.min(MAX_THREADS, thr + (long) type.getAcceleratorThreads() * stack.getCount());
            }
        }
        this.storageBytes = bytes;
        this.threads = (int) thr;
    }

    private static long saturatedAdd(long a, long b) {
        long r = a + b;
        return ((a ^ r) & (b ^ r)) < 0 ? Long.MAX_VALUE : r;
    }

    private static long saturatedMul(long a, long b) {
        long hi = Math.multiplyHigh(a, b);
        long lo = a * b;
        return (hi != 0 || lo < 0) ? Long.MAX_VALUE : lo;
    }

    @Override
    public long getStorageBytes() {
        return storageBytes;
    }

    @Override
    public int getAcceleratorThreads() {
        return threads;
    }

    public AppEngInternalInventory getAcceleratorsInv() {
        return accelerators;
    }

    public AppEngInternalInventory getStorageInv() {
        return storage;
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        this.saveChanges();
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.recalculate =  true;
    }

    @Override
    public void breakCluster() {
        if (!preservingClusterState) {
            super.breakCluster();
        }
    }

    public void rebuildCluster(boolean force) {
        if (!(level instanceof ServerLevel serverLevel) || isRemoved() || MBCalculator.isModificationInProgress()) {
            return;
        }

        var old = getCluster();
        if (old != null) {
            if (!force || old.isDestroyed()) {
                return;
            }

            var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, serverLevel.registryAccess());
            old.writeToNBT(output);
            setPreviousState(TagValueInput.create(ProblemReporter.DISCARDING, serverLevel.registryAccess(), output.buildResult()));
            setCoreBlock(true);
            preservingClusterState = true;
            try {
                old.destroy();
            } finally {
                preservingClusterState = false;
            }
        }

        var cluster = ownCalc.createCluster(serverLevel, worldPosition, worldPosition);
        MBCalculator.setModificationInProgress(cluster);
        try {
            ownCalc.updateBlockEntities(cluster, serverLevel, worldPosition, worldPosition);
            cluster.updateStatus(true);
        } finally {
            MBCalculator.setModificationInProgress(null);
        }

        getMainNode().ifPresent((grid, node) -> grid.postEvent(new GridCraftingCpuChange(node)));
    }

    @Override
    protected EnumSet<Direction> getConnections() {
        return EnumSet.noneOf(Direction.class);
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        accelerators.writeToNBT(data, "acceleratorsInv");
        storage.writeToNBT(data, "storageInv");
    }

    @Override
    public void loadTag(ValueInput data) {
        accelerators.readFromNBT(data, "acceleratorsInv");
        storage.readFromNBT(data, "storageInv");
        recalculate();
        super.loadTag(data);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var stack : accelerators) {
            if (!stack.isEmpty()) drops.add(stack);
        }
        for (var stack : storage) {
            if (!stack.isEmpty()) drops.add(stack);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        accelerators.clear();
        storage.clear();
    }
}