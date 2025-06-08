package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IItems;
import appeng.bootstrap.components.IPostInitComponent;
import appeng.core.AEConfig;
import appeng.core.features.ItemDefinition;
import dev.beecube31.crazyae2.client.rendering.*;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.items.Colorizer;
import dev.beecube31.crazyae2.common.items.ManaConnector;
import dev.beecube31.crazyae2.common.items.QuantumWirelessBooster;
import dev.beecube31.crazyae2.common.items.cells.energy.MultiEnergyItemCell;
import dev.beecube31.crazyae2.common.items.cells.energy.MultiEnergyItemCreativeCell;
import dev.beecube31.crazyae2.common.items.cells.storage.*;
import dev.beecube31.crazyae2.common.items.internal.ExperienceAsAEStack;
import dev.beecube31.crazyae2.common.items.internal.InternalStubItem;
import dev.beecube31.crazyae2.common.items.internal.ManaAsAEStack;
import dev.beecube31.crazyae2.common.items.internal.energy.*;
import dev.beecube31.crazyae2.common.items.patterns.*;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.Definitions;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.CrazyAESidedHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Items implements Definitions<IItemDefinition> {
	private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
	private final IItemDefinition storageCell256k;
	private final IItemDefinition storageCell1mb;
	private final IItemDefinition storageCell4mb;
	private final IItemDefinition storageCell16mb;
	private final IItemDefinition storageCell64mb;
	private final IItemDefinition storageCell256mb;
	private final IItemDefinition storageCell1gb;
	private final IItemDefinition storageCell2gb;

	private final IItemDefinition manaCell1k;
	private final IItemDefinition manaCell4k;
	private final IItemDefinition manaCell16k;
	private final IItemDefinition manaCell64k;
	private final IItemDefinition manaCell256k;
	private final IItemDefinition manaCell1mb;
	private final IItemDefinition manaCell4mb;
	private final IItemDefinition manaCell16mb;
	private final IItemDefinition manaCell64mb;
	private final IItemDefinition manaCell256mb;
	private final IItemDefinition manaCell1gb;
	private final IItemDefinition manaCell2gb;

	private final IItemDefinition energyCell1k;
	private final IItemDefinition energyCell4k;
	private final IItemDefinition energyCell16k;
	private final IItemDefinition energyCell64k;
	private final IItemDefinition energyCell256k;
	private final IItemDefinition energyCell1mb;
	private final IItemDefinition energyCell4mb;
	private final IItemDefinition energyCell16mb;
	private final IItemDefinition energyCell64mb;
	private final IItemDefinition energyCell256mb;
	private final IItemDefinition energyCell1gb;
	private final IItemDefinition energyCell2gb;

//	private final IItemDefinition experienceCell1k;
//	private final IItemDefinition experienceCell4k;
//	private final IItemDefinition experienceCell16k;
//	private final IItemDefinition experienceCell64k;
//	private final IItemDefinition experienceCell256k;
//	private final IItemDefinition experienceCell1mb;
//	private final IItemDefinition experienceCell4mb;
//	private final IItemDefinition experienceCell16mb;
//	private final IItemDefinition experienceCell64mb;
//	private final IItemDefinition experienceCell256mb;
//	private final IItemDefinition experienceCell1gb;
//	private final IItemDefinition experienceCell2gb;


	private final IItemDefinition creativeManaCell;
	private final IItemDefinition creativeEnergyCell;

	private final IItemDefinition improvedPortableCell;
	private final IItemDefinition advancedPortableCell;
	private final IItemDefinition perfectPortableCell;

	private final IItemDefinition fluidCell256k;
	private final IItemDefinition fluidCell1mb;
	private final IItemDefinition fluidCell4mb;
	private final IItemDefinition fluidCell16mb;
	private final IItemDefinition fluidCell64mb;
	private final IItemDefinition fluidCell256mb;
	private final IItemDefinition fluidCell1gb;
	private final IItemDefinition fluidCell2gb;

	private final IItemDefinition quantumWirelessBooster;
	private final IItemDefinition manaConnector;
//	private final IItemDefinition patternsUSBStick;

	private final IItemDefinition manaAsAEStack;
	private final IItemDefinition expAsAEStack;
	private final IItemDefinition EFEnergyAsAeStack;
	private final IItemDefinition FEEnergyAsAeStack;
	private final IItemDefinition EUEnergyAsAeStack;
	private final IItemDefinition SEEnergyAsAeStack;
	private final IItemDefinition QEEnergyAsAeStack;


	private final IItemDefinition elventradeEncodedPattern;
	private final IItemDefinition manapoolEncodedPattern;
	private final IItemDefinition petalEncodedPattern;
	private final IItemDefinition puredaisyEncodedPattern;
	private final IItemDefinition runealtarEncodedPattern;
	private final IItemDefinition teraplateEncodedPattern;
	private final IItemDefinition breweryEncodedPattern;

	private final IItemDefinition colorizer;
	private final IItemDefinition advNetTool;


	public Items(Registry registry) {
		this.manaConnector = this.registerById(registry.item("mana_connector", ManaConnector::new)
				.features(Features.MANA_BUSES)
				.ifModPresent("botania")
				.build());

//		this.patternsUSBStick = this.registerById(registry.item("patterns_usb_stick", PatternsUSBStick::new)
//				.features(Features.STUB)
//				.build());

		this.colorizer = this.registerById(registry.item("gui_colorizer", Colorizer::new)
				.features(Features.STUB)
				.build());

		this.advNetTool = this.registerById(registry.item("adv_net_tool", InternalStubItem::new)
				.features(Features.STUB)
				.setDisabled()
				.build());



		this.elventradeEncodedPattern = this.registerById(registry.item("elventrade_encoded_pattern", ElventradeEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new ElventradeEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.manapoolEncodedPattern = this.registerById(registry.item("manapool_encoded_pattern", ManapoolEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new ManapoolEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.petalEncodedPattern = this.registerById(registry.item("petal_encoded_pattern", PetalEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new PetalEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.puredaisyEncodedPattern = this.registerById(registry.item("puredaisy_encoded_pattern", PuredaisyEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new PuredaisyEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.runealtarEncodedPattern = this.registerById(registry.item("runealtar_encoded_pattern", RunealtarEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new RunealtarEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.teraplateEncodedPattern = this.registerById(registry.item("teraplate_encoded_pattern", TeraplateEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new TeraplateEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.breweryEncodedPattern = this.registerById(registry.item("brewery_encoded_pattern", BreweryEncodedPattern::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.rendering(new BreweryEncodedPatternRendering())
				.ifModPresent("botania")
				.build());




		this.storageCell256k = this.registerById(registry.item("storage_cell_256k", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_256K, 256 * 1024, 4D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell1mb = this.registerById(registry.item("storage_cell_1mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_1MB, 1024 * 1024, 6D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell4mb = this.registerById(registry.item("storage_cell_4mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_4MB, 4096 * 1024, 8D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell16mb = this.registerById(registry.item("storage_cell_16mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_16MB, 16384 * 1024, 12D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell64mb = this.registerById(registry.item("storage_cell_64mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_64MB, 65536 * 1024, 24D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell256mb = this.registerById(registry.item("storage_cell_256mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_256MB, 262144 * 1024, 40D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell1gb = this.registerById(registry.item("storage_cell_1gb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_1GB, 1048576 * 1024, 52D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell2gb = this.registerById(registry.item("storage_cell_2gb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_2GB, Integer.MAX_VALUE, 64D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());



		this.manaCell1k = this.registerById(registry.item("mana_cell_1k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1K, 1024, 1D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell4k = this.registerById(registry.item("mana_cell_4k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_4K, 4 * 1024, 2D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell16k = this.registerById(registry.item("mana_cell_16k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_16K, 16 * 1024, 3D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell64k = this.registerById(registry.item("mana_cell_64k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_64K, 64 * 1024, 4D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell256k = this.registerById(registry.item("mana_cell_256k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_256K, 256 * 1024, 4D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell1mb = this.registerById(registry.item("mana_cell_1mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1MB, 1024 * 1024, 6D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell4mb = this.registerById(registry.item("mana_cell_4mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_4MB, 4096 * 1024, 8D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell16mb = this.registerById(registry.item("mana_cell_16mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_16MB, 16384 * 1024, 12D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell64mb = this.registerById(registry.item("mana_cell_64mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_64MB, 65536 * 1024, 24D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell256mb = this.registerById(registry.item("mana_cell_256mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_256MB, 262144 * 1024, 40D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell1gb = this.registerById(registry.item("mana_cell_1gb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1GB, 1048576 * 1024, 52D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell2gb = this.registerById(registry.item("mana_cell_2gb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_2GB, Integer.MAX_VALUE, 64D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

//		this.experienceCell1k = this.registerById(registry.item("exp_cell_1k", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_1K, 1024, 1D))
//				.features(Features.EXPERIENCE_CELLS)
//				.build());
//
//		this.experienceCell4k = this.registerById(registry.item("exp_cell_4k", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_4K, 4 * 1024, 2D))
//				.features(Features.EXPERIENCE_CELLS)
//				.build());
//
//		this.experienceCell16k = this.registerById(registry.item("exp_cell_16k", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_16K, 16 * 1024, 3D))
//				.features(Features.EXPERIENCE_CELLS)
//				.build());
//
//		this.experienceCell64k = this.registerById(registry.item("exp_cell_64k", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_64K, 64 * 1024, 4D))
//				.features(Features.EXPERIENCE_CELLS)
//				.build());
//
//		this.experienceCell256k = this.registerById(registry.item("exp_cell_256k", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_256K, 256 * 1024, 4D))
//				.features(Features.EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell1mb = this.registerById(registry.item("exp_cell_1mb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_1MB, 1024 * 1024, 6D))
//				.features(Features.EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell4mb = this.registerById(registry.item("exp_cell_4mb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_4MB, 4096 * 1024, 8D))
//				.features(Features.EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell16mb = this.registerById(registry.item("exp_cell_16mb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_16MB, 16384 * 1024, 12D))
//				.features(Features.EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell64mb = this.registerById(registry.item("exp_cell_64mb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_64MB, 65536 * 1024, 24D))
//				.features(Features.MEGA_EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell256mb = this.registerById(registry.item("exp_cell_256mb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_256MB, 262144 * 1024, 40D))
//				.features(Features.MEGA_EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell1gb = this.registerById(registry.item("exp_cell_1gb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_1GB, 1048576 * 1024, 52D))
//				.features(Features.MEGA_EXPERIENCE_DENSE_CELLS)
//				.build());
//
//		this.experienceCell2gb = this.registerById(registry.item("exp_cell_2gb", () -> new ExperienceItemCell(Materials.MaterialType.EXP_PART_2GB, Integer.MAX_VALUE, 64D))
//				.features(Features.MEGA_EXPERIENCE_DENSE_CELLS)
//				.build());
		

		this.energyCell1k = this.registerById(registry.item("energy_cell_1k", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_1K, 1024, 1D))
				.features(Features.ENERGY_CELLS)
				.build());

		this.energyCell4k = this.registerById(registry.item("energy_cell_4k", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_4K, 4 * 1024, 2D))
				.features(Features.ENERGY_CELLS)
				.build());

		this.energyCell16k = this.registerById(registry.item("energy_cell_16k", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_16K, 16 * 1024, 3D))
				.features(Features.ENERGY_CELLS)
				.build());

		this.energyCell64k = this.registerById(registry.item("energy_cell_64k", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_64K, 64 * 1024, 4D))
				.features(Features.ENERGY_CELLS)
				.build());

		this.energyCell256k = this.registerById(registry.item("energy_cell_256k", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_256K, 256 * 1024, 4D))
				.features(Features.ENERGY_DENSE_CELLS)
				.build());

		this.energyCell1mb = this.registerById(registry.item("energy_cell_1mb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_1MB, 1024 * 1024, 6D))
				.features(Features.ENERGY_DENSE_CELLS)
				.build());

		this.energyCell4mb = this.registerById(registry.item("energy_cell_4mb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_4MB, 4096 * 1024, 8D))
				.features(Features.ENERGY_DENSE_CELLS)
				.build());

		this.energyCell16mb = this.registerById(registry.item("energy_cell_16mb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_16MB, 16384 * 1024, 12D))
				.features(Features.ENERGY_DENSE_CELLS)
				.build());

		this.energyCell64mb = this.registerById(registry.item("energy_cell_64mb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_64MB, 65536 * 1024, 24D))
				.features(Features.MEGA_ENERGY_DENSE_CELLS)
				.build());

		this.energyCell256mb = this.registerById(registry.item("energy_cell_256mb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_256MB, 262144 * 1024, 40D))
				.features(Features.MEGA_ENERGY_DENSE_CELLS)
				.build());

		this.energyCell1gb = this.registerById(registry.item("energy_cell_1gb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_1GB, 1048576 * 1024, 52D))
				.features(Features.MEGA_ENERGY_DENSE_CELLS)
				.build());

		this.energyCell2gb = this.registerById(registry.item("energy_cell_2gb", () -> new MultiEnergyItemCell(Materials.MaterialType.ENERGY_PART_2GB, Integer.MAX_VALUE, 64D))
				.features(Features.MEGA_ENERGY_DENSE_CELLS)
				.build());



		this.creativeManaCell = this.registerById(registry.item("creative_mana_cell", ManaItemCreativeCell::new)
				.ifModPresent("botania")
				.features(Features.MANA_CELLS, Features.MANA_DENSE_CELLS, Features.MEGA_MANA_DENSE_CELLS)
				.build());
		this.creativeEnergyCell = this.registerById(registry.item("creative_energy_cell", MultiEnergyItemCreativeCell::new)
				.features(Features.ENERGY_CELLS, Features.ENERGY_DENSE_CELLS, Features.MEGA_ENERGY_DENSE_CELLS)
				.build());


		this.improvedPortableCell = this.registerById(registry.item("improved_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 4, 1024 * 1024, 1, 2D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());
		this.advancedPortableCell = this.registerById(registry.item("advanced_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 8, 4096 * 1024, 1, 8D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());
		this.perfectPortableCell = this.registerById(registry.item("perfect_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 24, 16384 * 1024, 1, 16D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());



		this.fluidCell256k = this.registerById(registry.item("fluid_storage_cell_256k", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_256K, 256 * 1024, 4D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell1mb = this.registerById(registry.item("fluid_storage_cell_1mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_1MB, 1024 * 1024, 6D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell4mb = this.registerById(registry.item("fluid_storage_cell_4mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_4MB, 4096 * 1024, 8D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell16mb = this.registerById(registry.item("fluid_storage_cell_16mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_16MB, 16384 * 1024, 12D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell64mb = this.registerById(registry.item("fluid_storage_cell_64mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_64MB, 65536 * 1024, 24D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell256mb = this.registerById(registry.item("fluid_storage_cell_256mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_256MB, 262144 * 1024, 40D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell1gb = this.registerById(registry.item("fluid_storage_cell_1gb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_1GB, 1048576 * 1024, 52D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell2gb = this.registerById(registry.item("fluid_storage_cell_2gb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_2GB, Integer.MAX_VALUE, 64D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.quantumWirelessBooster = this.registerById(registry.item("quantum_wireless_booster", QuantumWirelessBooster::new)
				.features(Features.STUB)
				.hide()
				.build());

		this.manaAsAEStack = this.registerById(registry.item("mana_as_aestack", ManaAsAEStack::new)
				.ifModPresent("botania")
				.features(Features.MANA_BUSES, Features.MANA_TERM, Features.MANA_CELLS, Features.MANA_DENSE_CELLS, Features.MEGA_MANA_DENSE_CELLS)
				.hide()
				.build());

		this.expAsAEStack = this.registerById(registry.item("exp_as_aestack", ExperienceAsAEStack::new)
				.hide()
				.build());

		this.EFEnergyAsAeStack = this.registerById(registry.item("ef_energy_as_aestack", EFEnergyAsAEStack::new)
				.ifModPresent("industrialupgrade")
				.hide()
				.build());

		this.EUEnergyAsAeStack = this.registerById(registry.item("eu_energy_as_aestack", EUEnergyAsAEStack::new)
				.ifModPresent("ic2")
				.hide()
				.build());

		this.FEEnergyAsAeStack = this.registerById(registry.item("fe_energy_as_aestack", FEEnergyAsAEStack::new)
				.hide()
				.build());

		this.QEEnergyAsAeStack = this.registerById(registry.item("qe_energy_as_aestack", QEEnergyAsAEStack::new)
				.ifModPresent("industrialupgrade")
				.hide()
				.build());

		this.SEEnergyAsAeStack = this.registerById(registry.item("se_energy_as_aestack", SEEnergyAsAEStack::new)
				.ifModPresent("industrialupgrade")
				.hide()
				.build());

		registry.addBootstrapComponent((IPostInitComponent) r -> {
			IItems enabledItems = AEApi.instance().definitions().items();
			IItemDefinition itemCellsFeature = enabledItems.cell1k();
			if (itemCellsFeature.isEnabled()) {
				if (Features.DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(itemCellsFeature, new IItemDefinition[]{
							this.storageCell256k,
							this.storageCell1mb,
							this.storageCell4mb,
							this.storageCell16mb
					});
				}

				if (Features.MEGA_DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(itemCellsFeature, new IItemDefinition[]{
							this.storageCell64mb,
							this.storageCell256mb,
							this.storageCell1gb,
							this.storageCell2gb
					});
				}
			}

			IItemDefinition fluidCellsFeature = enabledItems.fluidCell1k();

			if (fluidCellsFeature.isEnabled()) {
				if (Features.DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(fluidCellsFeature, new IItemDefinition[]{
							this.fluidCell256k,
							this.fluidCell1mb,
							this.fluidCell4mb,
							this.fluidCell16mb
					});
				}

				if (Features.MEGA_DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(fluidCellsFeature, new IItemDefinition[]{
							this.fluidCell64mb,
							this.fluidCell256mb,
							this.fluidCell1gb,
							this.fluidCell2gb
					});
				}
			}
        });

		CrazyAESidedHandler.checkAvailableEnergyTypes(this.energyItemsList());
	}

	private static void mirrorCellUpgrades(Function<ItemStack, Boolean> predicate, IItemDefinition[] cells) {
		var supported = new java.util.HashMap<Upgrades, Integer>();
		Arrays.stream(Upgrades.values())
			.forEach(upgrade ->
				upgrade.getSupported().entrySet().stream()
					.filter(x -> predicate.apply(x.getKey()))
					.map(Map.Entry::getValue)
					.findFirst()
					.ifPresent(value -> supported.put(upgrade, value)));

		Arrays.stream(cells).forEach(iItemDefinition ->
			supported.forEach((key, value) ->
				key.registerItem(iItemDefinition, value)));
	}

	private static void mirrorCellUpgrades(IItemDefinition cellDef, IItemDefinition[] cells) {
		mirrorCellUpgrades(cellDef::isSameAs, cells);
	}

	private static void mirrorCellUpgrades(ItemStack itemStack, IItemDefinition[] cells) {
		mirrorCellUpgrades(itemStack::isItemEqual, cells);
	}

	private IItemDefinition registerById(ItemDefinition item) {
		this.byId.put(item.identifier(), item);
		return item;
	}

	@Override
	public Optional<IItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	public IItemDefinition manaCell1k() {
		return this.manaCell1k;
	}

	public IItemDefinition manaCell4k() {
		return this.manaCell4k;
	}

	public IItemDefinition manaCell16k() {
		return this.manaCell16k;
	}

	public IItemDefinition manaCell64k() {
		return this.manaCell64k;
	}

	public IItemDefinition manaCell256k() {
		return this.manaCell256k;
	}

	public IItemDefinition manaCell1MB() {
		return this.manaCell1mb;
	}

	public IItemDefinition manaCell4MB() {
		return this.manaCell4mb;
	}

	public IItemDefinition manaCell16MB() {
		return this.manaCell16mb;
	}

	public IItemDefinition manaCell64MB() {
		return this.manaCell64mb;
	}

	public IItemDefinition manaCell256MB() {
		return this.manaCell256mb;
	}

	public IItemDefinition manaCell1GB() {
		return this.manaCell1gb;
	}

	public IItemDefinition manaCell2GB() {
		return this.manaCell2gb;
	}

	public IItemDefinition energyCell1k() {
		return this.energyCell1k;
	}

	public IItemDefinition energyCell4k() {
		return this.energyCell4k;
	}

	public IItemDefinition energyCell16k() {
		return this.energyCell16k;
	}

	public IItemDefinition energyCell64k() {
		return this.energyCell64k;
	}

	public IItemDefinition energyCell256k() {
		return this.energyCell256k;
	}

	public IItemDefinition energyCell1MB() {
		return this.energyCell1mb;
	}

	public IItemDefinition energyCell4MB() {
		return this.energyCell4mb;
	}

	public IItemDefinition energyCell16MB() {
		return this.energyCell16mb;
	}

	public IItemDefinition energyCell64MB() {
		return this.energyCell64mb;
	}

	public IItemDefinition energyCell256MB() {
		return this.energyCell256mb;
	}

	public IItemDefinition energyCell1GB() {
		return this.energyCell1gb;
	}

	public IItemDefinition energyCell2GB() {
		return this.energyCell2gb;
	}

//	public IItemDefinition expCell1k() {
//		return this.experienceCell1k;
//	}
//
//	public IItemDefinition expCell4k() {
//		return this.experienceCell4k;
//	}
//
//	public IItemDefinition expCell16k() {
//		return this.experienceCell16k;
//	}
//
//	public IItemDefinition expCell64k() {
//		return this.experienceCell64k;
//	}
//
//	public IItemDefinition expCell256k() {
//		return this.experienceCell256k;
//	}
//
//	public IItemDefinition expCell1MB() {
//		return this.experienceCell1mb;
//	}
//
//	public IItemDefinition expCell4MB() {
//		return this.experienceCell4mb;
//	}
//
//	public IItemDefinition expCell16MB() {
//		return this.experienceCell16mb;
//	}
//
//	public IItemDefinition expCell64MB() {
//		return this.experienceCell64mb;
//	}
//
//	public IItemDefinition expCell256MB() {
//		return this.experienceCell256mb;
//	}
//
//	public IItemDefinition expCell1GB() {
//		return this.experienceCell1gb;
//	}
//
//	public IItemDefinition expCell2GB() {
//		return this.experienceCell2gb;
//	}

	public IItemDefinition creativeManaCell() {
		return this.creativeManaCell;
	}

	public IItemDefinition creativeEnergyCell() {
		return this.creativeEnergyCell;
	}

	public IItemDefinition storageCell256K() {
		return this.storageCell256k;
	}

	public IItemDefinition storageCell1MB() {
		return this.storageCell1mb;
	}

	public IItemDefinition storageCell4MB() {
		return this.storageCell4mb;
	}

	public IItemDefinition storageCell16MB() {
		return this.storageCell16mb;
	}

	public IItemDefinition storageCell64MB() {
		return this.storageCell64mb;
	}

	public IItemDefinition storageCell256MB() {
		return this.storageCell256mb;
	}

	public IItemDefinition storageCell1GB() {
		return this.storageCell1gb;
	}

	public IItemDefinition storageCell2GB() {
		return this.storageCell2gb;
	}

	public IItemDefinition improvedPortableCell() {
		return this.improvedPortableCell;
	}

	public IItemDefinition advancedPortableCell() {
		return this.advancedPortableCell;
	}

	public IItemDefinition perfectPortableCell() {
		return this.perfectPortableCell;
	}

	public IItemDefinition fluidStorageCell256K() {
		return this.fluidCell256k;
	}

	public IItemDefinition fluidStorageCell1MB() {
		return this.fluidCell1mb;
	}

	public IItemDefinition fluidStorageCell4MB() {
		return this.fluidCell4mb;
	}

	public IItemDefinition fluidStorageCell16MB() {
		return this.fluidCell16mb;
	}

	public IItemDefinition fluidStorageCell64MB() {
		return this.fluidCell64mb;
	}

	public IItemDefinition fluidStorageCell256MB() {
		return this.fluidCell256mb;
	}

	public IItemDefinition fluidStorageCell1GB() {
		return this.fluidCell1gb;
	}

	public IItemDefinition fluidStorageCell2GB() {
		return this.fluidCell2gb;
	}

	public IItemDefinition manaConnector() {
		return this.manaConnector;
	}

	public IItemDefinition manaAsAEStack() {
		return this.manaAsAEStack;
	}

	public IItemDefinition experienceAsAEStack() {
		return this.expAsAEStack;
	}

	public IItemDefinition EFEnergyAsAeStack() {
		return this.EFEnergyAsAeStack;
	}

	public IItemDefinition FEEnergyAsAeStack() {
		return this.FEEnergyAsAeStack;
	}

	public IItemDefinition EUEnergyAsAeStack() {
		return this.EUEnergyAsAeStack;
	}

	public List<IItemDefinition> energyItemsList() {
		return Arrays.asList(this.FEEnergyAsAeStack, this.EFEnergyAsAeStack, this.SEEnergyAsAeStack, this.QEEnergyAsAeStack, this.EUEnergyAsAeStack);
	}

	public List<IItemDefinition> energyIUItemsList() {
		return Arrays.asList(this.EFEnergyAsAeStack, this.SEEnergyAsAeStack, this.QEEnergyAsAeStack);
	}


	public List<IItemDefinition> getCreativeTabIcons() {
		return Arrays.asList(
				this.storageCell256k, this.storageCell1mb, this.storageCell4mb, this.storageCell16mb,
				this.storageCell64mb, this.storageCell256mb, this.storageCell1gb, this.storageCell2gb,
				this.fluidCell256k, this.fluidCell1mb, this.fluidCell4mb, this.fluidCell16mb,
				this.fluidCell64mb, this.fluidCell256mb, this.fluidCell1gb, this.fluidCell2gb,
				CrazyAE.definitions().blocks().improvedMolecularAssembler(), CrazyAE.definitions().blocks().perfectInterface()
		);
	}

	public IItemDefinition QEEnergyAsAeStack() {
		return this.QEEnergyAsAeStack;
	}

	public IItemDefinition SEEnergyAsAeStack() {
		return this.SEEnergyAsAeStack;
	}

	public IItemDefinition elventradeEncodedPattern() {
		return this.elventradeEncodedPattern;
	}

	public IItemDefinition manapoolEncodedPattern() {
		return this.manapoolEncodedPattern;
	}

	public IItemDefinition petalEncodedPattern() {
		return this.petalEncodedPattern;
	}

	public IItemDefinition runealtarEncodedPattern() {
		return this.runealtarEncodedPattern;
	}

	public IItemDefinition teraplateEncodedPattern() {
		return this.teraplateEncodedPattern;
	}

	public IItemDefinition breweryEncodedPattern() {
		return this.breweryEncodedPattern;
	}


	public IItemDefinition colorizer() {
		return this.colorizer;
	}

	public IItemDefinition advNetTool() {
		return this.advNetTool;
	}

//	public IItemDefinition patternsUSBStick() {
//		return this.patternsUSBStick;
//	}

	public IItemDefinition puredaisyEncodedPattern() {
		return this.puredaisyEncodedPattern;
	}
}
