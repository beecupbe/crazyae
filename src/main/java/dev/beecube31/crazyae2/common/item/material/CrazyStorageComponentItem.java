package dev.beecube31.crazyae2.common.item.material;

import appeng.api.implementations.items.IStorageComponent;
import appeng.items.AEBaseItem;
import net.minecraft.world.item.ItemStack;


public class CrazyStorageComponentItem extends AEBaseItem implements IStorageComponent {
    private final long storageInKb;

    public CrazyStorageComponentItem(Properties properties, long storageInKb) {
        super(properties);
        this.storageInKb = storageInKb;
    }

    public long getBytesLong() {
        return this.storageInKb * 1024L;
    }

    @Override
    public int getBytes(ItemStack is) {
        return (int) Math.min(Integer.MAX_VALUE / 8, getBytesLong());
    }

    @Override
    public boolean isStorageComponent(ItemStack is) {
        return true;
    }
}
