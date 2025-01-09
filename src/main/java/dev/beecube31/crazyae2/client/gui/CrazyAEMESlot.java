package dev.beecube31.crazyae2.client.gui;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CrazyAEMESlot extends SlotItemHandler {

    private final CrazyAEInternalMESlot mySlot;

    public CrazyAEMESlot(final CrazyAEInternalMESlot me) {
        super(null, 0, me.getxPosition(), me.getyPosition());
        this.mySlot = me;
    }

    public IAEItemStack getAEStack() {
        if (this.mySlot.hasPower()) {
            return this.mySlot.getAEStack();
        }
        return null;
    }

    @Override
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getStack() {
        if (this.mySlot.hasPower()) {
            return this.mySlot.getStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean getHasStack() {
        if (this.mySlot.hasPower()) {
            return !this.getStack().isEmpty();
        }
        return false;
    }

    @Override
    public void putStack(final ItemStack par1ItemStack) {

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

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }
}
