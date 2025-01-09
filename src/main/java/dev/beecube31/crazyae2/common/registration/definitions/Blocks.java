package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.ITileDefinition;
import appeng.block.crafting.ItemCraftingStorage;
import appeng.bootstrap.definitions.TileEntityDefinition;
import appeng.core.features.AEFeature;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.client.rendering.*;
import dev.beecube31.crazyae2.common.blocks.BlockDenseCraftingUnit;
import dev.beecube31.crazyae2.common.blocks.botania.*;
import dev.beecube31.crazyae2.common.blocks.crafting.BlockImprovedMAC;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockAdvancedEnergyCell;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockImprovedEnergyCell;
import dev.beecube31.crazyae2.common.blocks.energycells.BlockPerfectEnergyCell;
import dev.beecube31.crazyae2.common.blocks.grindstone.BlockImprovedCrank;
import dev.beecube31.crazyae2.common.blocks.materials.BlockFluxilized;
import dev.beecube31.crazyae2.common.blocks.misc.BlockImprovedCondenser;
import dev.beecube31.crazyae2.common.blocks.networking.*;
import dev.beecube31.crazyae2.common.blocks.solars.BlockPanelAdvanced;
import dev.beecube31.crazyae2.common.blocks.solars.BlockPanelBasic;
import dev.beecube31.crazyae2.common.blocks.solars.BlockPanelImproved;
import dev.beecube31.crazyae2.common.blocks.solars.BlockPanelPerfect;
import dev.beecube31.crazyae2.common.blocks.storage.BlockDriveImproved;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.features.subfeatures.DenseCellFeatures;
import dev.beecube31.crazyae2.common.features.subfeatures.MegaDenseCellFeatures;
import dev.beecube31.crazyae2.common.items.ItemEnergyCells;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEDenseCraftingCubeRendering;
import dev.beecube31.crazyae2.common.tile.TileDenseCraftingUnit;
import dev.beecube31.crazyae2.common.tile.botania.*;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.energycells.TileAdvancedEnergyCell;
import dev.beecube31.crazyae2.common.tile.energycells.TileImprovedEnergyCell;
import dev.beecube31.crazyae2.common.tile.energycells.TilePerfectEnergyCell;
import dev.beecube31.crazyae2.common.tile.grindstone.TileImprovedCrank;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.tile.networking.*;
import dev.beecube31.crazyae2.common.tile.solars.TileEnergyPanelAdvanced;
import dev.beecube31.crazyae2.common.tile.solars.TileEnergyPanelBasic;
import dev.beecube31.crazyae2.common.tile.solars.TileEnergyPanelImproved;
import dev.beecube31.crazyae2.common.tile.solars.TileEnergyPanelPerfect;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class Blocks {
	private final ITileDefinition craftingStorage256k;
	private final ITileDefinition craftingStorage1mb;
	private final ITileDefinition craftingStorage4mb;
	private final ITileDefinition craftingStorage16mb;
	private final ITileDefinition craftingStorage64mb;
	private final ITileDefinition craftingStorage256mb;
	private final ITileDefinition craftingStorage1gb;
	private final ITileDefinition craftingStorage2gb;

	private final ITileDefinition coprocessor4x;
	private final ITileDefinition coprocessor16x;
	private final ITileDefinition coprocessor64x;
	private final ITileDefinition coprocessor256x;
	private final ITileDefinition coprocessor1024x;
	private final ITileDefinition coprocessor4096x;
	private final ITileDefinition coprocessor16384x;
	private final ITileDefinition coprocessor65536x;


	private final ITileDefinition energyCellImproved;
	private final ITileDefinition energyCellAdvanced;
	private final ITileDefinition energyCellPerfect;

	private final ITileDefinition fastMAC;
	private final ITileDefinition impDrive;
	private final ITileDefinition iOPortImp;
	private final ITileDefinition crankImp;
	private final ITileDefinition craftingUnitsCombiner;
	private final ITileDefinition bigCrystalCharger;
	private final ITileDefinition energyPanelPerfect;

	private final ITileDefinition energyPanelBasic;
	private final ITileDefinition energyPanelImproved;
	private final ITileDefinition energyPanelAdvanced;

	private final ITileDefinition improvedCondenser;

	private final IBlockDefinition fluixilizedBlock;

	private final ITileDefinition patternsInterface;
	private final ITileDefinition quantumChannelMultiplier;
	private final ITileDefinition quantumChannelMultiplierCreative;


	private final ITileDefinition elventradeMechanical;
	private final ITileDefinition manapoolMechanical;
	private final ITileDefinition petalMechanical;
	private final ITileDefinition runealtarMechanical;
	private final ITileDefinition puredaisyMechanical;

	public Blocks(Registry registry) {
		this.craftingStorage256k = registry.block("crafting_storage_256k", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_256K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_256k", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_256K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(DenseCellFeatures.DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage1mb = registry.block("crafting_storage_1mb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_1024K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_1mb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_1024K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(DenseCellFeatures.DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage4mb = registry.block("crafting_storage_4mb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_4096K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_4mb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_4096K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(DenseCellFeatures.DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage16mb = registry.block("crafting_storage_16mb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_16384K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_16mb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_16384K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(DenseCellFeatures.DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage64mb = registry.block("crafting_storage_64mb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_65536K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_64mb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_65536K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(MegaDenseCellFeatures.MEGA_DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage256mb = registry.block("crafting_storage_256mb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_262144K))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_256mb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_262144K))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(MegaDenseCellFeatures.MEGA_DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage1gb = registry.block("crafting_storage_1gb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_1GB))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_1gb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_1GB))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(MegaDenseCellFeatures.MEGA_DENSE_CPU_STORAGE_UNITS)
				.build();

		this.craftingStorage2gb = registry.block("crafting_storage_2gb", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_2GB))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_storage_2gb", BlockDenseCraftingUnit.DenseCraftingUnitType.STORAGE_2GB))
				.useCustomItemModel()
				.item(ItemCraftingStorage::new)
				.features(MegaDenseCellFeatures.MEGA_DENSE_CPU_STORAGE_UNITS)
				.build();

		this.coprocessor4x = registry.block("crafting_accelerator_4x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_4X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_4x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_4X))
				.useCustomItemModel()
				.features(Features.DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor16x = registry.block("crafting_accelerator_16x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_16X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_16x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_16X))
				.useCustomItemModel()
				.features(Features.DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor64x = registry.block("crafting_accelerator_64x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_64X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_64x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_64X))
				.useCustomItemModel()
				.features(Features.DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor256x = registry.block("crafting_accelerator_256x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_256X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_256x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_256X))
				.useCustomItemModel()
				.features(Features.DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor1024x = registry.block("crafting_accelerator_1024x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_1024X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_1024x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_1024X))
				.useCustomItemModel()
				.features(Features.MEGA_DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor4096x = registry.block("crafting_accelerator_4096x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_4096X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_4096x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_4096X))
				.useCustomItemModel()
				.features(Features.MEGA_DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor16384x = registry.block("crafting_accelerator_16384x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_16384X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_16384x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_16384X))
				.useCustomItemModel()
				.features(Features.MEGA_DENSE_CPU_COPROCESSORS)
				.build();

		this.coprocessor65536x = registry.block("crafting_accelerator_65536x", () -> new BlockDenseCraftingUnit(BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_65536X))
				.tileEntity(new TileEntityDefinition(TileDenseCraftingUnit.class, "crafting_storage"))
				.rendering(new CrazyAEDenseCraftingCubeRendering("crafting_accelerator_65536x", BlockDenseCraftingUnit.DenseCraftingUnitType.COPROCESSOR_65536X))
				.useCustomItemModel()
				.features(Features.MEGA_DENSE_CPU_COPROCESSORS)
				.build();



		this.energyCellImproved = registry.block("improved_energy_cell", BlockImprovedEnergyCell::new)
				.features(Features.IMPROVED_ENERGY_CELLS)
				.item(ItemEnergyCells::new)
				.tileEntity(new TileEntityDefinition(TileImprovedEnergyCell.class))
				.rendering(new BlockBigEnergyCellRendering(new ResourceLocation(Tags.MODID, "improved_energy_cell")))
				.build();

		this.energyCellAdvanced = registry.block("advanced_energy_cell", BlockAdvancedEnergyCell::new)
				.features(Features.IMPROVED_ENERGY_CELLS)
				.item(ItemEnergyCells::new)
				.tileEntity(new TileEntityDefinition(TileAdvancedEnergyCell.class))
				.rendering(new BlockBigEnergyCellRendering(new ResourceLocation(Tags.MODID, "advanced_energy_cell")))
				.build();

		this.energyCellPerfect = registry.block("perfect_energy_cell", BlockPerfectEnergyCell::new)
				.features(Features.IMPROVED_ENERGY_CELLS)
				.item(ItemEnergyCells::new)
				.tileEntity(new TileEntityDefinition(TilePerfectEnergyCell.class))
				.rendering(new BlockBigEnergyCellRendering(new ResourceLocation(Tags.MODID, "perfect_energy_cell")))
				.build();



		this.fastMAC = registry.block("improved_molecular_assembler", BlockImprovedMAC::new)
				.tileEntity(new TileEntityDefinition(TileImprovedMAC.class))
				.build();

		this.impDrive = registry.block("improved_drive", BlockDriveImproved::new)
				.features(Features.IMPROVED_DRIVE)
				.tileEntity(new TileEntityDefinition(TileImprovedDrive.class))
				.rendering(new ImprovedDriveRendering())
				.useCustomItemModel()
				.build();

		this.iOPortImp = registry.block("improved_io_port", BlockIOPortImp::new)
				.features(Features.IMPROVED_IO_PORT)
				.tileEntity(new TileEntityDefinition(TileImprovedIOPort.class))
				.build();

		this.crankImp = registry.block("improved_crank", BlockImprovedCrank::new)
				.aeFeatures(AEFeature.GRIND_STONE)
				.tileEntity(new TileEntityDefinition(TileImprovedCrank.class))
				.rendering(new ImprovedCrankRendering())
				.useCustomItemModel()
				.build();

		this.craftingUnitsCombiner = registry.block("crafting_units_combiner", BlockCraftingUnitsCombiner::new)
				.tileEntity(new TileEntityDefinition(TileCraftingUnitsCombiner.class))
				.build();

		this.bigCrystalCharger = registry.block("big_crystal_charger", BlockBigCrystalCharger::new)
				.tileEntity(new TileEntityDefinition(TileBigCrystalCharger.class))
				.build();


		this.energyPanelBasic = registry.block("basic_solar_panel", BlockPanelBasic::new)
				.features(Features.SOLAR_PANELS)
				.tileEntity(new TileEntityDefinition(TileEnergyPanelBasic.class))
				.build();
		this.energyPanelImproved = registry.block("improved_solar_panel", BlockPanelImproved::new)
				.features(Features.SOLAR_PANELS)
				.tileEntity(new TileEntityDefinition(TileEnergyPanelImproved.class))
				.build();
		this.energyPanelAdvanced = registry.block("advanced_solar_panel", BlockPanelAdvanced::new)
				.features(Features.SOLAR_PANELS)
				.tileEntity(new TileEntityDefinition(TileEnergyPanelAdvanced.class))
				.build();
		this.energyPanelPerfect = registry.block("perfect_solar_panel", BlockPanelPerfect::new)
				.features(Features.SOLAR_PANELS)
				.tileEntity(new TileEntityDefinition(TileEnergyPanelPerfect.class))
				.build();

		this.patternsInterface = registry.block("patterns_interface", BlockInterfacePatterns::new)
				.features(Features.PATTERNS_INTERFACE)
				.tileEntity(new TileEntityDefinition(TilePatternsInterface.class))
				.build();

		this.quantumChannelMultiplier = registry.block("quantum_channels_multiplier", BlockQuantumChannelsMultiplier::new)
				.features(Features.QUANTUM_CHANNELS_MULTIPLIER)
				.tileEntity(new TileEntityDefinition(TileQuantumChannelsBooster.class))
				.rendering(new QCMRendering())
				.build();

		this.quantumChannelMultiplierCreative = registry.block("quantum_channels_multiplier_creative", BlockQuantumChannelsMultiplierCreative::new)
				.features(Features.QUANTUM_CHANNELS_MULTIPLIER)
				.tileEntity(new TileEntityDefinition(TileCreativeQuantumChannelsBooster.class))
				.rendering(new QCMCreativeRendering())
				.build();


		this.elventradeMechanical = registry.block("mechanical_elventrade", BlockMechanicalElventrade::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.tileEntity(new TileEntityDefinition(TileMechanicalElventrade.class))
				.build();
		this.manapoolMechanical = registry.block("mechanical_manapool", BlockMechanicalManapool::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.tileEntity(new TileEntityDefinition(TileMechanicalManapool.class))
				.build();
		this.runealtarMechanical = registry.block("mechanical_runealtar", BlockMechanicalRunealtar::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.tileEntity(new TileEntityDefinition(TileMechanicalRunealtar.class))
				.build();
		this.petalMechanical = registry.block("mechanical_petal", BlockMechanicalPetal::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.tileEntity(new TileEntityDefinition(TileMechanicalPetal.class))
				.build();
		this.puredaisyMechanical = registry.block("mechanical_puredaisy", BlockMechanicalPuredaisy::new)
				.features(Features.BOTANIA_MECHANICAL_BLOCKS)
				.tileEntity(new TileEntityDefinition(TileMechanicalPuredaisy.class))
				.build();


		this.improvedCondenser = registry.block("improved_condenser", BlockImprovedCondenser::new)
				.features(Features.STUB)
				.tileEntity(new TileEntityDefinition(TileImprovedCondenser.class))
				.build();

		this.fluixilizedBlock = registry.block("fluixilized_block", BlockFluxilized::new)
				.aeFeatures(AEFeature.FLUIX)
				.build();
	}

	public ITileDefinition improvedMolecularAssembler() {
		return this.fastMAC;
	}

	public ITileDefinition mechanicalElventrade() {
		return this.elventradeMechanical;
	}

	public ITileDefinition mechanicalManapool() {
		return this.manapoolMechanical;
	}

	public ITileDefinition mechanicalRunealtar() {
		return this.runealtarMechanical;
	}

	public ITileDefinition mechanicalPuredaisy() {
		return this.puredaisyMechanical;
	}

	public ITileDefinition mechanicalPetal() {
		return this.petalMechanical;
	}

	public ITileDefinition improvedDrive() {
		return this.impDrive;
	}

	public ITileDefinition craftingStorage256k() {
		return this.craftingStorage256k;
	}

	public ITileDefinition craftingStorage1mb() {
		return this.craftingStorage1mb;
	}

	public ITileDefinition craftingStorage4mb() {
		return this.craftingStorage4mb;
	}

	public ITileDefinition craftingStorage16mb() {
		return this.craftingStorage16mb;
	}

	public ITileDefinition craftingStorage64mb() {
		return this.craftingStorage64mb;
	}

	public ITileDefinition craftingStorage256mb() {
		return this.craftingStorage256mb;
	}

	public ITileDefinition craftingStorage1gb() {
		return this.craftingStorage1gb;
	}

	public ITileDefinition craftingStorage2gb() {
		return this.craftingStorage2gb;
	}

	public ITileDefinition coprocessor4x() {
		return this.coprocessor4x;
	}

	public ITileDefinition coprocessor16x() {
		return this.coprocessor16x;
	}

	public ITileDefinition coprocessor64x() {
		return this.coprocessor64x;
	}

	public ITileDefinition coprocessor256x() {
		return this.coprocessor256x;
	}

	public ITileDefinition coprocessor1024x() {
		return this.coprocessor1024x;
	}

	public ITileDefinition coprocessor4096x() {
		return this.coprocessor4096x;
	}

	public ITileDefinition coprocessor16384x() {
		return this.coprocessor16384x;
	}

	public ITileDefinition coprocessor65536x() {
		return this.coprocessor65536x;
	}

	public ITileDefinition improvedEnergyCell() {
		return this.energyCellImproved;
	}
	public ITileDefinition advancedEnergyCell() {
		return this.energyCellAdvanced;
	}
	public ITileDefinition perfectEnergyCell() {
		return this.energyCellPerfect;
	}

	public ITileDefinition ioPortImp() {
		return this.iOPortImp;
	}

	public ITileDefinition crankImp() {
		return this.crankImp;
	}

	public ITileDefinition bigCrystalCharger() {
		return this.bigCrystalCharger;
	}

	public ITileDefinition energyPanelPerfect() {
		return this.energyPanelPerfect;
	}

	public ITileDefinition energyPanelBasic() {
		return this.energyPanelBasic;
	}

	public ITileDefinition energyPanelImproved() {
		return this.energyPanelImproved;
	}

	public ITileDefinition energyPanelAdvanced() {
		return this.energyPanelAdvanced;
	}

	public ITileDefinition improvedCondenser() {
		return this.improvedCondenser;
	}

	public ITileDefinition quantumChannelMultiplier() {
		return this.quantumChannelMultiplier;
	}

	public ITileDefinition quantumChannelMultiplierCreative() {
		return this.quantumChannelMultiplierCreative;
	}

	public ITileDefinition craftingUnitsCombiner() {
		return this.craftingUnitsCombiner;
	}

	public ITileDefinition patternsInterface() {
		return this.patternsInterface;
	}

	public IBlockDefinition fluixilizedBlock() {
		return this.fluixilizedBlock;
	}

}
