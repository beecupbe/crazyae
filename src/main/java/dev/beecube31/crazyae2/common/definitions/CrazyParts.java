package dev.beecube31.crazyae2.common.definitions;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import dev.beecube31.crazyae2.common.parts.*;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class CrazyParts {
    private CrazyParts() {}

    private static <T extends IPart> RegistryObject<PartItem<T>> part(String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return CrazyRegistries.ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), partClass, factory));
    }

    public static final RegistryObject<PartItem<PartPerfectInterface>> PERFECT_INTERFACE =
            part("cable_perfect_interface", PartPerfectInterface.class, PartPerfectInterface::new);
    public static final RegistryObject<PartItem<PartPatternsInterface>> PATTERNS_INTERFACE =
            part("cable_patterns_interface", PartPatternsInterface.class, PartPatternsInterface::new);
    public static final RegistryObject<PartItem<PartImprovedImportBus>> IMPROVED_IMPORT_BUS =
            part("improved_import_bus", PartImprovedImportBus.class, PartImprovedImportBus::new);
    public static final RegistryObject<PartItem<PartImprovedExportBus>> IMPROVED_EXPORT_BUS =
            part("improved_export_bus", PartImprovedExportBus.class, PartImprovedExportBus::new);
    public static final RegistryObject<PartItem<PartThroughputStorageMonitor>> THROUGHPUT_STORAGE_MONITOR =
            part("throughput_storage_monitor", PartThroughputStorageMonitor.class, PartThroughputStorageMonitor::new);
    public static final RegistryObject<PartItem<PartThroughputConversionMonitor>> THROUGHPUT_CONVERSION_MONITOR =
            part("throughput_conversion_monitor", PartThroughputConversionMonitor.class, PartThroughputConversionMonitor::new);

    public static void init() {}
}
