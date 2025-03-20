package dev.beecube31.crazyae2.mixins.features.upgrades;

import appeng.container.slot.SlotRestrictedInput;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlotRestrictedInput.class)
public class MixinSlotRestrictedInput {
	@Final @Shadow(remap = false) private SlotRestrictedInput.PlacableItemType which;

	@Inject(method = "isItemValid", at = @At(
		value = "INVOKE",
		target = "Lappeng/api/definitions/IDefinitions;items()Lappeng/api/definitions/IItems;",
		remap = false
	), cancellable = true)
	private void injectValidityCheck(ItemStack is, CallbackInfoReturnable<Boolean> cir) {
		if (this.which == SlotRestrictedInput.PlacableItemType.UPGRADES) {
			if (is.getItem() instanceof CrazyAEUpgradeModule niu && niu.getType(is) != null) {
				cir.setReturnValue(true);
			}
		}
	}
}
