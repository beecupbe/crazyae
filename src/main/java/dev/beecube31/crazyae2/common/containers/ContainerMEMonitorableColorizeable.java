package dev.beecube31.crazyae2.common.containers;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.tiles.IMEChest;
import appeng.api.implementations.tiles.IViewCellStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.me.helpers.ChannelPowerSrc;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.List;

public class ContainerMEMonitorableColorizeable extends CrazyAEBaseContainer implements IConfigManagerHost, IConfigurableObject, IMEMonitorHandlerReceiver<IAEItemStack> {

    protected final SlotRestrictedInput[] cellView = new SlotRestrictedInput[5];
    private final IMEMonitor<IAEItemStack> monitor;
    public final IItemList<IAEItemStack> items = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
    private final IConfigManager clientCM;
    private final ITerminalHost host;
    @GuiSync(99)
    public boolean canAccessViewCells = false;
    @GuiSync(98)
    public boolean hasPower = false;
    private IConfigManagerHost gui;
    private IConfigManager serverCM;
    private IGridNode networkNode;
    protected int jeiOffset = Loader.isModLoaded("jei") ? 24 : 0;


    public ContainerMEMonitorableColorizeable(final InventoryPlayer ip, final ITerminalHost monitorable) {
        this(ip, monitorable, true);
    }

    protected ContainerMEMonitorableColorizeable(final InventoryPlayer ip, final ITerminalHost monitorable, final boolean bindInventory) {
        this(ip, monitorable, null, bindInventory);
    }

    protected ContainerMEMonitorableColorizeable(final InventoryPlayer ip, final ITerminalHost monitorable, final IGuiItemObject iGuiItemObject, final boolean bindInventory) {
        super(ip, monitorable instanceof TileEntity ? (TileEntity) monitorable : null, monitorable instanceof IPart ? (IPart) monitorable : null, iGuiItemObject);

        this.host = monitorable;
        this.clientCM = new ConfigManager(this);

        this.clientCM.registerSetting(Settings.SORT_BY, SortOrder.NAME);
        this.clientCM.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
        this.clientCM.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);

        if (Platform.isServer()) {
            this.serverCM = monitorable.getConfigManager();

            this.monitor = monitorable.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            if (this.monitor != null) {
                this.monitor.addListener(this, null);

                this.setCellInventory(this.monitor);

                if (monitorable instanceof IPortableCell) {
                    this.setPowerSource((IEnergySource) monitorable);
                    if (monitorable instanceof WirelessTerminalGuiObject) {
                        this.networkNode = ((WirelessTerminalGuiObject) monitorable).getActionableNode();
                    }
                } else if (monitorable instanceof IMEChest) {
                    this.setPowerSource((IEnergySource) monitorable);
                } else if (monitorable instanceof IGridHost || monitorable instanceof IActionHost) {
                    final IGridNode node;
                    if (monitorable instanceof IGridHost) {
                        node = ((IGridHost) monitorable).getGridNode(AEPartLocation.INTERNAL);
                    } else {
                        node = ((IActionHost) monitorable).getActionableNode();
                    }

                    if (node != null) {
                        this.networkNode = node;
                        final IGrid g = node.getGrid();
                        this.setPowerSource(new ChannelPowerSrc(this.networkNode, g.getCache(IEnergyGrid.class)));
                    }
                }
            } else {
                this.setValidContainer(false);
            }
        } else {
            this.monitor = null;
        }

