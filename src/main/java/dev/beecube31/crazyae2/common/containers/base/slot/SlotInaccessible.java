package dev.beecube31.crazyae2.common.containers.base.slot;

import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotInaccessible extends CrazyAESlot {

    private ItemStack dspStack = ItemStack.EMPTY;

    public SlotInaccessible(final IItemHandler i, final int slotIdx, final int x, final int y) {
        super(i, slotIdx, x, y);
    }

    @Override
    public boolean isItemValid(final ItemStack i) {
        return false;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        this.dspStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
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
