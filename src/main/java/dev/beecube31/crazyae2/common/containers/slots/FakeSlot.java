package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigInventory;
import appeng.util.ConfigMenuInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FakeSlot extends CrazyAESlot {
    public FakeSlot(InternalInventory inv, int invSlot) {
        super(inv, invSlot);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void set(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
        }

        super.set(is);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    // Used by REI/JEI dragging ghost items to determine if this is a valid destination
    public boolean canSetFilterTo(ItemStack stack) {
        return slot < getInventory().size() && getInventory().isItemValid(slot, stack);
    }

    public void increase(ItemStack is) {
        // Special support for increasing the configured stocking amount by simply clicking
        if (getInventory() instanceof ConfigMenuInventory configInv) {
            var realInv = configInv.getDelegate();
            if (realInv.getMode() == ConfigInventory.Mode.CONFIG_STACKS) {
                var newFilter = configInv.convertToSuitableStack(is);
                if (newFilter != null && newFilter.what().equals(realInv.getKey(slot))) {
                    realInv.insert(slot, newFilter.what(), newFilter.amount(), Actionable.MODULATE);
                    return;
                }
            }
        }

        set(is);
    }

    public void decrease(ItemStack is) {
        // Special support for modern config mode inventories
        if (getInventory() instanceof ConfigMenuInventory configInv) {
            var realInv = configInv.getDelegate();
            if (realInv.getMode() == ConfigInventory.Mode.CONFIG_STACKS) {
                var newFilter = configInv.convertToSuitableStack(is);

                if (newFilter != null) {
                    realInv.extract(slot, newFilter.what(), newFilter.amount(), Actionable.MODULATE);
                    return;
                }
            }
        }

        var current = getItem();
        if (is.isEmpty()) {
            // Decrease when holding nothing in hand
            current = current.copy();
            current.shrink(1);
            set(current);
        } else if (ItemStack.isSameItemSameComponents(current, is)) {
            // Increase when holding same item
            current = current.copy();
            current.grow(1);
            set(current);
        } else {
            is = is.copy();
            is.setCount(1);
            set(is);
        }
    }
}
