package dev.beecube31.crazyae2.client.gui;

import appeng.util.helpers.ItemHandlerUtil;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.interfaces.gui.IColorizeableSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class CrazyAESlot extends Slot implements IColorizeableSlot {
    private static final IInventory emptyInventory = new InventoryBasic("[undefined]", true, 0);
    private final IItemHandler itemHandler;
    private final int index;

    private final int defX;
    private final int defY;
    private boolean isDraggable = true;
    private boolean isPlayerSide = false;
    private CrazyAEBaseContainer myContainer = null;
    private StateSprite IIcon;
    private hasCalculatedValidness isValid;
    private boolean isDisplay = false;
    private boolean returnAsSingleStack;

    public CrazyAESlot(final IItemHandler inv, final int idx, final int x, final int y) {
        super(emptyInventory, idx, x, y);
        this.itemHandler = inv;
        this.index = idx;

        this.defX = x;
        this.defY = y;
        this.setIsValid(hasCalculatedValidness.NotAvailable);
    }

    public CrazyAESlot setNotDraggable() {
        this.setDraggable(false);
        return this;
    }

    public CrazyAESlot setPlayerSide() {
        this.isPlayerSide = true;
        return this;
    }

    public String getTooltip() {
        return null;
    }

    public void clearStack() {
        ItemHandlerUtil.setStackInSlot(this.itemHandler, this.index, ItemStack.EMPTY);
    }

    @Override
    public boolean isItemValid(@Nonnull final ItemStack par1ItemStack) {
        if (this.isSlotEnabled()) {
            return this.itemHandler.isItemValid(this.index, par1ItemStack);
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        if (!this.isSlotEnabled()) {
            return ItemStack.EMPTY;
        }

        if (this.itemHandler
                .getSlots() <=
                this.getSlotIndex()) {
            return ItemStack.EMPTY;
        }


        if (this.isDisplay()) {
            this.setDisplay(false);
            if (this.returnAsSingleStack()) {
                setReturnAsSingleStack(false);
                ItemStack ret = this.getDisplayStack().copy();
                ret.setCount(1);
                return ret;
            }
            return this.getDisplayStack();
        }

        return this.itemHandler.getStackInSlot(this.index);
    }

    private boolean returnAsSingleStack() {
        return this.returnAsSingleStack;
    }

    public void setReturnAsSingleStack(boolean returnAsSingleStack) {
        this.returnAsSingleStack = returnAsSingleStack;
    }

    @Override
    public void putStack(final ItemStack stack) {
        if (this.isSlotEnabled()) {
            ItemHandlerUtil.setStackInSlot(this.itemHandler, this.index, stack);

            if (this.getContainer() != null) {
                this.getContainer().onSlotChange(this);
            }
        }
    }

    public boolean disableInteraction() {
        return false;
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    @Override
    public void onSlotChanged() {
        this.setIsValid(hasCalculatedValidness.NotAvailable);
        if (this.isSlotEnabled() && this.itemHandler != null) {
            ItemHandlerUtil.setStackInSlot(this.itemHandler, this.index, this.getStack().copy());

            if (this.getContainer() != null) {
                this.getContainer().onSlotChange(this);
            }
        }
        super.onSlotChanged();
    }

    @Override
    public int getSlotStackLimit() {
        return this.itemHandler.getSlotLimit(this.index);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        if (this.isSlotEnabled()) {
            var draggedStack = par1EntityPlayer.inventory.getItemStack();
            ItemStack slotStack = this.getStack();


            if (!draggedStack.isEmpty()) {
                if (draggedStack.isItemEqual(slotStack)) {
                    if (draggedStack.getCount() >= draggedStack.getMaxStackSize()) {
                        return false;
                    }
                } else if (slotStack.getCount() > slotStack.getMaxStackSize()) {
                    return false;
                }
            }
        }

        if (this.isSlotEnabled()) {
            return !this.itemHandler.extractItem(this.index, Integer.MAX_VALUE, true).isEmpty();
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return this.itemHandler.extractItem(this.index, amount, false);
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof CrazyAESlot && ((CrazyAESlot) other).itemHandler == this.itemHandler;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isEnabled() {
        return this.isSlotEnabled();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CrazyAESlot getTarget() {
        return this;
    }

    public boolean isSlotEnabled() {
        return true;
    }

    public ItemStack getDisplayStack() {
        return this.itemHandler.getStackInSlot(this.index);
    }

    public float getOpacityOfIcon() {
        return 0.4f;
    }

    public boolean renderIconWithItem() {
        return false;
    }

    public StateSprite getIcon() {
        return this.getIIcon();
    }

    public boolean isPlayerSide() {
        return this.isPlayerSide;
    }

    public boolean shouldDisplay() {
        return this.isSlotEnabled();
    }

    public int getX() {
        return this.defX;
    }

    public int getY() {
        return this.defY;
    }

    private StateSprite getIIcon() {
        return this.IIcon;
    }

    public Slot setIIcon(final StateSprite iIcon) {
        this.IIcon = iIcon;
        return this;
    }

    private boolean isDisplay() {
        return this.isDisplay;
    }

    public void setDisplay(final boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public boolean isDraggable() {
        return this.isDraggable;
    }

    private void setDraggable(final boolean isDraggable) {
        this.isDraggable = isDraggable;
    }

    void setPlayerSide(final boolean isPlayerSide) {
        this.isPlayerSide = isPlayerSide;
    }

    public hasCalculatedValidness getIsValid() {
        return this.isValid;
    }

    public void setIsValid(final hasCalculatedValidness isValid) {
        this.isValid = isValid;
    }

    protected CrazyAEBaseContainer getContainer() {
        return this.myContainer;
    }

    public void setContainer(final CrazyAEBaseContainer myContainer) {
        this.myContainer = myContainer;
    }

    public enum hasCalculatedValidness {
        NotAvailable, Valid, Invalid
    }
}
