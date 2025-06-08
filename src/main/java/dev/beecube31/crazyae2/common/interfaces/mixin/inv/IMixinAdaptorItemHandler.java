package dev.beecube31.crazyae2.common.interfaces.mixin.inv;

import net.minecraft.item.ItemStack;

public interface IMixinAdaptorItemHandler {
    boolean crazyae$isInventoryFull();

    long crazyae$estimateInsertableAmount(ItemStack template);
}
