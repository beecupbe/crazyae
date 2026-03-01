package dev.beecube31.crazyae2.common.containers.base.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class BotaniaSlotFake extends SlotFake {

    public BotaniaSlotFake(final IItemHandler inv, final int idx, final int x, final int y) {
        super(inv, idx, x, y);
    }

    public BotaniaSlotFake(final IItemHandler inv, final int idx, final int x, final int y, final boolean oneStack) {
        super(inv, idx, x, y, oneStack);
    }

    @Override
    public void putStack(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
            is.setCount(1);
        }

        super.putStack(is);
    }
}
