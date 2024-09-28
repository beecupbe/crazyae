package dev.beecube31.crazyae2.mixins.features.upgrades;

import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng/items/contents/NetworkToolViewer$NetworkToolInventoryFilter", remap = false)
public class MixinNetworkToolViewer {

	@Inject(method = "allowInsert", at = @At("RETURN"), cancellable = true, remap = false)
	private void patchUpgrades(IItemHandler inv, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof CrazyAEUpgradeModule) {
			cir.setReturnValue(true);
		}
	}
}
