package dev.beecube31.crazyae2.common.containers.base;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.ICustomNameObject;
import appeng.helpers.InventoryAction;
import appeng.me.helpers.PlayerSource;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.AdaptorItemHandler;
import appeng.util.inv.WrapperCursorItemHandler;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.client.gui.SlotPlayerHotBar;
import dev.beecube31.crazyae2.client.gui.SlotPlayerInv;
import dev.beecube31.crazyae2.common.containers.base.slot.*;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.containers.guisync.SyncData;
import dev.beecube31.crazyae2.client.gui.CrazyAEMESlot;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketInventoryAction;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketTargetItemStack;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketValueConfig;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class CrazyAEBaseContainer extends Container {
    private final InventoryPlayer invPlayer;
    private final IActionSource mySrc;
    private final HashSet<Integer> locked = new HashSet<>();
    private final TileEntity tileEntity;
    private final IPart part;
    protected final IGuiItemObject obj;
    private final HashMap<Integer, SyncData> syncData = new HashMap<>();
    private boolean isContainerValid = true;
    private String customName;
    private ContainerOpenContext openContext;
    private IMEInventoryHandler<IAEItemStack> cellInv;
    private IEnergySource powerSrc;
    private boolean sentCustomName;
    private int ticksSinceCheck = 900;
    private IAEItemStack clientRequestedTargetItem = null;

    public CrazyAEBaseContainer(final InventoryPlayer ip, final TileEntity myTile, final IPart myPart) {
        this(ip, myTile, myPart, null);
    }

    public CrazyAEBaseContainer(final InventoryPlayer ip, final TileEntity myTile, final IPart myPart, final IGuiItemObject gio) {
        this.invPlayer = ip;
        this.tileEntity = myTile;
        this.part = myPart;
        this.obj = gio;
        this.mySrc = new PlayerSource(ip.player, this.getActionHost());
        this.prepareSync();
    }

    protected IActionHost getActionHost() {
        if (this.obj instanceof IActionHost) {
            return (IActionHost) this.obj;
        }

        if (this.tileEntity instanceof IActionHost) {
            return (IActionHost) this.tileEntity;
        }

        if (this.part instanceof IActionHost) {
            return (IActionHost) this.part;
        }

        return null;
    }

    private void prepareSync() {
        for (final Field f : this.getClass().getFields()) {
            if (f.isAnnotationPresent(GuiSync.class)) {
                final GuiSync annotation = f.getAnnotation(GuiSync.class);
                if (this.syncData.containsKey(annotation.value())) {
                    CrazyAE.logger().warn("Channel already in use: {} for {}", annotation.value(), f.getName());
                } else {
                    this.syncData.put(annotation.value(), new dev.beecube31.crazyae2.common.containers.guisync.SyncData(this, f, annotation));
                }
            }
        }
    }

    public CrazyAEBaseContainer(final InventoryPlayer ip, final Object anchor) {
        this.invPlayer = ip;
        this.tileEntity = anchor instanceof TileEntity ? (TileEntity) anchor : null;
        this.part = anchor instanceof IPart ? (IPart) anchor : null;
        this.obj = anchor instanceof IGuiItemObject ? (IGuiItemObject) anchor : null;

        if (this.tileEntity == null && this.part == null && this.obj == null) {
            throw new IllegalArgumentException("Must have a valid anchor, instead " + anchor + " in " + ip);
        }

        this.mySrc = new PlayerSource(ip.player, this.getActionHost());

        this.prepareSync();
    }

    public IAEItemStack getTargetStack() {
        return this.clientRequestedTargetItem;
    }

    public void setTargetStack(final IAEItemStack stack) {
        // client doesn't need to re-send, makes for lower overhead rapid packets.
        if (Platform.isClient()) {
            if (stack == null && this.clientRequestedTargetItem == null) {
                return;
            }
            if (stack != null && stack.isSameType(this.clientRequestedTargetItem)) {
                return;
            }

            NetworkHandler.instance().sendToServer(new PacketTargetItemStack((AEItemStack) stack));
        }

        this.clientRequestedTargetItem = stack == null ? null : stack.copy();
    }

    public IActionSource getActionSource() {
        return this.mySrc;
    }

    public void verifyPermissions(final SecurityPermissions security, final boolean requirePower) {
        if (Platform.isClient()) {
            return;
        }

        this.ticksSinceCheck++;
        if (this.ticksSinceCheck < 10) {
            return;
        }

        this.ticksSinceCheck = 0;
        this.setValidContainer(this.isValidContainer() && this.hasAccess(security, requirePower));
    }

    protected boolean hasAccess(final SecurityPermissions perm, final boolean requirePower) {
        final IActionHost host = this.getActionHost();

        if (host != null) {
            final IGridNode gn = host.getActionableNode();
            if (gn != null) {
                final IGrid g = gn.getGrid();
                if (requirePower) {
                    final IEnergyGrid eg = g.getCache(IEnergyGrid.class);
                    if (!eg.isNetworkPowered()) {
                        return false;
                    }
                }

                final ISecurityGrid sg = g.getCache(ISecurityGrid.class);
                return sg.hasPermission(this.getInventoryPlayer().player, perm);
            }
        }

        return false;
    }

    public void lockPlayerInventorySlot(final int idx) {
        this.locked.add(idx);
    }

    public Object getTarget() {
        if (this.tileEntity != null) {
            return this.tileEntity;
        }
        if (this.part != null) {
            return this.part;
        }
        return this.obj;
    }

    public InventoryPlayer getPlayerInv() {
        return this.getInventoryPlayer();
    }

    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    public final void updateFullProgressBar(final int idx, final long value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update(value);
            return;
        }

        this.updateProgressBar(idx, (int) value);
    }

    public void stringSync(final int idx, final String value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update(value);
        }
    }

    protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer, final int offsetX, final int offsetY) {
        IItemHandler ih = new PlayerInvWrapper(inventoryPlayer);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.locked.contains(j + i * 9 + 9)) {
                    this.addSlotToContainer(new SlotDisabled(ih, j + i * 9 + 9, 8 + j * 18 + offsetX, offsetY + i * 18));
                } else {
                    this.addSlotToContainer(new SlotPlayerInv(ih, j + i * 9 + 9, 8 + j * 18 + offsetX, offsetY + i * 18));
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            if (this.locked.contains(i)) {
                this.addSlotToContainer(new SlotDisabled(ih, i, 8 + i * 18 + offsetX, 58 + offsetY));
            } else {
                this.addSlotToContainer(new SlotPlayerHotBar(ih, i, 8 + i * 18 + offsetX, 58 + offsetY));
            }
        }
    }

    @Override
    protected Slot addSlotToContainer(final Slot newSlot) {
        if (newSlot instanceof final CrazyAESlot s) {
            s.setContainer(this);
            return super.addSlotToContainer(newSlot);
        } else {
            throw new IllegalArgumentException("Trying to add [" + newSlot + "] in [" + this.getClass() + "] to CrazyAE Container instead of CrazyAESlot.");
        }
    }

    @Override
    public void detectAndSendChanges() {
        this.sendCustomName();

        if (Platform.isServer()) {
            if (this.tileEntity != null && this.tileEntity.getWorld().getTileEntity(this.tileEntity.getPos()) != this.tileEntity) {
                this.setValidContainer(false);
            }

            for (final IContainerListener listener : this.listeners) {
                for (final SyncData sd : this.syncData.values()) {
                    sd.tick(listener);
                }
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer p, final int idx) {
        if (Platform.isClient()) {
            return ItemStack.EMPTY;
        }

        final CrazyAESlot clickSlot = (CrazyAESlot) this.inventorySlots.get(idx); // require AE SLots!

        if (clickSlot instanceof SlotDisabled || clickSlot instanceof SlotInaccessible) {
            return ItemStack.EMPTY;
        }
        if (clickSlot != null && clickSlot.getHasStack()) {
            ItemStack tis = clickSlot.getStack();

            if (tis.isEmpty()) {
                return ItemStack.EMPTY;
            }

            IItemDefinition expansionCard = AEApi.instance().definitions().materials().cardPatternExpansion();

            final List<Slot> selectedSlots = new ArrayList<>();

            if (clickSlot.isPlayerSide()) {
                tis = this.transferStackToContainer(tis);

                if (!tis.isEmpty()) {
                    for (final Object inventorySlot : this.inventorySlots) {
                        final CrazyAESlot cs = (CrazyAESlot) inventorySlot;

                        if (!(cs.isPlayerSide()) && !(cs instanceof SlotFake)) {
                            if (cs.isItemValid(tis)) {
                                selectedSlots.add(cs);
                            }
                        }
                    }
                }
            } else {
                tis = tis.copy();

                for (final Object inventorySlot : this.inventorySlots) {
                    final CrazyAESlot cs = (CrazyAESlot) inventorySlot;

                    if ((cs.isPlayerSide()) && !(cs instanceof SlotFake)) {
                        if (cs.isItemValid(tis)) {
                            selectedSlots.add(cs);
                        }
                    }
                }
            }

            if (selectedSlots.isEmpty() && clickSlot.isPlayerSide()) {
                if (!tis.isEmpty()) {
                    // target slots in the container...
                    for (final Object inventorySlot : this.inventorySlots) {
                        final CrazyAESlot cs = (CrazyAESlot) inventorySlot;
                        final ItemStack destination = cs.getStack();

                        if (!(cs.isPlayerSide()) && cs instanceof SlotFake) {
                            if (Platform.itemComparisons().isSameItem(destination, tis)) {
                                break;
                            } else if (destination.isEmpty()) {
                                cs.putStack(tis.copy());
                                this.updateSlot(cs);
                                break;
                            }
                        }
                    }
                }
            }

            if (!tis.isEmpty()) {
                for (final Slot d : selectedSlots) {
                    if (d instanceof SlotDisabled || d instanceof CrazyAEMESlot) {
                        continue;
                    }

                    if (d.isItemValid(tis)) {
                        if (d.getHasStack()) {
                            final ItemStack t = d.getStack().copy();

                            if (Platform.itemComparisons().isSameItem(tis, t))
                            {
                                if (d instanceof RestrictedSlot rs) {
                                    RestrictedSlot.PlaceableItemType it = rs.getPlaceableItemType();
                                    if (
                                            it == RestrictedSlot.PlaceableItemType.ELVENTRADE_ENCODED_PATTERN
                                            || it == RestrictedSlot.PlaceableItemType.MANAPOOL_ENCODED_PATTERN
                                            || it == RestrictedSlot.PlaceableItemType.PETAL_ENCODED_PATTERN
                                            || it == RestrictedSlot.PlaceableItemType.RUNEALTAR_ENCODED_PATTERN
                                            || it == RestrictedSlot.PlaceableItemType.PUREDAISY_ENCODED_PATTERN
                                    ) return ItemStack.EMPTY;
                                }

                                int maxSize = Math.min(tis.getMaxStackSize(), d.getSlotStackLimit());
                                int placeAble = maxSize - t.getCount();

                                if (tis.getCount() < placeAble) {
                                    placeAble = tis.getCount();
                                }

                                t.setCount(t.getCount() + placeAble);
                                tis.setCount(tis.getCount() - placeAble);

                                d.putStack(t);

                                if (tis.getCount() <= 0) {
                                    clickSlot.putStack(ItemStack.EMPTY);
                                    d.onSlotChanged();

                                    this.updateSlot(clickSlot);
                                    this.updateSlot(d);
                                    return ItemStack.EMPTY;
                                } else {
                                    this.updateSlot(d);
                                }
                            }
                        }
                    }
                }

                // any match..
                for (final Slot d : selectedSlots) {
                    if (d instanceof SlotDisabled || d instanceof CrazyAEMESlot) {
                        continue;
                    }

                    if (d.isItemValid(tis)) {
                        if (!d.getHasStack()) {
                            int maxSize = Math.min(tis.getMaxStackSize(), d.getSlotStackLimit());

                            final ItemStack tmp = tis.copy();
                            if (tmp.getCount() > maxSize) {
                                tmp.setCount(maxSize);
                            }

                            tis.setCount(tis.getCount() - tmp.getCount());
                            d.putStack(tmp);

                            if (tis.getCount() <= 0) {
                                clickSlot.putStack(ItemStack.EMPTY);
                                d.onSlotChanged();

                                this.updateSlot(clickSlot);
                                this.updateSlot(d);
                                return ItemStack.EMPTY;
                            } else {
                                this.updateSlot(d);

                                if (d instanceof RestrictedSlot rs) {
                                    RestrictedSlot.PlaceableItemType it = rs.getPlaceableItemType();
                                    if
                                    (
                                        it == RestrictedSlot.PlaceableItemType.ELVENTRADE_ENCODED_PATTERN
                                        || it == RestrictedSlot.PlaceableItemType.MANAPOOL_ENCODED_PATTERN
                                        || it == RestrictedSlot.PlaceableItemType.PETAL_ENCODED_PATTERN
                                        || it == RestrictedSlot.PlaceableItemType.RUNEALTAR_ENCODED_PATTERN
                                        || it == RestrictedSlot.PlaceableItemType.PUREDAISY_ENCODED_PATTERN
                                    ) break;
                                }
                            }
                        }
                    }
                }
            }

            clickSlot.putStack(!tis.isEmpty() ? tis : ItemStack.EMPTY);
        }

        this.updateSlot(clickSlot);
        return ItemStack.EMPTY;
    }

    @Override
    public final void updateProgressBar(final int idx, final int value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update((long) value);
        }
    }

    @Override
    public boolean canInteractWith(final EntityPlayer entityplayer) {
        if (this.isValidContainer()) {
            if (this.tileEntity instanceof IInventory) {
                return ((IInventory) this.tileEntity).isUsableByPlayer(entityplayer);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canDragIntoSlot(final Slot s) {
        return ((CrazyAESlot) s).isDraggable();
    }

    public void doAction(final EntityPlayerMP player, final InventoryAction action, final int slot, final long id) {
        if (slot >= 0 && slot < this.inventorySlots.size()) {
            final Slot s = this.getSlot(slot);
            if (s instanceof SlotCraftingTerm) {
                switch (action) {
                    case CRAFT_SHIFT:
                    case CRAFT_ITEM:
                    case CRAFT_STACK:
                        ((SlotCraftingTerm) s).doClick(action, player);
                        this.updateHeld(player);
                    default:
                }
            }

            final ItemStack hand = player.inventory.getItemStack();

            switch (action) {
                case PICKUP_OR_SET_DOWN:
                    if (hand.isEmpty()) {
                        s.putStack(ItemStack.EMPTY);
                    } else {
                        s.putStack(hand.copy());
                    }
                    break;
                case PLACE_SINGLE:
                    if (!hand.isEmpty()) {
                        final ItemStack is = hand.copy();
                        is.setCount(1);
                        s.putStack(is);
                    } else {
                        final ItemStack is = s.getStack().copy();
                        if (is.getCount() < is.getMaxStackSize() * 8)
                            is.grow(1);
                        s.putStack(is);
                    }
                    break;
                case PICKUP_SINGLE:
                    if (hand.isEmpty()) {
                        final ItemStack is = s.getStack().copy();
                        if (is.getCount() > 1)
                            is.shrink(1);
                        s.putStack(is);
                    }
                    break;
                case SPLIT_OR_PLACE_SINGLE:
                    ItemStack is = s.getStack();
                    if (!is.isEmpty()) {
                        if (hand.isEmpty()) {
                            is.setCount(Math.max(1, is.getCount() - 1));
                        } else if (hand.isItemEqual(is)) {
                            is.setCount(Math.min(is.getMaxStackSize(), is.getCount() + 1));
                        } else {
                            is = hand.copy();
                            is.setCount(1);
                        }
                        s.putStack(is);
                    } else if (!hand.isEmpty()) {
                        is = hand.copy();
                        is.setCount(1);
                        s.putStack(is);
                    }
                    break;
                case HALVE:
                    if (s.getStack().getCount() > 1) {
                        ItemStack halved = s.getStack().copy();
                        halved.setCount(s.getStack().getCount() / 2);
                        s.putStack(halved);
                    }
                    break;
                case DOUBLE:
                    ItemStack doubled = s.getStack().copy();
                    if (s.getStack().getCount() * 2 > 0) {
                        doubled.setCount(Math.min(s.getSlotStackLimit(), s.getStack().getCount() * 2));
                        s.putStack(doubled);
                    }
                    break;
                case CREATIVE_DUPLICATE:
                case MOVE_REGION:
                case SHIFT_CLICK:
                default:
                    break;
            }

            if (action == InventoryAction.MOVE_REGION) {
                final List<Slot> from = new ArrayList<>();

                for (final Slot j : this.inventorySlots) {
                    if (j != null && j.getClass() == s.getClass() && !(j instanceof SlotCraftingTerm)) {
                        from.add(j);
                    }
                }

                for (final Slot fr : from) {
                    this.transferStackInSlot(player, fr.slotNumber);
                }
            }

            return;
        }

        // get target item.
        final IAEItemStack slotItem = this.clientRequestedTargetItem;
        final Slot s = this.getSlot(slot);
        if (!(s instanceof SlotFake) || (s instanceof SlotFake f && !f.isOneStack())) {
            switch (action) {
                case SHIFT_CLICK:
                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    if (slotItem != null) {
                        IAEItemStack ais = slotItem.copy();
                        ItemStack myItem = ais.createItemStack();

                        ais.setStackSize(myItem.getMaxStackSize());

                        final InventoryAdaptor adp = InventoryAdaptor.getAdaptor(player);
                        myItem.setCount((int) ais.getStackSize());
                        myItem = adp.simulateAdd(myItem);

                        if (!myItem.isEmpty()) {
                            ais.setStackSize(ais.getStackSize() - myItem.getCount());
                        }

                        ais = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                        if (ais != null) {
                            adp.addItems(ais.createItemStack());
                        }
                    }
                    break;
                case ROLL_DOWN:
                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    final int releaseQty = 1;
                    final ItemStack isg = player.inventory.getItemStack();

                    if (!isg.isEmpty() && releaseQty > 0) {
                        IAEItemStack ais = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(isg);
                        ais.setStackSize(1);
                        final IAEItemStack extracted = ais.copy();

                        ais = Platform.poweredInsert(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                        if (ais == null) {
                            final InventoryAdaptor ia = new AdaptorItemHandler(new WrapperCursorItemHandler(player.inventory));

                            final ItemStack fail = ia.removeItems(1, extracted.getDefinition(), null);
                            if (fail.isEmpty()) {
                                this.getCellInventory().extractItems(extracted, Actionable.MODULATE, this.getActionSource());
                            }

                            this.updateHeld(player);
                        }
                    }

                    break;
                case ROLL_UP:
                case PICKUP_SINGLE:
                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    if (slotItem != null) {
                        int liftQty = 1;
                        final ItemStack item = player.inventory.getItemStack();

                        if (!item.isEmpty()) {
                            if (item.getCount() >= item.getMaxStackSize()) {
                                liftQty = 0;
                            }
                            if (!Platform.itemComparisons().isSameItem(slotItem.getDefinition(), item)) {
                                liftQty = 0;
                            }
                        }

                        if (liftQty > 0) {
                            IAEItemStack ais = slotItem.copy();
                            ais.setStackSize(1);
                            ais = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                            if (ais != null) {
                                final InventoryAdaptor ia = new AdaptorItemHandler(new WrapperCursorItemHandler(player.inventory));

                                final ItemStack fail = ia.addItems(ais.createItemStack());
                                if (!fail.isEmpty()) {
                                    this.getCellInventory().injectItems(ais, Actionable.MODULATE, this.getActionSource());
                                }

                                this.updateHeld(player);
                            }
                        }
                    }
                    break;
                case PICKUP_OR_SET_DOWN:
                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    if (player.inventory.getItemStack().isEmpty()) {
                        if (slotItem != null) {
                            IAEItemStack ais = slotItem.copy();
                            ais.setStackSize(ais.getDefinition().getMaxStackSize());
                            ais = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                            if (ais != null) {
                                player.inventory.setItemStack(ais.createItemStack());
                            } else {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                            }
                            this.updateHeld(player);
                        }
                    } else {
                        IAEItemStack ais = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(player.inventory.getItemStack());
                        ais = Platform.poweredInsert(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                        if (ais != null) {
                            player.inventory.setItemStack(ais.createItemStack());
                        } else {
                            player.inventory.setItemStack(ItemStack.EMPTY);
                        }
                        this.updateHeld(player);
                    }

                    break;
                case SPLIT_OR_PLACE_SINGLE:
                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    if (player.inventory.getItemStack().isEmpty()) {
                        if (slotItem != null) {
                            IAEItemStack ais = slotItem.copy();
                            final long maxSize = ais.getDefinition().getMaxStackSize();
                            ais.setStackSize(maxSize);
                            ais = this.getCellInventory().extractItems(ais, Actionable.SIMULATE, this.getActionSource());

                            if (ais != null) {
                                final long stackSize = Math.min(maxSize, ais.getStackSize());
                                ais.setStackSize((stackSize + 1) >> 1);
                                ais = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                            }

                            if (ais != null) {
                                player.inventory.setItemStack(ais.createItemStack());
                            } else {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                            }
                            this.updateHeld(player);
                        }
                    } else {
                        IAEItemStack ais = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(player.inventory.getItemStack());
                        ais.setStackSize(1);
                        ais = Platform.poweredInsert(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                        if (ais == null) {
                            final ItemStack is = player.inventory.getItemStack();
                            is.setCount(is.getCount() - 1);
                            if (is.getCount() <= 0) {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                            }
                            this.updateHeld(player);
                        }
                    }

                    break;
                case CREATIVE_DUPLICATE:
                    if (player.capabilities.isCreativeMode && slotItem != null) {
                        final ItemStack is = slotItem.createItemStack();
                        is.setCount(is.getMaxStackSize());
                        player.inventory.setItemStack(is);
                        this.updateHeld(player);
                    }
                    break;
                case MOVE_REGION:

                    if (this.getPowerSource() == null || this.getCellInventory() == null) {
                        return;
                    }

                    if (slotItem != null) {
                        final int playerInv = 9 * 4;
                        for (int slotNum = 0; slotNum < playerInv; slotNum++) {
                            IAEItemStack ais = slotItem.copy();
                            ItemStack myItem = ais.createItemStack();

                            ais.setStackSize(myItem.getMaxStackSize());

                            final InventoryAdaptor adp = InventoryAdaptor.getAdaptor(player);
                            myItem.setCount((int) ais.getStackSize());
                            myItem = adp.simulateAdd(myItem);

                            if (!myItem.isEmpty()) {
                                ais.setStackSize(ais.getStackSize() - myItem.getCount());
                            }

                            ais = Platform.poweredExtraction(this.getPowerSource(), this.getCellInventory(), ais, this.getActionSource());
                            if (ais != null) {
                                adp.addItems(ais.createItemStack());
                            } else {
                                return;
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    protected void updateHeld(final EntityPlayerMP p) {
        if (Platform.isServer()) {
            try {
                NetworkHandler.instance().sendTo(
                        new PacketInventoryAction(InventoryAction.UPDATE_HAND, 0, AEItemStack.fromItemStack(p.inventory.getItemStack())),
                        p
                );
            } catch (final IOException e) {
                CrazyAE.logger().debug(e);
            }
        }
    }

    protected ItemStack transferStackToContainer(final ItemStack input) {
        return this.shiftStoreItem(input);
    }

    private ItemStack shiftStoreItem(final ItemStack input) {
        if (this.getPowerSource() == null || this.getCellInventory() == null) {
            return input;
        }
        final IAEItemStack ais = Platform.poweredInsert(this.getPowerSource(), this.getCellInventory(),
                Objects.requireNonNull(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(input)),
                this.getActionSource());
        if (ais == null) {
            return ItemStack.EMPTY;
        }
        return ais.createItemStack();
    }

    private void updateSlot(final Slot clickSlot) {
        // ???
        this.detectAndSendChanges();
    }

    private void sendCustomName() {
        if (!this.sentCustomName) {
            this.sentCustomName = true;
            if (Platform.isServer()) {
                ICustomNameObject name = null;

                if (this.part instanceof ICustomNameObject) {
                    name = (ICustomNameObject) this.part;
                }

                if (this.tileEntity instanceof ICustomNameObject) {
                    name = (ICustomNameObject) this.tileEntity;
                }

                if (this.obj instanceof ICustomNameObject) {
                    name = (ICustomNameObject) this.obj;
                }

                if (this instanceof ICustomNameObject) {
                    name = (ICustomNameObject) this;
                }

                if (name != null) {
                    if (name.hasCustomInventoryName()) {
                        this.setCustomName(name.getCustomInventoryName());
                    }

                    if (this.getCustomName() != null) {
                        try {
                            NetworkHandler.instance()
                                    .sendTo(new PacketValueConfig("CustomName", this.getCustomName()),
                                            (EntityPlayerMP) this.getInventoryPlayer().player);
                        } catch (final IOException e) {
                            CrazyAE.logger().debug(e);
                        }
                    }
                }
            }
        }
    }

    public void swapSlotContents(final int slotA, final int slotB) {
        final Slot a = this.getSlot(slotA);
        final Slot b = this.getSlot(slotB);

        final ItemStack isA = a.getStack();
        final ItemStack isB = b.getStack();

        // something to do?
        if (isA.isEmpty() && isB.isEmpty()) {
            return;
        }

        // can take?

        if (!isA.isEmpty() && !a.canTakeStack(this.getInventoryPlayer().player)) {
            return;
        }

        if (!isB.isEmpty() && !b.canTakeStack(this.getInventoryPlayer().player)) {
            return;
        }

        // swap valid?

        if (!isB.isEmpty() && !a.isItemValid(isB)) {
            return;
        }

        if (!isA.isEmpty() && !b.isItemValid(isA)) {
            return;
        }

        ItemStack testA = isB.isEmpty() ? ItemStack.EMPTY : isB.copy();
        ItemStack testB = isA.isEmpty() ? ItemStack.EMPTY : isA.copy();

        // can put some back?
        if (!testA.isEmpty() && testA.getCount() > a.getSlotStackLimit()) {
            if (!testB.isEmpty()) {
                return;
            }

            final int totalA = testA.getCount();
            testA.setCount(a.getSlotStackLimit());
            testB = testA.copy();

            testB.setCount(totalA - testA.getCount());
        }

        if (!testB.isEmpty() && testB.getCount() > b.getSlotStackLimit()) {
            if (!testA.isEmpty()) {
                return;
            }

            final int totalB = testB.getCount();
            testB.setCount(b.getSlotStackLimit());
            testA = testB.copy();

            testA.setCount(totalB - testA.getCount());
        }

        a.putStack(testA);
        b.putStack(testB);
    }

    @Override
    @NotNull
    public ItemStack slotClick(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull EntityPlayer player) {
        if (slotId >= 0 && clickTypeIn == ClickType.PICKUP) {
            final var slot = this.getSlot(slotId);
            if (slot instanceof CrazyAESlot crazyAESlot) {
                var slotStack = slot.getStack();
                var draggedStack = this.invPlayer.getItemStack();

                // The default vanilla behavior assumes that slots can't hold more items than the default stack size.
                // Thus, it's possible to underflow the vanilla code when clicking non-empty slots with an item stack.
                if (!draggedStack.isEmpty()) {
                    if (crazyAESlot.isItemValid(draggedStack)) {
                        if (slotStack.getItem() == draggedStack.getItem() && slotStack.getMetadata() == draggedStack.getMetadata() && ItemStack.areItemStackTagsEqual(slotStack, draggedStack)) {
                            // Slot size or stack size, whichever is smaller.
                            var maxSize = Math.min(crazyAESlot.getSlotStackLimit(), draggedStack.getMaxStackSize());

                            // The maximum number of items that can be inserted into the slot, non-negative.
                            var maxInsertable = Math.min(draggedStack.getCount(),
                                    Math.max(0, maxSize - crazyAESlot.getStack().getCount()));

                            if (maxInsertable != 0) {
                                var toInsert = Math.min(maxInsertable, dragType == 0 ? maxInsertable : 1);

                                draggedStack.shrink(toInsert);
                                slotStack.grow(toInsert);

                                slot.putStack(slot.getStack());
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
                // Fixes taking and halving issues from oversized slots.
                else if (dragType == 0 || dragType == 1) {
                    if (slot.canTakeStack(player) && !slotStack.isEmpty()) {
                        var result = slotStack.copy();
                        var toTake = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());
                        this.invPlayer.setItemStack(slot.decrStackSize(dragType == 0 ? toTake : (toTake + 1) / 2));

                        slot.putStack(slot.getStack());
                        return result;
                    }
                }

            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    public void onUpdate(final String field, final Object oldValue, final Object newValue) {

    }

    public void onSlotChange(final Slot s) {

    }

    public boolean isValidForSlot(final Slot s, final ItemStack i) {
        return true;
    }

    public IMEInventoryHandler<IAEItemStack> getCellInventory() {
        return this.cellInv;
    }

    public void setCellInventory(final IMEInventoryHandler<IAEItemStack> cellInv) {
        this.cellInv = cellInv;
    }

    public String getCustomName() {
        return this.customName;
    }

    public void setCustomName(final String customName) {
        this.customName = customName;
    }

    public InventoryPlayer getInventoryPlayer() {
        return this.invPlayer;
    }

    public boolean isValidContainer() {
        return this.isContainerValid;
    }

    public void setValidContainer(final boolean isContainerValid) {
        this.isContainerValid = isContainerValid;
    }

    public ContainerOpenContext getOpenContext() {
        return this.openContext;
    }

    public void setOpenContext(final ContainerOpenContext openContext) {
        this.openContext = openContext;
    }

    public IEnergySource getPowerSource() {
        return this.powerSrc;
    }

    public void setPowerSource(final IEnergySource powerSrc) {
        this.powerSrc = powerSrc;
    }
}
