package dev.beecube31.crazyae2.mixins.aefixes.mac;

import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotRestrictedInput;
import dev.beecube31.crazyae2.common.items.patterns.ItemCustomEncodedPatternBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlotRestrictedInput.class, remap = false)
public abstract class MixinSlotRestrictedInput extends AppEngSlot {
    public MixinSlotRestrictedInput(IItemHandler inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }

    @Inject(
            method = "isItemValid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.BEFORE,
                    ordinal = 1
            ),
            remap = false,
            cancellable = true
    )
    private void crazyae$isItemValid(ItemStack i, CallbackInfoReturnable<Boolean> cir) {
        if (i.getItem() instanceof ItemCustomEncodedPatternBase) {
            cir.setReturnValue(false);
        }
    }
}
