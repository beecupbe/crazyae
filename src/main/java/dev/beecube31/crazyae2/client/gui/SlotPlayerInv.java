package dev.beecube31.crazyae2.client.gui;

import net.minecraftforge.items.IItemHandler;

public class SlotPlayerInv extends CrazyAESlot {
    public SlotPlayerInv(final IItemHandler par1iInventory, final int par2, final int par3, final int par4) {
        super(par1iInventory, par2, par3, par4);
        this.setPlayerSide(true);
    }
}
