package dev.beecube31.crazyae2.common.containers.base.slot;

import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotDisabled extends CrazyAESlot {

    public SlotDisabled(final IItemHandler par1iInventory, final int slotIndex, final int x, final int y) {
        super(par1iInventory, slotIndex, x, y);
    }

    @Override
    public boolean isItemValid(final @NotNull ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return false;
    }
}
