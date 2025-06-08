package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.container.slot.IJEITargetSlot;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotFake extends CrazyAESlot implements IJEITargetSlot {

    private final boolean oneStack;

    public SlotFake(final IItemHandler inv, final int idx, final int x, final int y) {
        super(inv, idx, x, y);
        this.oneStack = false;
    }

    public SlotFake(final IItemHandler inv, final int idx, final int x, final int y, boolean oneStack) {
        super(inv, idx, x, y);
        this.oneStack = oneStack;
    }

    @Override
    public int getSlotStackLimit() {
        return this.oneStack ? 1 : super.getSlotStackLimit();
    }

    @Override
    public ItemStack onTake(final EntityPlayer par1EntityPlayer, final ItemStack par2ItemStack) {
        return par2ItemStack;
    }

    public boolean isOneStack() {
        return this.oneStack;
    }

    @Override
    public @NotNull ItemStack decrStackSize(final int par1) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(final @NotNull ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public void putStack(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
        }

        super.putStack(is);
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }
}
