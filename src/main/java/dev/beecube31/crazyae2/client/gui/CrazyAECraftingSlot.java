package dev.beecube31.crazyae2.client.gui;

import appeng.util.helpers.ItemHandlerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class CrazyAECraftingSlot extends CrazyAESlot {

    private final IItemHandler craftMatrix;

    private final EntityPlayer thePlayer;

    private int amountCrafted;

    public CrazyAECraftingSlot(final EntityPlayer par1EntityPlayer, final IItemHandler par2IInventory, final IItemHandler par3IInventory, final int par4, final int par5, final int par6) {
        super(par3IInventory, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.craftMatrix = par2IInventory;
    }

    @Override
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return false;
    }

    @Override
    protected void onCrafting(final ItemStack par1ItemStack, final int par2) {
        this.amountCrafted += par2;
        this.onCrafting(par1ItemStack);
    }

    @Override
    protected void onCrafting(final ItemStack par1ItemStack) {
        par1ItemStack.onCrafting(this.thePlayer.world, this.thePlayer, this.amountCrafted);
        this.amountCrafted = 0;
    }

    @Override
    public ItemStack onTake(final EntityPlayer playerIn, final ItemStack stack) {
        this.onCrafting(stack);
        stack.getItem().onCreated(stack, playerIn.world, playerIn);
        final InventoryCrafting ic = new InventoryCrafting(this.getContainer(), 3, 3);

        for (int x = 0; x < this.craftMatrix.getSlots(); x++) {
            ic.setInventorySlotContents(x, this.craftMatrix.getStackInSlot(x));
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, ic);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);

        final NonNullList<ItemStack> aitemstack = this.getRemainingItems(ic, playerIn.world);

        ItemHandlerUtil.copy(ic, this.craftMatrix, false);

        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < aitemstack.size(); ++i) {
            final ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
            final ItemStack itemstack2 = aitemstack.get(i);

            if (!itemstack1.isEmpty()) {
                this.craftMatrix.extractItem(i, 1, false);
            }

            if (!itemstack2.isEmpty()) {
                if (this.craftMatrix.getStackInSlot(i).isEmpty()) {
                    ItemHandlerUtil.setStackInSlot(this.craftMatrix, i, itemstack2);
                } else if (!this.thePlayer.inventory.addItemStackToInventory(itemstack2)) {
                    this.thePlayer.dropItem(itemstack2, false);
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack decrStackSize(final int par1) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(par1, this.getStack().getCount());
        }

        return super.decrStackSize(par1);
    }

    protected NonNullList<ItemStack> getRemainingItems(InventoryCrafting ic, World world) {
        return CraftingManager.getRemainingItems(ic, world);
    }
}
