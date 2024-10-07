package dev.beecube31.crazyae2.common.features.subfeatures;

import org.jetbrains.annotations.Nullable;

public enum MegaDenseCellFeatures implements ISubFeature {
	MEGA_DENSE_CPU_STORAGE_UNITS("Add biggest CPU storage counterparts (64MB-2GB)");

	private final String description;
	private boolean enabled;

	MegaDenseCellFeatures(String description) {
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
	public String getMixins() {
		return null;
	}
}
