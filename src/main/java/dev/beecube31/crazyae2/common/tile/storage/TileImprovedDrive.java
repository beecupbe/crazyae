package dev.beecube31.crazyae2.common.tile.storage;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.blockentity.inventory.AppEngCellInventory;
import appeng.core.AELog;
import appeng.helpers.IPriorityHost;
import appeng.me.storage.DriveWatcher;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.enums.DriveSlotState;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

public class TileImprovedDrive extends AENetworkedInvBlockEntity implements IChestOrDrive, IPriorityHost, IStorageProvider {
    private final AppEngCellInventory inv = new AppEngCellInventory(this, getCellCount());
    private final DriveWatcher[] invBySlot = new DriveWatcher[getCellCount()];
    private boolean isCached = false;
    private int priority = 0;
    private boolean wasOnline = false;
    private final Item[] clientSideCellItems = new Item[getCellCount()];
    private final CellState[] clientSideCellState = new CellState[getCellCount()];
    private boolean clientSideOnline;

    public TileImprovedDrive(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        getMainNode().addService(IStorageProvider.class, this).setFlags(GridFlags.REQUIRE_CHANNEL);
        this.inv.setFilter(new CellValidInventoryFilter());

        Arrays.fill(clientSideCellState, CellState.ABSENT);
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        updateClientSideState();

        data.writeBoolean(clientSideOnline);
        for (int i = 0; i < getCellCount(); i++) {
            data.writeByte(clientSideCellState[i].ordinal());
        }

        for (int i = 0; i < getCellCount(); i++) {
            data.writeVarInt(BuiltInRegistries.ITEM.getId(getCellItem(i)));
        }
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);

        data.putBoolean("online", isPowered());

