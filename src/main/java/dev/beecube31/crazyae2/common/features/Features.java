package dev.beecube31.crazyae2.common.features;

import dev.beecube31.crazyae2.common.features.subfeatures.*;
import dev.beecube31.crazyae2.common.features.subfeatures.ISubFeature;

import javax.annotation.Nullable;
import java.util.EnumSet;

public enum Features implements IFeature {
	STUB,

	UPGRADES(EnumSet.allOf(UpgradeFeatures.class), "upgrades"),
	DENSE_CELLS(EnumSet.allOf(DenseCellFeatures.class)),
	MEGA_DENSE_CELLS(EnumSet.allOf(MegaDenseCellFeatures.class)),
	PORTABLE_DENSE_CELLS,
	IMPROVED_DRIVE,
	IMPROVED_IO_PORT,
	IMPROVED_GRINDSTONE_CRANK,
	IMPROVED_BUSES,
	MANA_BUSES,
	IMPROVED_ENERGY_CELLS,
	SOLAR_PANELS,
	QUANTUM_CHANNELS_MULTIPLIER("qcm"),
	PATTERNS_INTERFACE,


	DENSE_CPU_COPROCESSORS("dense.coprocessor"),
	MEGA_DENSE_CPU_COPROCESSORS("dense.coprocessor"),
	QUANTUM_WIRELESS_BOOSTER("wireless.booster");

	private String[] mixins;
	private EnumSet<? extends ISubFeature> subFeatures = null;
	private boolean enabled;

	Features() {}

	Features(String mixins) {
		this();
		this.mixins = new String[]{ mixins };
	}

	Features(EnumSet<? extends ISubFeature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	Features(EnumSet<? extends ISubFeature> subFeatures, String mixins) {
		this(subFeatures);

		this.mixins = new String[]{ mixins };
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Nullable
	public EnumSet<? extends ISubFeature> getSubFeatures() {
		return this.subFeatures;
	}

	@Nullable
	public String[] getMixins() {
		return this.mixins;
	}
}
