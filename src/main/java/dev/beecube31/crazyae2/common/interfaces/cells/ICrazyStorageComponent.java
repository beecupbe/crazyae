package dev.beecube31.crazyae2.common.interfaces.cells;

import appeng.api.implementations.items.IStorageComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ICrazyStorageComponent extends IStorageComponent {

    /**
     * This isn't necessarily the same as if you make a storage cell out of it, but all of AE's default cells do it that
     * way, its currently only used for the condenser.
     *
     * @param is item
     *
     * @return number of bytes
     */
    long getBytesLong(ItemStack is);
}
