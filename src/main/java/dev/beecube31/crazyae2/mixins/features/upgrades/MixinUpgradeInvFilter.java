package dev.beecube31.crazyae2.mixins.features.upgrades;

import appeng.parts.automation.UpgradeInventory;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeInventory;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = { "appeng.parts.automation.UpgradeInventory$UpgradeInvFilter" }, remap = false)
public class MixinUpgradeInvFilter {
	@Shadow @Final UpgradeInventory this$0;

	@Inject(method = "allowInsert", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
		shift = At.Shift.AFTER,
		remap = true
	), cancellable = true)
	private void injectAllowInsert(IItemHandler inv, int slot, ItemStack itemstack, CallbackInfoReturnable<Boolean> cir) {
		if (this.this$0 instanceof ICrazyAEUpgradeInventory te && itemstack.getItem() instanceof CrazyAEUpgradeModule upgrade) {
			var u = upgrade.getType(itemstack);
			if (u != null) {
				cir.setReturnValue(te.getInstalledUpgrades(u) < te.getMaxInstalled(u));
			}
		}
	}
}