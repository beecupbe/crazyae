package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.inventories.InternalInventory;
import appeng.crafting.CraftingEvent;
import appeng.helpers.Inventories;
import appeng.util.inv.AppEngInternalInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public class CrazyCraftingSlot extends CrazyAESlot {

    /**
     * The craft matrix inventory linked to this result slot.
     */
    private final InternalInventory craftingGrid;

    /**
     * The player that is using the GUI where this slot resides.
     */
    private final Player player;

    /**
     * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
     */
    private int amountCrafted;

    public CrazyCraftingSlot(Player player, InternalInventory craftingGrid) {
        super(new AppEngInternalInventory(1), 0);
        this.player = player;
        this.craftingGrid = craftingGrid;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    @Override
    protected void onQuickCraft(ItemStack par1ItemStack, int par2) {
        this.amountCrafted += par2;
        this.checkTakeAchievements(par1ItemStack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void checkTakeAchievements(ItemStack par1ItemStack) {
        par1ItemStack.onCraftedBy(this.player.level(), this.player, this.amountCrafted);
        this.amountCrafted = 0;
    }

    @Override
    public void onTake(Player playerIn, ItemStack stack) {
        CraftingEvent.fireCraftingEvent(playerIn, stack, this.craftingGrid.toContainer());
        this.amountCrafted += stack.getCount();
        this.checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(playerIn);
        final CraftingContainer ic = new TransientCraftingContainer(this.getContainer(), 3, 3);

        for (int x = 0; x < this.craftingGrid.size(); x++) {
            ic.setItem(x, this.craftingGrid.getStackInSlot(x));
        }

        var aitemstack = this.getRemainingItems(ic, playerIn.level());

        Inventories.copy(ic, this.craftingGrid, false);

        ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < aitemstack.size(); ++i) {
            final ItemStack itemstack1 = this.craftingGrid.getStackInSlot(i);
            final ItemStack itemstack2 = aitemstack.get(i);

            if (!itemstack1.isEmpty()) {
                this.craftingGrid.extractItem(i, 1, false);
            }

            if (!itemstack2.isEmpty()) {
                if (this.craftingGrid.getStackInSlot(i).isEmpty()) {
                    this.craftingGrid.setItemDirect(i, itemstack2);
                } else if (!this.player.getInventory().add(itemstack2)) {
                    this.player.drop(itemstack2, false);
                }
            }
        }
    }

    /**
     * Overrides what is being shown as the crafting output, but doesn't notify parent menu.
     */
    public void setDisplayedCraftingOutput(ItemStack stack) {
        getInventory().setItemDirect(0, stack);
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack remove(int par1) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(par1, this.getItem().getCount());
        }

        return super.remove(par1);
    }

    // TODO: This is really hacky and NEEDS to be solved with a full menu/gui
    // refactoring.
    protected NonNullList<ItemStack> getRemainingItems(CraftingContainer ic, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, ic, level)
                .map(iCraftingRecipe -> iCraftingRecipe.getRemainingItems(ic))
                .orElse(NonNullList.withSize(9, ItemStack.EMPTY));
    }
}
