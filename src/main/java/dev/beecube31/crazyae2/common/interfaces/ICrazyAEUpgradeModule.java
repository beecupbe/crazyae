package dev.beecube31.crazyae2.common.interfaces;

import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import net.minecraft.item.ItemStack;

public interface ICrazyAEUpgradeModule {
	Upgrades.UpgradeType getType(ItemStack is);
}
