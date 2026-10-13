package dev.beecube31.crazyae2.core.registries.client;

import appeng.client.gui.style.StyleManager;
import dev.beecube31.crazyae2.client.gui.implementations.*;
import dev.beecube31.crazyae2.common.containers.implementations.*;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RegisterFactoryClient {

    public static final RegisterFactoryClient INSTANCE = new RegisterFactoryClient();

    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.<ContainerImprovedMAC, GuiImprovedMAC>register(
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

            MenuScreens.<ContainerDriveImproved, GuiImprovedDrive>register(
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

            MenuScreens.<ContainerImprovedIOPort, GuiImprovedIOPort>register(
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

            MenuScreens.<ContainerBigCrystalCharger, GuiImprovedCrystalCharger>register(
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
            MenuScreens.<ContainerColorizer, GuiColorizer>register(
                    CrazyContainers.COLORIZER.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/colorizer.json");
                            return new GuiColorizer(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerPerfectInterface, GuiPerfectInterface>register(
                    CrazyContainers.PERFECT_INTERFACE.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazyae_perfect_interface.json");
                            return new GuiPerfectInterface(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerPatternsInterface, GuiPatternsInterface>register(
                    CrazyContainers.PATTERNS_INTERFACE.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazyae_patterns_interface.json");
                            return new GuiPatternsInterface(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerTrashcan, GuiTrashcan>register(
                    CrazyContainers.TRASHCAN.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazyae_trashcan.json");
                            return new GuiTrashcan(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerImprovedCondenser, GuiImprovedCondenser>register(
                    CrazyContainers.IMPROVED_CONDENSER.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazyae_improved_condenser.json");
                            return new GuiImprovedCondenser(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerCraftingUnitsCombiner, GuiCraftingUnitsCombiner>register(
                    CrazyContainers.CRAFTING_UNITS_COMBINER.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazyae_crafting_units_combiner.json");
                            return new GuiCraftingUnitsCombiner(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerCrazyPriority, GuiCrazyPriority>register(
                    CrazyContainers.PRIORITY.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/crazy_priority.json");
                            return new GuiCrazyPriority(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerIOBus, GuiIOBus>register(
                    CrazyContainers.IMPROVED_EXPORT_BUS.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/improved_export_bus.json");
                            return new GuiIOBus(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
            MenuScreens.<ContainerIOBus, GuiIOBus>register(
                    CrazyContainers.IMPROVED_IMPORT_BUS.get(),
                    (menu, playerInv, title) -> {
                        try {
                            var style = StyleManager.loadStyleDoc("/screens/improved_import_bus.json");
                            return new GuiIOBus(menu, playerInv, title, style);
                        } catch (Throwable t) {
                            throw new RuntimeException("A critical exception was thrown during GUI initialization", t);
                        }
                    }
            );
        });
    }

    public void init() {}
}
