package dev.beecube31.crazyae2.common.features;

import dev.beecube31.crazyae2.common.features.subfeatures.DenseCellFeatures;
import dev.beecube31.crazyae2.common.features.subfeatures.ISubFeature;
import dev.beecube31.crazyae2.common.features.subfeatures.MegaDenseCellFeatures;
import dev.beecube31.crazyae2.common.features.subfeatures.UpgradeFeatures;
import dev.beecube31.crazyae2.common.util.FeatureSet;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.EnumSet;

public enum Features implements IFeature {
	STUB,

	UPGRADES(
			new FeatureSet().add(EnumSet.allOf(UpgradeFeatures.class)).add("upgrades").add("mixin:upgrades")
	),
	DENSE_CELLS(
			new FeatureSet().add(EnumSet.allOf(DenseCellFeatures.class))
	),
	MEGA_DENSE_CELLS(
			new FeatureSet().add(EnumSet.allOf(MegaDenseCellFeatures.class))
	),

	PARTS(),

	ENERGY_TERM(),

	PORTABLE_DENSE_CELLS,
	IMPROVED_DRIVE,
	IMPROVED_IO_PORT,
	BIG_CRYSTAL_CHARGER,
	IMPROVED_GRINDSTONE_CRANK,
	IMPROVED_BUSES,
	IMPROVED_ENERGY_CELLS,
	SOLAR_PANELS,
	PART_DRIVE,
	PERFECT_INTERFACE(
			new FeatureSet().add("mixin:perfect.interface")
	),
	ENERGY_BUSES,

	IMPROVED_MOLECULAR_ASSEMBLER(
			new FeatureSet().add("mixin:patternterm.fastplace")
	),
	MANA_CELLS(
			new FeatureSet().add("mixin:manastorage").add("modid:botania")
	),
	MANA_DENSE_CELLS(
			new FeatureSet().add("mixin:manastorage").add("modid:botania")
	),
	MEGA_MANA_DENSE_CELLS(
			new FeatureSet().add("mixin:manastorage").add("modid:botania")
	),
	MANA_TERM(
			new FeatureSet().add("mixin:manastorage").add("modid:botania")
	),
	BOTANIA_MECHANICAL_BLOCKS(
			new FeatureSet().add("modid:botania")
	),
	BOTANIA_JEI_INTEGRATION(
			new FeatureSet().add("mixin:botaniajei").add("modid:botania")
	),


	ENERGY_CELLS(),
	ENERGY_DENSE_CELLS(),
	MEGA_ENERGY_DENSE_CELLS(),

	CRAFTING_UNITS_COMBINER(
			new FeatureSet().add("mixin:cu.combiner")
	),

	MANA_BUSES(
			new FeatureSet().add("modid:botania")
	),
	QUANTUM_CHANNELS_MULTIPLIER(
			new FeatureSet().add("mixin:qcm")
	),
	PATTERNS_INTERFACE(
			new FeatureSet().add("mixin:patterns.interface")
	),

	DENSE_CPU_COPROCESSORS(
			new FeatureSet().add("mixin:patterns.interface")
	),
	MEGA_DENSE_CPU_COPROCESSORS(
			new FeatureSet().add("mixin:dense.coprocessor")
	);

	private String[] mixins;
	private String modid;
	private EnumSet<? extends ISubFeature> subFeatures = null;
	private boolean enabled;

	Features() {}

	Features(FeatureSet attribs) {
		this();

		for (Object obj : attribs.get()) {
			if (obj instanceof String attrib) {
				if (attrib.contains("mixin:")) {
					this.mixins = new String[]{attrib.substring(6)};
				}

				if (attrib.contains("modid:")) {
					this.modid = attrib.substring(6);
				}

				return;
			}

			if (obj instanceof EnumSet attrib) {
				this.subFeatures = attrib;
			}

		}
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
