package dev.beecube31.crazyae2.common.containers.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.inventories.InternalInventory;

public class InaccessibleSlot extends CrazyAESlot {

    private ItemStack dspStack = ItemStack.EMPTY;

    public InaccessibleSlot(InternalInventory i, int invSlot) {
        super(i, invSlot);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dspStack = ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public ItemStack getDisplayStack() {
        if (this.dspStack.isEmpty()) {
            final ItemStack dsp = super.getDisplayStack();
            if (!dsp.isEmpty()) {
                this.dspStack = dsp.copy();
            }
        }
        return this.dspStack;
    }
}
