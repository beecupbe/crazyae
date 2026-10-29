package dev.beecube31.crazyae2.core.registries.client;

import dev.beecube31.crazyae2.client.gui.implementations.GuiCraftingUnitsCombiner;
import dev.beecube31.crazyae2.client.gui.implementations.GuiPatternsInterface;
import dev.beecube31.crazyae2.client.gui.implementations.GuiPerfectInterface;
import dev.beecube31.crazyae2.client.gui.implementations.GuiTrashcan;
import dev.beecube31.crazyae2.client.gui.implementations.GuiImprovedCondenser;

import appeng.client.gui.style.StyleManager;
import dev.beecube31.crazyae2.client.gui.implementations.*;
import dev.beecube31.crazyae2.common.containers.implementations.*;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class RegisterFactoryClient {

    public static final RegisterFactoryClient INSTANCE = new RegisterFactoryClient();

    public static void onRegisterScreens(final RegisterMenuScreensEvent event) {
        {
            try {
                event.<ContainerImprovedMAC, GuiImprovedMAC>register(
                        CrazyContainers.IMPROVED_MAC.get(),
                        (menu, playerInv, title) -> {
                            var style = StyleManager.loadStyleDoc("/screens/improved_mac.json");
                            try {
                                return new GuiImprovedMAC(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );

                event.<ContainerDriveImproved, GuiImprovedDrive>register(
                        CrazyContainers.IMPROVED_DRIVE.get(),
                        (menu, playerInv, title) -> {
                            var style = StyleManager.loadStyleDoc("/screens/improved_drive.json");
                            try {
                                return new GuiImprovedDrive(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );

                event.<ContainerImprovedIOPort, GuiImprovedIOPort>register(
                        CrazyContainers.IMPROVED_IO_PORT.get(),
                        (menu, playerInv, title) -> {
                            var style = StyleManager.loadStyleDoc("/screens/improved_io_port.json");
                            try {
                                return new GuiImprovedIOPort(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );

                event.<ContainerBigCrystalCharger, GuiImprovedCrystalCharger>register(
                        CrazyContainers.IMPROVED_CRYSTAL_CHARGER.get(),
                        (menu, playerInv, title) -> {
                            var style = StyleManager.loadStyleDoc("/screens/improved_crystal_charger.json");
                            try {
                                return new GuiImprovedCrystalCharger(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerColorizer, GuiColorizer>register(
                        CrazyContainers.COLORIZER.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/colorizer.json");
                                return new GuiColorizer(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerPerfectInterface, GuiPerfectInterface>register(
                        CrazyContainers.PERFECT_INTERFACE.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_perfect_interface.json");
                                return new GuiPerfectInterface(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerPatternsInterface, GuiPatternsInterface>register(
                        CrazyContainers.PATTERNS_INTERFACE.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_patterns_interface.json");
                                return new GuiPatternsInterface(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerTrashcan, GuiTrashcan>register(
                        CrazyContainers.TRASHCAN.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_trashcan.json");
                                return new GuiTrashcan(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerImprovedCondenser, GuiImprovedCondenser>register(
                        CrazyContainers.IMPROVED_CONDENSER.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_improved_condenser.json");
                                return new GuiImprovedCondenser(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerCraftingUnitsCombiner, GuiCraftingUnitsCombiner>register(
                        CrazyContainers.CRAFTING_UNITS_COMBINER.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_crafting_units_combiner.json");
                                return new GuiCraftingUnitsCombiner(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerQuantumCPU, GuiQuantumCPU>register(
                        CrazyContainers.QUANTUM_CPU.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_quantum_cpu.json");
                                return new GuiQuantumCPU(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerQuantumCPUCraftingBlocks, GuiQuantumCPUCraftingBlocks>register(
                        CrazyContainers.QUANTUM_CPU_CRAFTING_BLOCKS.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazyae_quantum_cpu_crafting_blocks.json");
                                return new GuiQuantumCPUCraftingBlocks(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerCrazyPriority, GuiCrazyPriority>register(
                        CrazyContainers.PRIORITY.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/crazy_priority.json");
                                return new GuiCrazyPriority(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerIOBus, GuiIOBus>register(
                        CrazyContainers.IMPROVED_EXPORT_BUS.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/improved_export_bus.json");
                                return new GuiIOBus(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
                event.<ContainerIOBus, GuiIOBus>register(
                        CrazyContainers.IMPROVED_IMPORT_BUS.get(),
                        (menu, playerInv, title) -> {
                            try {
                                var style = StyleManager.loadStyleDoc("/screens/improved_import_bus.json");
                                return new GuiIOBus(menu, playerInv, title, style);
                            } catch (Throwable t) {
                                CrazyAE.LOGGER.error("Failed to create GuiColorizer instance!", t);
                                throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                            }
                        }
                );
            } catch (Throwable t) {
                CrazyAE.LOGGER.error("err",t);
            }
        }
    }

    public void init() {}
}