        this.canAccessViewCells = false;
        if (monitorable instanceof IViewCellStorage) {
            for (int y = 0; y < 5; y++) {
                this.cellView[y] = new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.VIEW_CELL, ((IViewCellStorage) monitorable)
                        .getViewCellStorage(), y, 206, y * 18 + 8 + jeiOffset, this.getInventoryPlayer());
                this.cellView[y].setAllowEdit(this.canAccessViewCells);
                this.addSlotToContainer(this.cellView[y]);
            }
        }

        if (bindInventory) {
            this.bindPlayerInventory(ip, 0, 0);
        }
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer p, final int idx) {
        if (Platform.isClient()) {
            return ItemStack.EMPTY;
        }

        // Below logic is all about handling shift click for view cells
        if (!(this.host instanceof IViewCellStorage)) {
            return super.transferStackInSlot(p, idx);
        }

        // Is it a view cell?
        final Slot clickSlot = this.inventorySlots.get(idx);
        ItemStack itemStack = clickSlot.getStack();
        if (!AEApi.instance().definitions().items().viewCell().isSameAs(itemStack)) {
            return super.transferStackInSlot(p, idx);
        }

        // Are we clicking from the player's inventory?
        final boolean isPlayerInventorySlot = this.inventorySlots.get(idx) instanceof SlotPlayerInv || this.inventorySlots.get(idx) instanceof SlotPlayerHotBar;
        if (!isPlayerInventorySlot) {
            return super.transferStackInSlot(p, idx);
        }

        // Attempt to move the item into the view cell storage
        final IItemHandler viewCellInv = ((IViewCellStorage) this.host).getViewCellStorage();
        for (int slot = 0; slot < viewCellInv.getSlots(); slot++) {
            if (viewCellInv.isItemValid(slot, itemStack) && viewCellInv.getStackInSlot(slot).isEmpty()) {
                ItemStack remainder = viewCellInv.insertItem(slot, itemStack, true);
                if (!remainder.isEmpty()) { // That slot can't take the item
                    continue;
                }
                remainder = viewCellInv.insertItem(slot, itemStack, false);
                clickSlot.putStack(remainder);
                this.detectAndSendChanges();
                if (!remainder.isEmpty()) {
                    // How??
                    return super.transferStackInSlot(p, idx);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.transferStackInSlot(p, idx);
    }

    public IGridNode getNetworkNode() {
        return this.networkNode;
    }

    @Override
    public void detectAndSendChanges() {
        if (Platform.isServer()) {
            if (this.monitor != this.host.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))) {
                this.setValidContainer(false);
            }

            for (final Settings set : this.serverCM.getSettings()) {
                final Enum<?> sideLocal = this.serverCM.getSetting(set);
                final Enum<?> sideRemote = this.clientCM.getSetting(set);

                if (sideLocal != sideRemote) {
                    this.clientCM.putSetting(set, sideLocal);
                    for (final IContainerListener crafter : this.listeners) {
                        if (crafter instanceof EntityPlayerMP) {
                            try {
                                NetworkHandler.instance().sendTo(new PacketValueConfig(set.name(), sideLocal.name()), (EntityPlayerMP) crafter);
                            } catch (final IOException e) {
                                AELog.debug(e);
                            }
                        }
                    }
                }
            }

            if (!this.items.isEmpty()) {
                try {
                    final IItemList<IAEItemStack> monitorCache = this.monitor.getStorageList();

                    final PacketMEInventoryUpdate piu = new PacketMEInventoryUpdate();

                    for (final IAEItemStack is : this.items) {
                        final IAEItemStack send = monitorCache.findPrecise(is);
                        if (send == null) {
                            is.setStackSize(0);
                            piu.appendItem(is);
                        } else {
                            piu.appendItem(send);
                        }
                    }

                    if (!piu.isEmpty()) {
                        this.items.resetStatus();

                        for (final Object c : this.listeners) {
                            if (c instanceof EntityPlayer) {
                                NetworkHandler.instance().sendTo(piu, (EntityPlayerMP) c);
                            }
                        }
                    }
                } catch (final IOException e) {
                    AELog.debug(e);
                }
            }

            this.updatePowerStatus();

            final boolean oldAccessible = this.canAccessViewCells;
            this.canAccessViewCells = this.hasAccess(SecurityPermissions.BUILD, false);
            if (this.canAccessViewCells != oldAccessible) {
                for (int y = 0; y < 5; y++) {
                    if (this.cellView[y] != null) {
                        this.cellView[y].setAllowEdit(this.canAccessViewCells);
                    }
                }
            }

            super.detectAndSendChanges();
        }

    }

    protected void updatePowerStatus() {
        try {
            if (this.networkNode != null) {
                this.setPowered(this.networkNode.isActive());
            } else if (this.getPowerSource() instanceof IEnergyGrid) {
                this.setPowered(((IEnergyGrid) this.getPowerSource()).isNetworkPowered());
            } else {
                this.setPowered(this.getPowerSource().extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.8);
            }
        } catch (final Throwable t) {
            // :P
        }
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (field.equals("canAccessViewCells")) {
            for (int y = 0; y < 5; y++) {
                if (this.cellView[y] != null) {
                    this.cellView[y].setAllowEdit(this.canAccessViewCells);
                }
            }
        }

        super.onUpdate(field, oldValue, newValue);
    }

    @Override
    public void addListener(final @NotNull IContainerListener c) {
        super.addListener(c);

        this.queueInventory(c);
    }

    private void queueInventory(final IContainerListener c) {
        if (Platform.isServer() && c instanceof EntityPlayer && this.monitor != null) {
            try {
                PacketMEInventoryUpdate piu = new PacketMEInventoryUpdate();
                final IItemList<IAEItemStack> monitorCache = this.monitor.getStorageList();

                for (final IAEItemStack send : monitorCache) {
                    try {
                        piu.appendItem(send);
                    } catch (final BufferOverflowException boe) {
                        NetworkHandler.instance().sendTo(piu, (EntityPlayerMP) c);

                        piu = new PacketMEInventoryUpdate();
                        piu.appendItem(send);
                    }
                }

                NetworkHandler.instance().sendTo(piu, (EntityPlayerMP) c);
            } catch (final IOException e) {
                AELog.debug(e);
            }
        }
    }

    @Override
    public void removeListener(final @NotNull IContainerListener c) {
        super.removeListener(c);

        if (this.listeners.isEmpty() && this.monitor != null) {
            this.monitor.removeListener(this);
        }
    }

    @Override
    public void onContainerClosed(final @NotNull EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.monitor != null) {
            this.monitor.removeListener(this);
        }
    }

    @Override
    public boolean isValid(final Object verificationToken) {
        return true;
    }

    @Override
    public void postChange(final IBaseMonitor<IAEItemStack> monitor, final Iterable<IAEItemStack> change, final IActionSource source) {
        for (final IAEItemStack is : change) {
            this.items.add(is);
        }
    }

    @Override
    public void onListUpdate() {
        for (final IContainerListener c : this.listeners) {
            this.queueInventory(c);
        }
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
        if (this.getGui() != null) {
            this.getGui().updateSetting(manager, settingName, newValue);
        }
    }

    @Override
    public IConfigManager getConfigManager() {
        if (Platform.isServer()) {
            return this.serverCM;
        }
        return this.clientCM;
    }

    public ItemStack[] getViewCells() {
        final ItemStack[] list = new ItemStack[this.cellView.length];

        for (int x = 0; x < this.cellView.length; x++) {
            list[x] = this.cellView[x].getStack();
        }

        return list;
    }

    public SlotRestrictedInput getCellViewSlot(final int index) {
        return this.cellView[index];
    }

    public boolean isPowered() {
        return this.hasPower;
    }

    private void setPowered(final boolean isPowered) {
        this.hasPower = isPowered;
    }

    public IConfigManagerHost getGui() {
        return this.gui;
    }

    public void setGui(@Nonnull final IConfigManagerHost gui) {
        this.gui = gui;
    }

    public IItemList<IAEItemStack> getItems() {
        return items;
    }

    public void postUpdate(final List<IAEItemStack> list) {
        for (final IAEItemStack is : list) {
            this.items.add(is);
        }
        ((GuiMEMonitorable) this.gui).postUpdate(list);
    }
}
