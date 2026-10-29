package dev.beecube31.crazyae2.common.tile.networking;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.HolderLookup;
import appeng.api.config.*;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.StorageHelper;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.me.helpers.MachineSource;
import appeng.menu.locator.MenuLocators;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import appeng.util.inv.filter.IAEItemFilter;

public class TileImprovedIOPort extends AENetworkedInvBlockEntity implements IUpgradeableObject, IConfigurableObject, IGridTickable {
    private static final int NUMBER_OF_CELL_SLOTS = 12;
    private static final int NUMBER_OF_UPGRADE_SLOTS = 3;

    private final ConfigManager manager;

    private final AppEngInternalInventory inputCells = new AppEngInternalInventory(this, NUMBER_OF_CELL_SLOTS);
    private final AppEngInternalInventory outputCells = new AppEngInternalInventory(this, NUMBER_OF_CELL_SLOTS);
    private final InternalInventory combinedInventory = new CombinedInternalInventory(this.inputCells, this.outputCells);

    private final InternalInventory inputCellsExt = new FilteredInternalInventory(this.inputCells, AEItemFilters.INSERT_ONLY);
    private final InternalInventory outputCellsExt = new FilteredInternalInventory(this.outputCells, AEItemFilters.EXTRACT_ONLY);

    private final IUpgradeInventory upgrades;
    private final IActionSource mySrc;
    private YesNo lastRedstoneState;

    private boolean isActive = false;

    public TileImprovedIOPort(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).addService(IGridTickable.class, this);
        this.manager = new ConfigManager(this::updateTask);
        this.manager.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.manager.registerSetting(Settings.FULLNESS_MODE, FullnessMode.EMPTY);
        this.manager.registerSetting(Settings.OPERATION_MODE, OperationMode.EMPTY);
        this.mySrc = new MachineSource(this);
        this.lastRedstoneState = YesNo.UNDECIDED;

        this.upgrades = UpgradeInventories.forMachine(CrazyBlocks.IMPROVED_IO_PORT.get(), NUMBER_OF_UPGRADE_SLOTS, this::saveChanges);
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        this.manager.writeToNBT(data);
        this.upgrades.writeToNBT(data, "upgrades");
        data.putInt("lastRedstoneState", this.lastRedstoneState.ordinal());
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.manager.readFromNBT(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.lastRedstoneState = YesNo.values()[data.getIntOr("lastRedstoneState", YesNo.UNDECIDED.ordinal())];
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isActive());
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean ret = super.readFromStream(data);

        final boolean isActive = data.readBoolean();
        ret = isActive != this.isActive || ret;
        this.isActive = isActive;

