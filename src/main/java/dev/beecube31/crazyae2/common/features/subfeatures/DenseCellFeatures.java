package dev.beecube31.crazyae2.common.features.subfeatures;

import org.jetbrains.annotations.Nullable;

public enum DenseCellFeatures implements ISubFeature {
	DENSE_CPU_STORAGE_UNITS("Add CPU storage counterparts (256KB-16MB)");

	private final String description;
	private boolean enabled;

	DenseCellFeatures(String description) {
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
