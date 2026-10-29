package dev.beecube31.crazyae2.common.definitions;

import dev.beecube31.crazyae2.common.block.crafting.BlockQuantumCPU;

import dev.beecube31.crazyae2.common.item.CrazyCraftingBlockItem;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.function.Function;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.misc.InterfaceBlock;
import appeng.block.networking.EnergyCellBlockItem;
import dev.beecube31.crazyae2.common.block.BlockFluixilized;
import dev.beecube31.crazyae2.common.block.crafting.BlockCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.block.crafting.BlockImprovedMAC;
import dev.beecube31.crazyae2.common.block.energycells.CrazyEnergyCellBlock;
import dev.beecube31.crazyae2.common.block.misc.BlockImprovedCondenser;
import dev.beecube31.crazyae2.common.block.grindstone.ImprovedCrankBlock;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedCrystalCharger;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedDrive;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedIOPort;
import dev.beecube31.crazyae2.common.block.solars.SolarPanelBlock;
import dev.beecube31.crazyae2.common.block.solars.SolarPanelBlockItem;
import dev.beecube31.crazyae2.common.block.trashcans.BlockTrashcan;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.common.enums.SolarPanelTier;
import dev.beecube31.crazyae2.common.enums.TrashcanType;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static appeng.block.AEBaseBlock.metalProps;

public final class CrazyBlocks {
    private CrazyBlocks() {}

