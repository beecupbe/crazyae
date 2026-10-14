package dev.beecube31.crazyae2.core.registries.server;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.helpers.IPriorityHost;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import dev.beecube31.crazyae2.common.containers.implementations.*;
import dev.beecube31.crazyae2.common.parts.PartImprovedExportBus;
import dev.beecube31.crazyae2.common.parts.PartImprovedImportBus;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcan;
import dev.beecube31.crazyae2.core.builder.ContainerTypeBuilder;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class CrazyContainers {
    public static final RegistryObject<MenuType<ContainerImprovedMAC>> IMPROVED_MAC = CrazyRegistries.MENU_TYPES.register(
            "improved_mac", () -> ContainerTypeBuilder.create(ContainerImprovedMAC::new, TileImprovedMAC.class).build("improved_mac"));

    public static final RegistryObject<MenuType<ContainerDriveImproved>> IMPROVED_DRIVE = CrazyRegistries.MENU_TYPES.register(
            "improved_drive", () -> ContainerTypeBuilder.create(ContainerDriveImproved::new, TileImprovedDrive.class).build("improved_drive"));

    public static final RegistryObject<MenuType<ContainerImprovedIOPort>> IMPROVED_IO_PORT = CrazyRegistries.MENU_TYPES.register(
            "improved_io_port", () -> ContainerTypeBuilder.create(ContainerImprovedIOPort::new, TileImprovedIOPort.class).build("improved_io_port"));

    public static final RegistryObject<MenuType<ContainerBigCrystalCharger>> IMPROVED_CRYSTAL_CHARGER = CrazyRegistries.MENU_TYPES.register(
            "improved_crystal_charger", () -> ContainerTypeBuilder.create(ContainerBigCrystalCharger::new, TileBigCrystalCharger.class).build("improved_crystal_charger"));

    public static final RegistryObject<MenuType<ContainerColorizer>> COLORIZER = CrazyRegistries.MENU_TYPES.register(
            "colorizer", () -> ContainerTypeBuilder.create(ContainerColorizer::new, ItemMenuHost.class).build("colorizer"));

    public static final RegistryObject<MenuType<ContainerPerfectInterface>> PERFECT_INTERFACE = CrazyRegistries.MENU_TYPES.register(
            "perfect_interface", () -> ContainerTypeBuilder.create(ContainerPerfectInterface::new, InterfaceLogicHost.class).build("perfect_interface"));

    public static final RegistryObject<MenuType<ContainerPatternsInterface>> PATTERNS_INTERFACE = CrazyRegistries.MENU_TYPES.register(
            "patterns_interface", () -> ContainerTypeBuilder.create(ContainerPatternsInterface::new, PatternProviderLogicHost.class).build("patterns_interface"));

    public static final RegistryObject<MenuType<ContainerTrashcan>> TRASHCAN = CrazyRegistries.MENU_TYPES.register(
            "trashcan", () -> ContainerTypeBuilder.create(ContainerTrashcan::new, TileTrashcan.class).build("trashcan"));

    public static final RegistryObject<MenuType<ContainerCraftingUnitsCombiner>> CRAFTING_UNITS_COMBINER = CrazyRegistries.MENU_TYPES.register(
            "crafting_units_combiner", () -> ContainerTypeBuilder.create(ContainerCraftingUnitsCombiner::new, TileCraftingUnitsCombiner.class).build("crafting_units_combiner"));

    public static final RegistryObject<MenuType<ContainerQuantumCPU>> QUANTUM_CPU = CrazyRegistries.MENU_TYPES.register(
            "quantum_cpu", () -> ContainerTypeBuilder.create(ContainerQuantumCPU::new, TileQuantumCPU.class).build("quantum_cpu"));

    public static final RegistryObject<MenuType<ContainerQuantumCPUCraftingBlocks>> QUANTUM_CPU_CRAFTING_BLOCKS = CrazyRegistries.MENU_TYPES.register(
            "quantum_cpu_crafting_blocks", () -> ContainerTypeBuilder.create(ContainerQuantumCPUCraftingBlocks::new, TileQuantumCPU.class).build("quantum_cpu_crafting_blocks"));

    public static final RegistryObject<MenuType<ContainerImprovedCondenser>> IMPROVED_CONDENSER = CrazyRegistries.MENU_TYPES.register(
            "improved_condenser", () -> ContainerTypeBuilder.create(ContainerImprovedCondenser::new, TileImprovedCondenser.class).build("improved_condenser"));

    public static final RegistryObject<MenuType<ContainerIOBus>> IMPROVED_EXPORT_BUS = CrazyRegistries.MENU_TYPES.register(
            "improved_export_bus", () -> ContainerTypeBuilder.create(ContainerIOBus::new, PartImprovedExportBus.class).build("improved_export_bus"));

    public static final RegistryObject<MenuType<ContainerIOBus>> IMPROVED_IMPORT_BUS = CrazyRegistries.MENU_TYPES.register(
            "improved_import_bus", () -> ContainerTypeBuilder.create(ContainerIOBus::new, PartImprovedImportBus.class).build("improved_import_bus"));


    public static final RegistryObject<MenuType<ContainerCrazyPriority>> PRIORITY = CrazyRegistries.MENU_TYPES.register(
            "priority", () -> ContainerTypeBuilder.create(ContainerCrazyPriority::new, IPriorityHost.class)
                    .withInitialData((host, buffer) ->
                            buffer.writeVarInt(host.getPriority()), (host, menu, buffer) ->
                            menu.forcePriority(buffer.readVarInt())).build("priority"));

    public static void init() {}
}
