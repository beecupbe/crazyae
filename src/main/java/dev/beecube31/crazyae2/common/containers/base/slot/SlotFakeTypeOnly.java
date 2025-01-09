package dev.beecube31.crazyae2.common.containers.base.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotFakeTypeOnly extends SlotFake {

    public SlotFakeTypeOnly(final IItemHandler inv, final int idx, final int x, final int y) {
        super(inv, idx, x, y);
    }

    @Override
    public void putStack(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
            if (is.getCount() > 1) {
                is.setCount(1);
            } else if (is.getCount() < -1) {
                is.setCount(-1);
            }
        }

        super.putStack(is);
    }
}