    private static final Map<CrazyAECraftingUnitType, DeferredBlock<? extends CraftingUnitBlock>> CRAFTING_UNITS =
            new EnumMap<>(CrazyAECraftingUnitType.class);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
                                                                     BiFunction<T, Item.Properties, Item> itemFactory) {
        DeferredBlock<T> block = CrazyRegistries.BLOCKS.registerBlock(name, blockFactory);
        CrazyRegistries.ITEMS.register(name, () -> itemFactory.apply(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, CrazyAE.makeId(name)))
                .useBlockDescriptionPrefix()));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory) {
        return registerBlock(name, blockFactory, BlockItem::new);
    }

    public static final DeferredBlock<BlockImprovedMAC> IMPROVED_MAC = registerBlock("improved_mac", p -> new BlockImprovedMAC(metalProps(p).noOcclusion()));
    public static final DeferredBlock<BlockImprovedDrive> IMPROVED_DRIVE = registerBlock("improved_drive", BlockImprovedDrive::new);
    public static final DeferredBlock<BlockImprovedCrystalCharger> IMPROVED_CRYSTAL_CHARGER = registerBlock("improved_crystal_charger", BlockImprovedCrystalCharger::new);
    public static final DeferredBlock<BlockImprovedCondenser> IMPROVED_CONDENSER = registerBlock("improved_condenser", BlockImprovedCondenser::new);
    public static final DeferredBlock<BlockImprovedIOPort> IMPROVED_IO_PORT = registerBlock("improved_io_port", BlockImprovedIOPort::new);
    public static final DeferredBlock<InterfaceBlock> PERFECT_INTERFACE = registerBlock("perfect_interface", InterfaceBlock::new);
    public static final DeferredBlock<PatternProviderBlock> PATTERNS_INTERFACE = registerBlock("patterns_interface", PatternProviderBlock::new);
    public static final DeferredBlock<BlockTrashcan> TRASHCAN_ITEMS = registerBlock("trashcan_items", p -> new BlockTrashcan(p, TrashcanType.ITEMS));
    public static final DeferredBlock<BlockTrashcan> TRASHCAN_FLUIDS = registerBlock("trashcan_fluids", p -> new BlockTrashcan(p, TrashcanType.FLUIDS));
    public static final DeferredBlock<ImprovedCrankBlock> IMPROVED_CRANK = registerBlock("improved_crank", ImprovedCrankBlock::new);
    public static final DeferredBlock<BlockFluixilized> FLUIXILIZED_BLOCK = registerBlock("fluixilized_block", BlockFluixilized::new);

    public static final DeferredBlock<CrazyEnergyCellBlock> IMPROVED_ENERGY_CELL = registerBlock("improved_energy_cell",
            p -> new CrazyEnergyCellBlock(p, CrazyAEConfig.COMMON.improvedEnergyCellCapacity::get, 250, 300), EnergyCellBlockItem::new);
    public static final DeferredBlock<CrazyEnergyCellBlock> ADVANCED_ENERGY_CELL = registerBlock("advanced_energy_cell",
            p -> new CrazyEnergyCellBlock(p, CrazyAEConfig.COMMON.advancedEnergyCellCapacity::get, 250, 400), EnergyCellBlockItem::new);
    public static final DeferredBlock<CrazyEnergyCellBlock> PERFECT_ENERGY_CELL = registerBlock("perfect_energy_cell",
            p -> new CrazyEnergyCellBlock(p, CrazyAEConfig.COMMON.perfectEnergyCellCapacity::get, 250, 500), EnergyCellBlockItem::new);
    public static final DeferredBlock<CrazyEnergyCellBlock> QUANTUM_ENERGY_CELL = registerBlock("quantum_energy_cell",
            p -> new CrazyEnergyCellBlock(p, CrazyAEConfig.COMMON.quantumEnergyCellCapacity::get, 250, 600), EnergyCellBlockItem::new);

    public static final DeferredBlock<SolarPanelBlock> BASIC_SOLAR_PANEL = registerBlock("basic_solar_panel", p -> new SolarPanelBlock(p, SolarPanelTier.BASIC), SolarPanelBlockItem::new);
    public static final DeferredBlock<SolarPanelBlock> IMPROVED_SOLAR_PANEL = registerBlock("improved_solar_panel", p -> new SolarPanelBlock(p, SolarPanelTier.IMPROVED), SolarPanelBlockItem::new);
    public static final DeferredBlock<SolarPanelBlock> ADVANCED_SOLAR_PANEL = registerBlock("advanced_solar_panel", p -> new SolarPanelBlock(p, SolarPanelTier.ADVANCED), SolarPanelBlockItem::new);
    public static final DeferredBlock<SolarPanelBlock> PERFECT_SOLAR_PANEL = registerBlock("perfect_solar_panel", p -> new SolarPanelBlock(p, SolarPanelTier.PERFECT), SolarPanelBlockItem::new);
    public static final DeferredBlock<SolarPanelBlock> QUANTUM_SOLAR_PANEL = registerBlock("quantum_solar_panel", p -> new SolarPanelBlock(p, SolarPanelTier.QUANTUM), SolarPanelBlockItem::new);

    static {
        for (var type : CrazyAECraftingUnitType.values()) {
            if (type == CrazyAECraftingUnitType.COMBINER) {
                continue;
            }
            CRAFTING_UNITS.put(type, registerBlock(type.getName(), p -> new CraftingUnitBlock(p, type), (block, props) -> {
                Supplier<ItemLike> disassembly = disassemblyItem(type);
                return disassembly == null ? new BlockItem(block, props) : new CrazyCraftingBlockItem(block, props, disassembly);
            }));
        }
    }

    public static final DeferredBlock<BlockCraftingUnitsCombiner> CRAFTING_UNITS_COMBINER =
            registerBlock("crafting_units_combiner", BlockCraftingUnitsCombiner::new);

    static {
        CRAFTING_UNITS.put(CrazyAECraftingUnitType.COMBINER, CRAFTING_UNITS_COMBINER);
    }

    private static Supplier<ItemLike> disassemblyItem(CrazyAECraftingUnitType type) {
        if (type.isCreative()) {
            return null;
        }
        if (type.getAcceleratorThreads() > 0) {
            return CrazyItems.QUANTUM_PROCESSOR::get;
        }
        return switch (type) {
            case STORAGE_1MB -> CrazyItems.CELL_COMPONENT_1MB::get;
            case STORAGE_4MB -> CrazyItems.CELL_COMPONENT_4MB::get;
            case STORAGE_16MB -> CrazyItems.CELL_COMPONENT_16MB::get;
            case STORAGE_64MB -> CrazyItems.CELL_COMPONENT_64MB::get;
            case STORAGE_256MB -> CrazyItems.CELL_COMPONENT_256MB::get;
            case STORAGE_1GB -> CrazyItems.CELL_COMPONENT_1GB::get;
            case STORAGE_2GB -> CrazyItems.CELL_COMPONENT_2GB::get;
            case STORAGE_8GB -> CrazyItems.CELL_COMPONENT_8GB::get;
            case STORAGE_32GB -> CrazyItems.CELL_COMPONENT_32GB::get;
            case STORAGE_128GB -> CrazyItems.CELL_COMPONENT_128GB::get;
            default -> null;
        };
    }

    public static CraftingUnitBlock getCraftingBlock(CrazyAECraftingUnitType type) {
        return CRAFTING_UNITS.get(type).get();
    }

    public static Map<CrazyAECraftingUnitType, DeferredBlock<? extends CraftingUnitBlock>> craftingUnits() {
        return Collections.unmodifiableMap(CRAFTING_UNITS);
    }

    public static final DeferredBlock<BlockQuantumCPU> QUANTUM_CPU =
            registerBlock("quantum_cpu", BlockQuantumCPU::new);

    public static void init() {}
}
