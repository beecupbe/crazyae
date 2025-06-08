package dev.beecube31.crazyae2.mixins.core.inv;

import appeng.util.InventoryAdaptor;
import appeng.util.inv.AdaptorItemHandler;
import dev.beecube31.crazyae2.common.interfaces.mixin.inv.IMixinAdaptorItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AdaptorItemHandler.class, remap = false)
public abstract class MixinAdaptorItemHandler extends InventoryAdaptor implements IMixinAdaptorItemHandler {
    @Shadow @Final protected IItemHandler itemHandler;

    @Override
    public boolean crazyae$isInventoryFull() {
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            ItemStack stackInSlot = this.itemHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty()) {
                return false;
            } else {
                if (stackInSlot.isStackable() && stackInSlot.getCount() < this.itemHandler.getSlotLimit(i) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public long crazyae$estimateInsertableAmount(ItemStack template) {
        if (template == null || template.isEmpty()) {
            return 0;
        }

        long totalCanInsert = 0;

        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            if (this.itemHandler.isItemValid(i, template)) {
                ItemStack stackInSlot = this.itemHandler.getStackInSlot(i);
                int slotLimit = this.itemHandler.getSlotLimit(i);
                int itemMaxStack = template.getMaxStackSize();
                int actualLimit = Math.min(slotLimit, itemMaxStack);

                if (stackInSlot.isEmpty()) {
                    totalCanInsert += actualLimit;
                } else {
                    int canAddToSlot = actualLimit - stackInSlot.getCount();
                    if (canAddToSlot > 0) {
                        totalCanInsert += canAddToSlot;
                    }
                }
            }
        }
        return totalCanInsert;
    }
}
