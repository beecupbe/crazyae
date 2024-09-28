package dev.beecube31.crazyae2.common.interfaces;

import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;

public interface ICrazyAEUpgradeInventory {
	int getInstalledUpgrades(Upgrades.UpgradeType u);

	int getMaxInstalled(Upgrades.UpgradeType u);

	void markDirty();
}