        for (int i = 0; i < getCellCount(); i++) {
            var cellItem = getCellItem(i);
            if (cellItem != null) {
                var cellData = new CompoundTag();
                cellData.putString("id", BuiltInRegistries.ITEM.getKey(cellItem).toString());

                var cellState = getCellStatus(i);
                cellData.putString("state", cellState.name().toLowerCase(Locale.ROOT));
                data.put("cell" + i, cellData);
            }
        }
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);

        var online = data.readBoolean();
        if (clientSideOnline != online) {
            clientSideOnline = online;
            changed = true;
        }

        var states = CellState.values();
        for (int i = 0; i < getCellCount(); i++) {
            var cellState = states[Math.min(data.readUnsignedByte(), states.length - 1)];
            if (clientSideCellState[i] != cellState) {
                clientSideCellState[i] = cellState;
                changed = true;
            }
        }

        for (int i = 0; i < getCellCount(); i++) {
            var itemId = data.readVarInt();
            Item item = itemId == 0 ? null : BuiltInRegistries.ITEM.byId(itemId);
            if (itemId != 0 && item == Items.AIR) {
                AELog.warn("Received unknown item id from server for disk drive %s: %d", this, itemId);
            }
            if (clientSideCellItems[i] != item) {
                clientSideCellItems[i] = item;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);

        clientSideOnline = data.getBoolean("online");

        for (int i = 0; i < getCellCount(); i++) {
            this.clientSideCellItems[i] = null;
            this.clientSideCellState[i] = CellState.ABSENT;

            var tagName = "cell" + i;
            if (data.contains(tagName, Tag.TAG_COMPOUND)) {
                var cellData = data.getCompound(tagName);
                var id = ResourceLocation.parse(cellData.getString("id"));
                var cellStateName = cellData.getString("state");

                clientSideCellItems[i] = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
                try {
                    clientSideCellState[i] = CellState.valueOf(cellStateName.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    AELog.warn("Cannot parse cell state for cell %d: %s", i, cellStateName);
                }
            }
        }
    }

    @Override
    public net.neoforged.neoforge.client.model.data.ModelData getModelData() {
        var states = new DriveSlotState[getCellCount()];
        for (int i = 0; i < states.length; i++) {
            states[i] = slotState(i);
        }
        return appeng.client.render.model.AEModelData.builder().with(DriveSlotState.SLOTS, states).build();
    }

    private DriveSlotState slotState(int slot) {
        var state = clientSideCellState[slot];
        if (state == null || state == CellState.ABSENT) {
            return DriveSlotState.EMPTY;
        }
        if (!clientSideOnline) {
            return DriveSlotState.OFFLINE;
        }
        return switch (state) {
            case EMPTY -> DriveSlotState.NO_CONTENTS;
            case NOT_EMPTY -> DriveSlotState.ONLINE;
            case TYPES_FULL -> DriveSlotState.TYPES_FULL;
            case FULL -> DriveSlotState.FULL;
            default -> DriveSlotState.EMPTY;
        };
    }

    @Override
    public int getCellCount() {
        return 35;
    }

    @Nullable
    @Override
    public Item getCellItem(int slot) {
        // Client-side we'll need to actually use the synced state
        if (level == null || level.isClientSide) {
            return clientSideCellItems[slot];
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot);
        if (!stackInSlot.isEmpty()) {
            return stackInSlot.getItem();
        }
        return null;
    }

    @Override
    public CellState getCellStatus(int slot) {
        if (isClientSide()) {
            return this.clientSideCellState[slot];
        }

        var handler = this.invBySlot[slot];
        if (handler == null) {
            return CellState.ABSENT;
        }

        return handler.getStatus();
    }

    @Override
    public @Nullable MEStorage getCellInventory(int slot) {
        return this.invBySlot[slot];
    }

    @Nullable
    @Override
    public StorageCell getOriginalCellInventory(int slot) {
        var handler = this.invBySlot[slot];
        if (handler != null) {
            return handler.getCell();
        }
        return null;
    }

    @Override
    public boolean isPowered() {
        if (isClientSide()) {
            return clientSideOnline;
        }

        return this.getMainNode().isOnline();
    }

    @Override
    public boolean isCellBlinking(int slot) {
        return false;
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.isCached = false;
        this.priority = data.getInt("priority");
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        data.putInt("priority", this.priority);
    }

    private void updateVisualStateIfNeeded() {
        if (updateClientSideState()) {
            this.markForUpdate();
        }
    }

    private boolean updateClientSideState() {
        if (isClientSide()) {
            return false;
        }

        updateState();

        var changed = false;
        var online = getMainNode().isOnline();
        if (online != this.clientSideOnline) {
            this.clientSideOnline = online;
            changed = true;
        }

        for (int x = 0; x < this.getCellCount(); x++) {
            var cellItem = getCellItem(x);
            if (cellItem != this.clientSideCellItems[x]) {
                this.clientSideCellItems[x] = cellItem;
                changed = true;
            }

            var cellState = this.getCellStatus(x);
            if (cellState != this.clientSideCellState[x]) {
                this.clientSideCellState[x] = cellState;
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var currentOnline = getMainNode().isOnline();
        if (this.wasOnline != currentOnline) {
            this.wasOnline = currentOnline;
            IStorageProvider.requestUpdate(getMainNode());
            updateVisualStateIfNeeded();
        }
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (this.isCached) {
            this.isCached = false;
            this.updateState();
        }

        IStorageProvider.requestUpdate(getMainNode());

        this.markForUpdate();
    }

    private void updateState() {
        if (!this.isCached) {
            double power = 2.0;
            for (int slot = 0; slot < this.inv.size(); slot++) {
                power += updateStateForSlot(slot);
            }
            this.getMainNode().setIdlePowerUsage(power);

            this.isCached = true;
        }
    }

    private double updateStateForSlot(int slot) {
        this.invBySlot[slot] = null;
        this.inv.setHandler(slot, null);

        var is = this.inv.getStackInSlot(slot);
        if (!is.isEmpty()) {
            var cell = StorageCells.getCellInventory(is, this::onCellContentChanged);

            if (cell != null) {
                this.inv.setHandler(slot, cell);

                var driveWatcher = new DriveWatcher(cell, () -> blinkCell(slot));
                this.invBySlot[slot] = driveWatcher;

                return cell.getIdleDrain();
            }
        }

        return 0;
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updateState();
    }

    @Override
    public void mountInventories(IStorageMounts storageMounts) {
        if (this.getMainNode().isOnline()) {
            this.updateState();
            for (var inventory : this.invBySlot) {
                if (inventory != null) {
                    storageMounts.mount(inventory, priority);
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
        this.saveChanges();

        this.isCached = false;
        this.updateState();

        IStorageProvider.requestUpdate(getMainNode());
    }

    private void blinkCell(int slot) {
        this.updateVisualStateIfNeeded();
    }

    private void onCellContentChanged() {
        this.level.blockEntityChanged(this.worldPosition);
    }

    private static class CellValidInventoryFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && StorageCells.isCellHandled(stack);
        }
    }


    public void openMenu(Player player) {
        ContainerOpener.open(CrazyContainers.IMPROVED_DRIVE.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ContainerOpener.returnTo(CrazyContainers.IMPROVED_DRIVE.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(CrazyBlocks.IMPROVED_DRIVE.get());
    }
}
