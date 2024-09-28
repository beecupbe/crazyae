package dev.beecube31.crazyae2.mixins.features.upgrades;

import appeng.parts.automation.UpgradeInventory;
import com.llamalad7.mixinextras.sugar.Local;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeInventory;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = UpgradeInventory.class, remap = false)
public abstract class MixinUpgradeInventory implements ICrazyAEUpgradeInventory {
	@Unique
	private final HashMap<Upgrades.UpgradeType, Integer> crazyae$installedUpgrades = new HashMap<>();
	@Shadow
	private boolean cached;

	@Shadow
	private void updateUpgradeInfo() {}

	@Override
	@Unique
	public int getInstalledUpgrades(Upgrades.UpgradeType u) {
		if (!this.cached) {
			this.updateUpgradeInfo();
		}

		return this.crazyae$installedUpgrades.getOrDefault(u, 0);
	}

	@Override
	@Unique
	public abstract int getMaxInstalled(Upgrades.UpgradeType u);

	@Inject(method = "updateUpgradeInfo", at = @At("HEAD"))
	private void injectUpdateUpgradeInfo(CallbackInfo ci) {
		this.crazyae$installedUpgrades.clear();
	}

	@Inject(method = "updateUpgradeInfo", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
		shift = At.Shift.AFTER,
		remap = true,
		ordinal = 0
	))
	private void injectUpdateUpgradeInfoIS(CallbackInfo ci, @Local ItemStack is) {
		var item = is.getItem();
		if (item instanceof CrazyAEUpgradeModule niu) {
			var type = niu.getType(is);
			this.crazyae$installedUpgrades.put(type, this.crazyae$installedUpgrades.getOrDefault(type, 0) + 1);
		}
	}

	@Override
	public void markDirty() {
		this.cached = false;
	}
}
