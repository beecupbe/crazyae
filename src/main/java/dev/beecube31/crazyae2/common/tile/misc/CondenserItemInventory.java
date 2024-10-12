package dev.beecube31.crazyae2.common.tile.misc;


import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.BaseActionSource;
import appeng.me.storage.ITickingMonitor;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


class CondenserItemInventory implements IMEMonitor<IAEItemStack>, ITickingMonitor {
    private final HashMap<IMEMonitorHandlerReceiver<IAEItemStack>, Object> listeners = new HashMap<>();
    private final TileImprovedCondenser target;
    private boolean hasChanged = true;
    private final ItemList cachedList = new ItemList();
    private IActionSource actionSource = new BaseActionSource();
    private ItemList changeSet = new ItemList();

    CondenserItemInventory(final TileImprovedCondenser te) {
        this.target = te;
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode, final IActionSource src) {
        if (mode == Actionable.MODULATE && input != null) {
            this.target.addPower(input.getStackSize());
        }
        return null;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        AEItemStack ret = null;

        for (int i = 0; i < 4; i++) {
            ItemStack slotItem = this.target.getOutputSlot().getStackInSlot(i);
            if (!slotItem.isEmpty() && request.isSameType(slotItem)) {
                int count = (int) Math.min(request.getStackSize(), Integer.MAX_VALUE);
                ret = AEItemStack.fromItemStack(this.target.getOutputSlot().extractItem(i, count, mode == Actionable.SIMULATE));

                if (ret != null && !(ret.getStackSize() > 0)) {
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out) {
        for (int i = 0; i < 4; i++) {
            ItemStack slotItem = this.target.getOutputSlot().getStackInSlot(i);

            if (!slotItem.isEmpty()) {
                out.add(AEItemStack.fromItemStack(slotItem));
            }
        }

        return out;
    }

    @Override
    public IItemList<IAEItemStack> getStorageList() {
        if (this.hasChanged) {
            this.hasChanged = false;
            this.cachedList.resetStatus();
            return this.getAvailableItems(this.cachedList);
        }
        return this.cachedList;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack input) {
        return false;
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(final int i) {
        return i == 2;
    }

    @Override
    public void addListener(final IMEMonitorHandlerReceiver<IAEItemStack> l, final Object verificationToken) {
        this.listeners.put(l, verificationToken);
    }

    @Override
    public void removeListener(final IMEMonitorHandlerReceiver<IAEItemStack> l) {
        this.listeners.remove(l);
    }

    public void updateOutput(ItemStack added, ItemStack removed) {
        this.hasChanged = true;
        if (!added.isEmpty()) {
            this.changeSet.add(AEItemStack.fromItemStack(added));
        }
        if (!removed.isEmpty()) {
            this.changeSet.add(AEItemStack.fromItemStack(removed).setStackSize(-removed.getCount()));
        }
    }

    @Override
    public TickRateModulation onTick() {
        final ItemList currentChanges = this.changeSet;

        if (currentChanges.isEmpty()) {
            return TickRateModulation.IDLE;
        }

        this.changeSet = new ItemList();
        final Iterator<Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> i = this.listeners.entrySet().iterator();
        while (i.hasNext()) {
            final Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object> l = i.next();
            final IMEMonitorHandlerReceiver<IAEItemStack> key = l.getKey();
            if (key.isValid(l.getValue())) {
                key.postChange(this, currentChanges, this.actionSource);
            } else {
                i.remove();
            }
        }

        return TickRateModulation.URGENT;
    }

    @Override
    public void setActionSource(IActionSource actionSource) {
        this.actionSource = actionSource;
    }
}
