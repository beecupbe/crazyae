package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.items.misc.ItemEncodedPattern;
import appeng.util.Platform;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.common.interfaces.jei.IJEITargetSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotDisconnected extends CrazyAESlot implements IJEITargetSlot {

    private final ClientDCInternalInv mySlot;

    public SlotDisconnected(final ClientDCInternalInv me, final int which, final int x, final int y) {
        super(me.getInventory(), which, x, y);
        this.mySlot = me;
    }

    @Override
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public void putStack(final ItemStack par1ItemStack) {

    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }

    @Override
    public ItemStack getDisplayStack() {
        if (Platform.isClient()) {
            final ItemStack is = super.getStack();
            if (!is.isEmpty() && is.getItem() instanceof final ItemEncodedPattern iep) {
                final ItemStack out = iep.getOutput(is);
                if (!out.isEmpty()) {
                    return out;
                }
            }
        }
        return super.getStack();
    }

    @Override
    public boolean getHasStack() {
        return !this.getStack().isEmpty();
    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public ItemStack decrStackSize(final int par1) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isHere(final IInventory inv, final int slotIn) {
        return false;
    }

    public ClientDCInternalInv getSlot() {
        return this.mySlot;
    }
}
