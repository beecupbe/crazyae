package dev.beecube31.crazyae2.common.features.subfeatures;

import org.jetbrains.annotations.Nullable;

public enum UltraDenseDeviceFeatures implements ISubFeature {
	ULTRA_DENSE_CPU_STORAGE_UNITS("Add CPU storage counterparts (8GB-128GB)");

	private final String description;
	private boolean enabled;

	UltraDenseDeviceFeatures(String description) {
		this.description = description;
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
	public String getMixin() {
		return null;
	}
}
