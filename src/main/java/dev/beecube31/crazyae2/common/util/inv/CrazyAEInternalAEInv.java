package dev.beecube31.crazyae2.common.util.inv;

import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.AELog;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.util.inv.iterator.CrazyAEInvIterator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class CrazyAEInternalAEInv implements IItemHandlerModifiable, Iterable<IAEItemStack> {
    private final IAEAppEngInventory te;
    private final IAEItemStack[] inv;
    private final int size;
    private int maxStack;
    private boolean dirtyFlag = false;

    public CrazyAEInternalAEInv(final IAEAppEngInventory te, final int s) {
        this.te = te;
        this.size = s;
        this.maxStack = 64;
        this.inv = new IAEItemStack[s];
    }

    public CrazyAEInternalAEInv(final IAEAppEngInventory te, final int s, int stackSize) {
        this.te = te;
        this.size = s;
        this.maxStack = stackSize;
        this.inv = new IAEItemStack[s];
    }

    public void setMaxStackSize(final int s) {
        this.maxStack = s;
    }

    public IAEItemStack getAEStackInSlot(final int var1) {
        return this.inv[var1];
    }

    public void writeToNBT(final NBTTagCompound data, final String name) {
        final NBTTagCompound c = new NBTTagCompound();
        this.writeToNBT(c);
        data.setTag(name, c);
    }

    private void writeToNBT(final NBTTagCompound target) {
        for (int x = 0; x < this.size; x++) {
            try {
                final NBTTagCompound c = new NBTTagCompound();

                if (this.inv[x] != null) {
                    this.inv[x].writeToNBT(c);
                }

                target.setTag("#" + x, c);
            } catch (final Exception ignored) {
            }
        }
    }

    public void readFromNBT(final NBTTagCompound data, final String name) {
        final NBTTagCompound c = data.getCompoundTag(name);
        this.readFromNBT(c);
    }

    private void readFromNBT(final NBTTagCompound target) {
        for (int x = 0; x < this.size; x++) {
            try {
                final NBTTagCompound c = target.getCompoundTag("#" + x);

                this.inv[x] = AEItemStack.fromNBT(c);
            } catch (final Exception e) {
                AELog.debug(e);
            }
        }
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public int getSlots() {
        return this.size;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(final int var1) {
        if (this.inv[var1] == null) {
            return ItemStack.EMPTY;
        }

        return this.inv[var1].createItemStack();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = this.getStackInSlot(slot);
        int limit = this.getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.inv[slot] = AEApi.instance()
                        .storage()
                        .getStorageChannel(IItemStorageChannel.class)
                        .createStack(
                                reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.fireOnChangeInventory(slot, InvOperation.INSERT, ItemStack.EMPTY,
                    reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.inv[slot] != null) {
            final ItemStack split = this.getStackInSlot(slot);

            if (amount >= split.getCount()) {
                if (!simulate) {
                    this.inv[slot] = null;
                    this.fireOnChangeInventory(slot, InvOperation.EXTRACT, split, ItemStack.EMPTY);
                }
                return split;
            } else {
                if (!simulate) {
                    split.grow(-amount);
                    this.fireOnChangeInventory(slot, InvOperation.EXTRACT, ItemHandlerHelper.copyStackWithSize(split, amount), ItemStack.EMPTY);
                }
                return ItemHandlerHelper.copyStackWithSize(split, amount);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(final int slot, final @NotNull ItemStack newItemStack) {
        ItemStack oldStack = this.getStackInSlot(slot).copy();
        this.inv[slot] = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(newItemStack);

        if (this.te != null && Platform.isServer()) {
            ItemStack newStack = newItemStack.copy();
            InvOperation op = InvOperation.SET;

            if (ItemStack.areItemsEqual(oldStack, newStack)) {
                if (newStack.getCount() > oldStack.getCount()) {
                    newStack.shrink(oldStack.getCount());
                    oldStack = ItemStack.EMPTY;
                    op = InvOperation.INSERT;
                } else {
                    oldStack.shrink(newStack.getCount());
                    newStack = ItemStack.EMPTY;
                    op = InvOperation.EXTRACT;
                }
            }
            this.fireOnChangeInventory(slot, op, oldStack, newStack);
        }
    }

    private void fireOnChangeInventory(int slot, InvOperation op, ItemStack removed, ItemStack inserted) {
        if (this.te != null && Platform.isServer() && !this.dirtyFlag) {
            this.dirtyFlag = true;
            this.te.onChangeInventory(this, slot, op, removed, inserted);
            this.te.saveChanges();
            this.dirtyFlag = false;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.maxStack;
    }

    @Override
    public @NotNull Iterator<IAEItemStack> iterator() {
        return new CrazyAEInvIterator(this);
    }

    public Iterator<IAEItemStack> getNewIterator() {
        return new CrazyAEInvIterator(this);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
