package dev.beecube31.crazyae2.common.features.subfeatures;

import dev.beecube31.crazyae2.common.features.IFeature;

import javax.annotation.Nullable;

public interface ISubFeature extends IFeature {
	String name();

	@Nullable
	String getDescription();

	void setEnabled(boolean enabled);

	@Nullable
	String getMixin();
}
