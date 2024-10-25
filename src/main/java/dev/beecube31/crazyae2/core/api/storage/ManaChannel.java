package dev.beecube31.crazyae2.core.api.storage;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

public final class ManaChannel implements IManaStorageChannel {
    @Override
    @NotNull
    public IItemList<IAEItemStack> createList() {
        return new ItemList();
    }

    @Override
    public IAEItemStack createStack(@NotNull Object input) {
        Preconditions.checkNotNull(input);

        if (input instanceof ItemStack) {
            return AEItemStack.fromItemStack((ItemStack) input);
        }

        return null;
    }

    @Override
    public IAEItemStack createFromNBT(@NotNull NBTTagCompound nbt) {
        Preconditions.checkNotNull(nbt);
        return AEItemStack.fromNBT(nbt);
    }

    @Override
    public IAEItemStack readFromPacket(@NotNull ByteBuf input) {
        Preconditions.checkNotNull(input);

        return AEItemStack.fromPacket(input);
    }
}
