package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.inventories.InternalInventory;
import appeng.menu.AEBaseMenu;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class CraftingMatrixSlot extends CrazyAESlot {
    private final AEBaseMenu c;
    private final Container wrappedInventory;

    public CraftingMatrixSlot(AEBaseMenu c, InternalInventory inv, int invSlot) {
        super(inv, invSlot);
        this.c = c;
        this.wrappedInventory = inv.toContainer();
    }

    @Override
    public void clearStack() {
        super.clearStack();
        this.c.slotsChanged(this.wrappedInventory);
    }

    @Override
    public void set(ItemStack par1ItemStack) {
        super.set(par1ItemStack);
        this.c.slotsChanged(this.wrappedInventory);
    }

    @Override
    public ItemStack remove(int par1) {
        final ItemStack is = super.remove(par1);
        this.c.slotsChanged(this.wrappedInventory);
        return is;
    }
}
