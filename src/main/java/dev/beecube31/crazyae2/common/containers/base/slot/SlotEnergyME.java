package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.common.interfaces.gui.IStacksizeRenderTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SlotEnergyME extends SlotItemHandler implements IStacksizeRenderTarget {

    private final InternalEnergySlotME slot;

    public SlotEnergyME(InternalEnergySlotME slot) {
        super(null, 0, slot.getxPosition(), slot.getyPosition());
        this.slot = slot;
    }

    @Override
    public boolean isItemValid(final ItemStack par1ItemStack) {
        return false;
    }

    public IAEItemStack getAEStack() {
        if (this.slot.hasPower()) {
            return this.slot.getAEStack();
        }
        return null;
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        if (this.slot.hasPower()) {
            return this.slot.getStack();
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getDefinition() {
        if (this.slot.hasPower()) {
            return this.slot.getDefinition();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean getHasStack() {
        if (this.slot.hasPower()) {
            return !this.getStack().isEmpty();
        }
        return false;
    }

    @Override
    public void putStack(final @NotNull ItemStack par1ItemStack) {}

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Nonnull
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

    @Override
    public ItemStack getDisplayStack() {
        return this.slot.getStack();
    }

    @Override
    public int xPos() {
        return this.slot.getxPosition();
    }

    @Override
    public int yPos() {
        return this.slot.getyPosition();
    }
}
