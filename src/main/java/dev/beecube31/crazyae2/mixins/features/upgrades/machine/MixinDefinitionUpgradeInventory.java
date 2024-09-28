package dev.beecube31.crazyae2.mixins.features.upgrades.machine;

import appeng.api.definitions.IItemDefinition;
import appeng.parts.automation.DefinitionUpgradeInventory;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import dev.beecube31.crazyae2.mixins.features.upgrades.MixinUpgradeInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = DefinitionUpgradeInventory.class, remap = false)
public class MixinDefinitionUpgradeInventory extends MixinUpgradeInventory {
	@Shadow @Final private IItemDefinition definition;

	@Unique
	@Override
	public int getMaxInstalled(Upgrades.UpgradeType upgrades) {
		var max = 0;

		for (final ItemStack stack : upgrades.getSupported().keySet()) {
			if (this.definition.isSameAs(stack)) {
				max = upgrades.getSupported().get(stack);
				break;
			}
		}

		return max;
	}
}