        return ret;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    private void updateTask() {
        getMainNode().ifPresent((grid, node) -> {
            if (this.hasWork()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });
    }

    public void updateRedstoneState() {
        final YesNo currentState = this.level.getBestNeighborSignal(this.worldPosition) != 0 ? YesNo.YES : YesNo.NO;
        if (this.lastRedstoneState != currentState) {
            this.lastRedstoneState = currentState;
            this.updateTask();
        }
    }

    private boolean getRedstoneState() {
        if (this.lastRedstoneState == YesNo.UNDECIDED) {
            this.updateRedstoneState();
        }

        return this.lastRedstoneState == YesNo.YES;
    }

    private boolean isEnabled() {
        if (!upgrades.isInstalled(AEItems.REDSTONE_CARD)) {
            return true;
        }

        final RedstoneMode rs = this.manager.getSetting(Settings.REDSTONE_CONTROLLED);
        if (rs == RedstoneMode.HIGH_SIGNAL) {
            return this.getRedstoneState();
        }
        return !this.getRedstoneState();
    }

    public boolean isActive() {
        if (level != null && !level.isClientSide()) {
            return this.getMainNode().isOnline();
        } else {
            return this.isActive;
        }
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            this.markForUpdate();
        }
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        } else if (id.equals(ISegmentedInventory.CELLS)) {
            return this.combinedInventory;
        } else {
            return super.getSubInventory(id);
        }
    }

    private boolean hasWork() {
        if (this.isEnabled()) {

            return !this.inputCells.isEmpty();
        }

        return false;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.combinedInventory;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (this.inputCells == inv) {
            this.updateTask();
        }
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        if (facing == this.getTop() || facing == this.getTop().getOpposite()) {
            return this.inputCellsExt;
        } else {
            return this.outputCellsExt;
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.IOPort, !this.hasWork());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!this.getMainNode().isActive()) {
            return TickRateModulation.IDLE;
        }

        TickRateModulation ret = TickRateModulation.SLEEP;
        long itemsToMove = 2048;

        switch (this.upgrades.getInstalledUpgrades(CrazyItems.STACKS_CARD.get())) {
            case 1 -> itemsToMove *= 64;
            case 2 -> itemsToMove *= 1024;
            case 3 -> itemsToMove *= 8192;
        }

        var grid = getMainNode().getGrid();
        if (grid == null) {
            return TickRateModulation.IDLE;
        }

        for (int x = 0; x < NUMBER_OF_CELL_SLOTS; x++) {
            var cell = this.inputCells.getStackInSlot(x);

            var cellInv = StorageCells.getCellInventory(cell, null);

            if (cellInv == null) {
                moveSlot(x);
                continue;
            }

            if (itemsToMove > 0) {
                itemsToMove = transferContents(grid, cellInv, itemsToMove);

                if (itemsToMove > 0) {
                    ret = TickRateModulation.IDLE;
                } else {
                    ret = TickRateModulation.URGENT;
                }
            }

            if (itemsToMove > 0 && matchesFullnessMode(cellInv) && this.moveSlot(x)) {
                ret = TickRateModulation.URGENT;
            }
        }

        return ret;
    }
    public boolean matchesFullnessMode(StorageCell inv) {
        return switch (manager.getSetting(Settings.FULLNESS_MODE)) {
            case HALF -> true;
            case EMPTY -> inv.getStatus() == CellState.EMPTY;
            case FULL -> inv.getStatus() == CellState.FULL;
        };
    }

    private long transferContents(IGrid grid, StorageCell cellInv, long itemsToMove) {
        var networkInv = grid.getStorageService().getInventory();
        KeyCounter srcList;
        MEStorage src, destination;
        if (this.manager.getSetting(Settings.OPERATION_MODE) == OperationMode.EMPTY) {
            src = cellInv;
            srcList = cellInv.getAvailableStacks();
            destination = networkInv;
        } else {
            src = networkInv;
            srcList = grid.getStorageService().getCachedInventory();
            destination = cellInv;
        }

        var energy = grid.getEnergyService();
        boolean didStuff;

        do {
            didStuff = false;

            for (var srcEntry : srcList) {
                var totalStackSize = srcEntry.getLongValue();
                if (totalStackSize > 0) {
                    var what = srcEntry.getKey();
                    var possible = destination.insert(what, totalStackSize, Actionable.SIMULATE, this.mySrc);

                    if (possible > 0) {
                        possible = Math.min(possible, itemsToMove * what.getAmountPerOperation());

                        possible = src.extract(what, possible, Actionable.MODULATE, this.mySrc);
                        if (possible > 0) {
                            var inserted = StorageHelper.poweredInsert(energy, destination, what, possible, this.mySrc);

                            if (inserted < possible) {
                                src.insert(what, possible - inserted, Actionable.MODULATE, this.mySrc);
                            }

                            if (inserted > 0) {
                                itemsToMove -= Math.max(1, inserted / what.getAmountPerOperation());
                                didStuff = true;
                            }

                            break;
                        }
                    }
                }
            }
        } while (itemsToMove > 0 && didStuff);

        return itemsToMove;
    }

    private boolean moveSlot(int x) {
        if (this.outputCells.addItems(this.inputCells.getStackInSlot(x)).isEmpty()) {
            this.inputCells.setItemDirect(x, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }

    public void openMenu(Player player) {
        ContainerOpener.open(CrazyContainers.IMPROVED_IO_PORT.get(), player, MenuLocators.forBlockEntity(this));
    }

    private static class StorageCellInsertOnlyFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return false;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && StorageCells.isCellHandled(stack);
        }
    }
}
