package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.container.slot.IOptionalSlotHost;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class OptionalSlotFakeTypeOnly extends OptionalSlotFake {

    public OptionalSlotFakeTypeOnly(final IItemHandler inv, final IOptionalSlotHost containerBus, final int idx, final int x, final int y, final int offX, final int offY, final int groupNum) {
        super(inv, containerBus, idx, x, y, offX, offY, groupNum);
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
