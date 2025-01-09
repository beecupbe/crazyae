package dev.beecube31.crazyae2.common.features;

import dev.beecube31.crazyae2.common.features.subfeatures.*;
import dev.beecube31.crazyae2.common.features.subfeatures.ISubFeature;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.EnumSet;

public enum Features implements IFeature {
	STUB,

	UPGRADES(EnumSet.allOf(UpgradeFeatures.class), "upgrades"),
	DENSE_CELLS(EnumSet.allOf(DenseCellFeatures.class)),
	MEGA_DENSE_CELLS(EnumSet.allOf(MegaDenseCellFeatures.class)),

	MANA_CELLS("manastorage", "botania"),
	MANA_DENSE_CELLS("manastorage", "botania"),
	MEGA_MANA_DENSE_CELLS("manastorage", "botania"),
	MANA_TERM("manastorage", "botania"),
	BOTANIA_MECHANICAL_BLOCKS(false, "botania"),
	BOTANIA_JEI_INTEGRATION("botaniajei", "botania"),

	PORTABLE_DENSE_CELLS,
	IMPROVED_DRIVE,
	IMPROVED_IO_PORT,
	BIG_CRYSTAL_CHARGER,
	CRAFTING_UNITS_COMBINER("cu.combiner"),
	IMPROVED_GRINDSTONE_CRANK,
	IMPROVED_BUSES,
	MANA_BUSES(false, "botania"),
	IMPROVED_ENERGY_CELLS,
	SOLAR_PANELS,
	QUANTUM_CHANNELS_MULTIPLIER("qcm"),
	PATTERNS_INTERFACE("patterns.interface"),

//	QUANTUM_WIRELESS_ACCESS_POINT("qwap"),

	DENSE_CPU_COPROCESSORS("dense.coprocessor"),
	MEGA_DENSE_CPU_COPROCESSORS("dense.coprocessor");

	private String[] mixins;
	private String modid;
	private EnumSet<? extends ISubFeature> subFeatures = null;
	private boolean enabled;

	Features() {}

	Features(String mixins) {
		this();
		this.mixins = new String[]{ mixins };
	}

	Features(String mixins, String modid) {
		this();
		this.modid = modid;
		this.mixins = new String[]{ mixins };
	}

	Features(boolean stub, String modid) {
		this();
		this.modid = modid;
	}

	Features(EnumSet<? extends ISubFeature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	Features(EnumSet<? extends ISubFeature> subFeatures, String mixins) {
		this(subFeatures);

		this.mixins = new String[]{ mixins };
	}

	public boolean isEnabled() {
		if (this.modid != null && !Loader.isModLoaded(this.modid)) {
			return false;
		}

		return this.enabled || this == STUB;
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

	@Nullable
	public String getRequiredModid() {
		return this.modid;
	}
}
