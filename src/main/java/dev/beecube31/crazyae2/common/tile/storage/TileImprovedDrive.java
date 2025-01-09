package dev.beecube31.crazyae2.common.tile.storage;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.AppEngCellInventory;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.interfaces.IChangeablePriorityHost;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.util.DriveWatcherImproved;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;
import java.util.*;

public class TileImprovedDrive extends AENetworkInvTile implements IChestOrDrive, IChangeablePriorityHost {


    private static final int CELL_COUNT = 35;
    private static final int BITS_PER_CELL = 3;
    private static final int CELLS_PER_INT = Integer.SIZE / BITS_PER_CELL;

    private final AppEngCellInventory inv = new AppEngCellInventory(this, CELL_COUNT);
    private final ICellHandler[] handlersBySlot = new ICellHandler[CELL_COUNT];
    private final DriveWatcherImproved<IAEItemStack>[] invBySlot = new DriveWatcherImproved[CELL_COUNT];
    private final IActionSource mySrc;
    private boolean isCached = false;
    private final Map<IStorageChannel<? extends IAEStack<?>>, List<IMEInventoryHandler>> inventoryHandlers;
    private int priority = 0;
    private boolean wasActive = false;

    private final int[] cellState = new int[ (int) Math.ceil((double) CELL_COUNT / CELLS_PER_INT)];
    private boolean powered;
    private int blinking;

    public TileImprovedDrive() {
        this.mySrc = new MachineSource(this);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.inv.setFilter(new CellValidInventoryFilter());
        this.inventoryHandlers = new IdentityHashMap<>();
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);

        for (int state : this.cellState) {
            data.writeInt(state);
        }

        data.writeBoolean(this.getProxy().isActive());
        data.writeInt(this.blinking);
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        final int[] oldCellState = Arrays.copyOf(this.cellState, this.cellState.length);
        final boolean oldPowered = this.powered;
        final int oldBlinking = this.blinking;

        for (int i = 0; i < this.cellState.length; i++) {
            this.cellState[i] = data.readInt();
        }

        this.powered = data.readBoolean();
        this.blinking = data.readInt();

        return !Arrays.equals(oldCellState, this.cellState) || oldPowered != this.powered || oldBlinking != this.blinking || c;
    }

    @Override
    public int getCellCount() {
        return CELL_COUNT;
    }

    @Override
    public int getCellStatus(final int slot) {
        if (Platform.isClient()) {
            int arrayIndex = slot / CELLS_PER_INT;
            int bitOffset = (slot % CELLS_PER_INT) * BITS_PER_CELL;
            return (this.cellState[arrayIndex] >> bitOffset) & 0b111;
        }

        final DriveWatcherImproved<IAEItemStack> handler = this.invBySlot[slot];
        if (handler == null) {
            return 0;
        }

        return handler.getStatus();
    }

    @Override
    public boolean isPowered() {
        if (Platform.isClient()) {
            return this.powered;
        }

        return this.getProxy().isActive();
    }

    @Override
    public boolean isCellBlinking(final int slot) {
        return (this.blinking & (1 << slot)) == 1;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.isCached = false;
        this.priority = data.getInteger("priority");

        if (data.hasKey("cellState")) {
            int[] loadedCellState = data.getIntArray("cellState");
            System.arraycopy(loadedCellState, 0, this.cellState, 0, Math.min(loadedCellState.length, this.cellState.length));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("priority", this.priority);
        data.setIntArray("cellState", this.cellState);
        return data;
    }

    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        this.recalculateDisplay();
    }

    private void recalculateDisplay() {
        final boolean currentActive = this.getProxy().isActive();
        final int[] oldCellState = Arrays.copyOf(this.cellState, this.cellState.length);
        final boolean oldPowered = this.powered;

        this.powered = currentActive;

        if (this.wasActive != currentActive) {
            this.wasActive = currentActive;
            try {
                this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
            } catch (final GridAccessException e) {
                // :P
            }
        }

        for (int x = 0; x < this.getCellCount(); x++) {
            int arrayIndex = x / CELLS_PER_INT;
            int bitOffset = (x % CELLS_PER_INT) * BITS_PER_CELL;
            int status = this.getCellStatus(x);

            int mask = ~(0b111 << bitOffset);
            this.cellState[arrayIndex] &= mask;

            this.cellState[arrayIndex] |= (status << bitOffset);
        }

        if (!Arrays.equals(oldCellState, this.cellState) || oldPowered != this.powered) {
            this.markForUpdate();
        }
    }

    @MENetworkEventSubscribe
    public void channelRender(final MENetworkChannelsChanged c) {
        this.recalculateDisplay();
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public IItemHandler getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.isCached) {
            this.isCached = false;
            this.updateState();
            this.recalculateDisplay();
        }

        try {
            if (this.getProxy().isActive()) {
                final IStorageGrid gs = this.getProxy().getStorage();
                Platform.postChanges(gs, removed, added, this.mySrc);
            }
            this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException ignored) {
        }

        this.markForUpdate();
    }

    private void updateState() {
        if (!this.isCached) {
            final Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels = AEApi.instance().storage().storageChannels();
            storageChannels.forEach(channel -> this.inventoryHandlers.put(channel, new ArrayList<>(CELL_COUNT)));

            double power = 2.0;

            for (int x = 0; x < this.inv.getSlots(); x++) {
                final ItemStack is = this.inv.getStackInSlot(x);
                this.invBySlot[x] = null;
                this.handlersBySlot[x] = null;

                if (!is.isEmpty()) {
                    this.handlersBySlot[x] = AEApi.instance().registries().cell().getHandler(is);

                    if (this.handlersBySlot[x] != null) {
                        for (IStorageChannel<? extends IAEStack<?>> channel : storageChannels) {

                            ICellInventoryHandler cell = this.handlersBySlot[x].getCellInventory(is, this, channel);

                            if (cell != null) {
                                this.inv.setHandler(x, cell);
                                power += this.handlersBySlot[x].cellIdleDrain(is, cell);

                                final DriveWatcherImproved<IAEItemStack> ih = new DriveWatcherImproved<IAEItemStack>(cell, is, this.handlersBySlot[x], this);
                                ih.setPriority(this.priority);
                                this.invBySlot[x] = ih;
                                this.inventoryHandlers.get(channel).add(ih);

                                break;
                            }
                        }
                    }
                }
            }

            this.getProxy().setIdlePowerUsage(power);

            this.isCached = true;
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updateState();
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(final IStorageChannel channel) {
        this.updateState();
        return this.inventoryHandlers.get(channel);
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(final int newValue) {
        this.priority = newValue;
        this.saveChanges();

        this.isCached = false;
        this.updateState();

        try {
            this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException e) {
            // :P
        }
    }

    @Override
    public void blinkCell(final int slot) {
        this.blinking |= (1 << slot);
        this.recalculateDisplay();
    }

    @Override
    public void saveChanges(final ICellInventory<?> cellInventory) {
        this.world.markChunkDirty(this.pos, this);
    }

    private static class CellValidInventoryFilter implements IAEItemFilter {

        @Override
        public boolean allowExtract(IItemHandler inv, int slot, int amount) {
            return true;
        }

        @Override
        public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && AEApi.instance().registries().cell().isCellHandled(stack);
        }
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return AEApi.instance().definitions().blocks().drive().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public CrazyAEGuiBridge getGuiBridge() {
        return CrazyAEGuiBridge.IMPROVED_DRIVE;
    }
}
