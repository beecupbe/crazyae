package dev.beecube31.crazyae2.common.features.subfeatures;

import org.jetbrains.annotations.Nullable;

public enum UpgradeFeatures implements ISubFeature {
	STACKS("Enable Stacks Card", null),
	IMPROVED_SPEED_UPGRADE("Enable Improved Speed Card", null),
	ADVANCED_SPEED_UPGRADE("Enable Advanced Speed Card", null);


	private final String description;
	private final String mixins;
	private boolean enabled;

	UpgradeFeatures(String description, String mixins) {
		this.description = description;
		this.mixins = mixins;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Nullable
	@Override
	public String getMixins() {
		return this.mixins;
	}
}
