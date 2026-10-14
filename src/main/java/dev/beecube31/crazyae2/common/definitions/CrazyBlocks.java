package dev.beecube31.crazyae2.common.definitions;

import appeng.block.crafting.CraftingBlockItem;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.misc.InterfaceBlock;
import appeng.block.networking.EnergyCellBlockItem;
import dev.beecube31.crazyae2.common.block.BlockFluixilized;
import dev.beecube31.crazyae2.common.block.crafting.BlockCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.block.crafting.BlockImprovedMAC;
import dev.beecube31.crazyae2.common.block.crafting.BlockQuantumCPU;
import dev.beecube31.crazyae2.common.block.energycells.CrazyEnergyCellBlock;
import dev.beecube31.crazyae2.common.block.grindstone.ImprovedCrankBlock;
import dev.beecube31.crazyae2.common.block.misc.BlockImprovedCondenser;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedCrystalCharger;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedDrive;
import dev.beecube31.crazyae2.common.block.networking.BlockImprovedIOPort;
import dev.beecube31.crazyae2.common.block.solars.SolarPanelBlock;
import dev.beecube31.crazyae2.common.block.trashcans.BlockTrashcan;
import dev.beecube31.crazyae2.common.enums.SolarPanelTier;
import dev.beecube31.crazyae2.common.enums.TrashcanType;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static appeng.block.AEBaseBlock.metalProps;

public final class CrazyBlocks {
    private CrazyBlocks() {}

    private static final Map<CrazyAECraftingUnitType, RegistryObject<? extends CraftingUnitBlock>> CRAFTING_UNITS =
            new EnumMap<>(CrazyAECraftingUnitType.class);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier,
                                                                     BiFunction<T, Item.Properties, Item> itemFactory) {
        RegistryObject<T> block = CrazyRegistries.BLOCKS.register(name, blockSupplier);
        CrazyRegistries.ITEMS.register(name, () -> itemFactory.apply(block.get(), new Item.Properties()));
        return block;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        return registerBlock(name, blockSupplier, BlockItem::new);
    }

    public static final RegistryObject<BlockImprovedMAC> IMPROVED_MAC = registerBlock("improved_mac", () -> new BlockImprovedMAC(metalProps().noOcclusion()));
    public static final RegistryObject<BlockImprovedDrive> IMPROVED_DRIVE = registerBlock("improved_drive", BlockImprovedDrive::new);
    public static final RegistryObject<BlockImprovedCrystalCharger> IMPROVED_CRYSTAL_CHARGER = registerBlock("improved_crystal_charger", BlockImprovedCrystalCharger::new);
    public static final RegistryObject<BlockImprovedCondenser> IMPROVED_CONDENSER = registerBlock("improved_condenser", BlockImprovedCondenser::new);
    public static final RegistryObject<BlockImprovedIOPort> IMPROVED_IO_PORT = registerBlock("improved_io_port", BlockImprovedIOPort::new);
    public static final RegistryObject<InterfaceBlock> PERFECT_INTERFACE = registerBlock("perfect_interface", InterfaceBlock::new);
    public static final RegistryObject<PatternProviderBlock> PATTERNS_INTERFACE = registerBlock("patterns_interface", PatternProviderBlock::new);
    public static final RegistryObject<BlockTrashcan> TRASHCAN_ITEMS = registerBlock("trashcan_items", () -> new BlockTrashcan(TrashcanType.ITEMS));
    public static final RegistryObject<BlockTrashcan> TRASHCAN_FLUIDS = registerBlock("trashcan_fluids", () -> new BlockTrashcan(TrashcanType.FLUIDS));
    public static final RegistryObject<ImprovedCrankBlock> IMPROVED_CRANK = registerBlock("improved_crank", ImprovedCrankBlock::new);
    public static final RegistryObject<BlockFluixilized> FLUIXILIZED_BLOCK = registerBlock("fluixilized_block", BlockFluixilized::new);

    public static final RegistryObject<CrazyEnergyCellBlock> IMPROVED_ENERGY_CELL = registerBlock("improved_energy_cell",
            () -> new CrazyEnergyCellBlock(CrazyAEConfig.COMMON.improvedEnergyCellCapacity::get, 250, 300), EnergyCellBlockItem::new);
    public static final RegistryObject<CrazyEnergyCellBlock> ADVANCED_ENERGY_CELL = registerBlock("advanced_energy_cell",
            () -> new CrazyEnergyCellBlock(CrazyAEConfig.COMMON.advancedEnergyCellCapacity::get, 250, 400), EnergyCellBlockItem::new);
    public static final RegistryObject<CrazyEnergyCellBlock> PERFECT_ENERGY_CELL = registerBlock("perfect_energy_cell",
            () -> new CrazyEnergyCellBlock(CrazyAEConfig.COMMON.perfectEnergyCellCapacity::get, 250, 500), EnergyCellBlockItem::new);
    public static final RegistryObject<CrazyEnergyCellBlock> QUANTUM_ENERGY_CELL = registerBlock("quantum_energy_cell",
            () -> new CrazyEnergyCellBlock(CrazyAEConfig.COMMON.quantumEnergyCellCapacity::get, 250, 600), EnergyCellBlockItem::new);

    public static final RegistryObject<SolarPanelBlock> BASIC_SOLAR_PANEL = registerBlock("basic_solar_panel", () -> new SolarPanelBlock(SolarPanelTier.BASIC));
    public static final RegistryObject<SolarPanelBlock> IMPROVED_SOLAR_PANEL = registerBlock("improved_solar_panel", () -> new SolarPanelBlock(SolarPanelTier.IMPROVED));
    public static final RegistryObject<SolarPanelBlock> ADVANCED_SOLAR_PANEL = registerBlock("advanced_solar_panel", () -> new SolarPanelBlock(SolarPanelTier.ADVANCED));
    public static final RegistryObject<SolarPanelBlock> PERFECT_SOLAR_PANEL = registerBlock("perfect_solar_panel", () -> new SolarPanelBlock(SolarPanelTier.PERFECT));
    public static final RegistryObject<SolarPanelBlock> QUANTUM_SOLAR_PANEL = registerBlock("quantum_solar_panel", () -> new SolarPanelBlock(SolarPanelTier.QUANTUM));

    static {
        for (var type : CrazyAECraftingUnitType.values()) {
            if (type == CrazyAECraftingUnitType.COMBINER) {
                continue;
            }
            CRAFTING_UNITS.put(type, registerBlock(type.getName(), () -> new CraftingUnitBlock(type), (block, props) -> {
                Supplier<ItemLike> disassembly = disassemblyItem(type);
                return disassembly == null ? new BlockItem(block, props) : new CraftingBlockItem(block, props, disassembly);
            }));
        }
    }

    public static final RegistryObject<BlockCraftingUnitsCombiner> CRAFTING_UNITS_COMBINER =
            registerBlock("crafting_units_combiner", BlockCraftingUnitsCombiner::new);

    static {
        CRAFTING_UNITS.put(CrazyAECraftingUnitType.COMBINER, CRAFTING_UNITS_COMBINER);
    }

    public static final RegistryObject<BlockQuantumCPU> QUANTUM_CPU =
            registerBlock("quantum_cpu", BlockQuantumCPU::new);

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

    public static Map<CrazyAECraftingUnitType, RegistryObject<? extends CraftingUnitBlock>> craftingUnits() {
        return Collections.unmodifiableMap(CRAFTING_UNITS);
    }

    public static void init() {}
}
