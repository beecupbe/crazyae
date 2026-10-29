package dev.beecube31.crazyae2.common.definitions;

import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.items.materials.MaterialItem;
import appeng.menu.me.common.MEStorageMenu;
import dev.beecube31.crazyae2.common.enums.CrazyStorageTier;
import dev.beecube31.crazyae2.common.item.cell.CrazyAEStorageCell;
import dev.beecube31.crazyae2.common.item.cell.CrazyPortableCellItem;
import dev.beecube31.crazyae2.common.item.material.CrazyStorageComponentItem;
import dev.beecube31.crazyae2.common.item.tools.ToolColorizer;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class CrazyItems {

    private static final List<DeferredItem<? extends Item>> CELLS = new ArrayList<>();
    private static final List<DeferredItem<? extends Item>> PORTABLE_CELLS = new ArrayList<>();

    private static final int ITEM_TYPES = 63;
    private static final int FLUID_TYPES = 18;
    private static final int BYTES_PER_TYPE = 1;

    private CrazyItems() {}

    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
        return CrazyRegistries.ITEMS.register(name, itemSupplier);
    }

    private static DeferredItem<CrazyStorageComponentItem> component(String name, long kilobytes) {
        return registerItem(name, () -> new CrazyStorageComponentItem(new Item.Properties(), kilobytes));
    }

    private static DeferredItem<CrazyAEStorageCell> cell(String name, Supplier<? extends ItemLike> component,
                                                           ItemLike housing, CrazyStorageTier tier,
                                                           int types, AEKeyType keyType) {
        var ro = registerItem(name, () -> new CrazyAEStorageCell(new Item.Properties().stacksTo(1),
                component.get(), housing, tier.idleDrain(), tier.bytes() / 1024L, BYTES_PER_TYPE, types, keyType));
        CELLS.add(ro);
        return ro;
    }

    private static DeferredItem<CrazyPortableCellItem> portable(String name, CrazyStorageTier tier, AEKeyType keyType, double power) {
        boolean items = keyType == AEKeyType.items();
        var ro = registerItem(name, () -> new CrazyPortableCellItem(keyType,
                items ? ITEM_TYPES : FLUID_TYPES,
                items ? MEStorageMenu.PORTABLE_ITEM_CELL_TYPE : MEStorageMenu.PORTABLE_FLUID_CELL_TYPE,
                tier, new Item.Properties().stacksTo(1), items ? 0x80caff : 0xff8000, power));
        PORTABLE_CELLS.add(ro);
        return ro;
    }


    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_1MB = component("storage_cell_component_1mb", 1024L);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_4MB = component("storage_cell_component_4mb", 1024L * 4);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_16MB = component("storage_cell_component_16mb", 1024L * 16);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_64MB = component("storage_cell_component_64mb", 1024L * 64);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_256MB = component("storage_cell_component_256mb", 1024L * 256);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_1GB = component("storage_cell_component_1gb", 1024L * 1024);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_2GB = component("storage_cell_component_2gb", 1024L * 1024 * 2);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_8GB = component("storage_cell_component_8gb", 1024L * 1024 * 8);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_32GB = component("storage_cell_component_32gb", 1024L * 1024 * 32);
    public static final DeferredItem<CrazyStorageComponentItem> CELL_COMPONENT_128GB = component("storage_cell_component_128gb", 1024L * 1024 * 128);

    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_1MB = component("fluid_storage_cell_component_1mb", 1024L);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_4MB = component("fluid_storage_cell_component_4mb", 1024L * 4);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_16MB = component("fluid_storage_cell_component_16mb", 1024L * 16);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_64MB = component("fluid_storage_cell_component_64mb", 1024L * 64);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_256MB = component("fluid_storage_cell_component_256mb", 1024L * 256);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_1GB = component("fluid_storage_cell_component_1gb", 1024L * 1024);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_2GB = component("fluid_storage_cell_component_2gb", 1024L * 1024 * 2);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_8GB = component("fluid_storage_cell_component_8gb", 1024L * 1024 * 8);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_32GB = component("fluid_storage_cell_component_32gb", 1024L * 1024 * 32);
    public static final DeferredItem<CrazyStorageComponentItem> FLUID_CELL_COMPONENT_128GB = component("fluid_storage_cell_component_128gb", 1024L * 1024 * 128);


    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_1MB = cell("item_cell_1mb", CELL_COMPONENT_1MB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_1MB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_4MB = cell("item_cell_4mb", CELL_COMPONENT_4MB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_4MB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_16MB = cell("item_cell_16mb", CELL_COMPONENT_16MB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_16MB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_64MB = cell("item_cell_64mb", CELL_COMPONENT_64MB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_64MB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_256MB = cell("item_cell_256mb", CELL_COMPONENT_256MB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_256MB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_1GB = cell("item_cell_1gb", CELL_COMPONENT_1GB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_1GB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_2GB = cell("item_cell_2gb", CELL_COMPONENT_2GB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_2GB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_8GB = cell("item_cell_8gb", CELL_COMPONENT_8GB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_8GB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_32GB = cell("item_cell_32gb", CELL_COMPONENT_32GB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_32GB, ITEM_TYPES, AEKeyType.items());
    public static final DeferredItem<CrazyAEStorageCell> ITEM_CELL_128GB = cell("item_cell_128gb", CELL_COMPONENT_128GB, AEItems.ITEM_CELL_HOUSING, CrazyStorageTier.SIZE_128GB, ITEM_TYPES, AEKeyType.items());


    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_1MB = cell("fluid_cell_1mb", FLUID_CELL_COMPONENT_1MB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_1MB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_4MB = cell("fluid_cell_4mb", FLUID_CELL_COMPONENT_4MB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_4MB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_16MB = cell("fluid_cell_16mb", FLUID_CELL_COMPONENT_16MB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_16MB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_64MB = cell("fluid_cell_64mb", FLUID_CELL_COMPONENT_64MB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_64MB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_256MB = cell("fluid_cell_256mb", FLUID_CELL_COMPONENT_256MB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_256MB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_1GB = cell("fluid_cell_1gb", FLUID_CELL_COMPONENT_1GB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_1GB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_2GB = cell("fluid_cell_2gb", FLUID_CELL_COMPONENT_2GB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_2GB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_8GB = cell("fluid_cell_8gb", FLUID_CELL_COMPONENT_8GB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_8GB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_32GB = cell("fluid_cell_32gb", FLUID_CELL_COMPONENT_32GB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_32GB, FLUID_TYPES, AEKeyType.fluids());
    public static final DeferredItem<CrazyAEStorageCell> FLUID_CELL_128GB = cell("fluid_cell_128gb", FLUID_CELL_COMPONENT_128GB, AEItems.FLUID_CELL_HOUSING, CrazyStorageTier.SIZE_128GB, FLUID_TYPES, AEKeyType.fluids());


    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_1MB = portable("portable_cell_1mb", CrazyStorageTier.SIZE_1MB, AEKeyType.items(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_4MB = portable("portable_cell_4mb", CrazyStorageTier.SIZE_4MB, AEKeyType.items(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_16MB = portable("portable_cell_16mb", CrazyStorageTier.SIZE_16MB, AEKeyType.items(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_64MB = portable("portable_cell_64mb", CrazyStorageTier.SIZE_64MB, AEKeyType.items(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_256MB = portable("portable_cell_256mb", CrazyStorageTier.SIZE_256MB, AEKeyType.items(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_1GB = portable("portable_cell_1gb", CrazyStorageTier.SIZE_1GB, AEKeyType.items(),480000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_2GB = portable("portable_cell_2gb", CrazyStorageTier.SIZE_2GB, AEKeyType.items(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_8GB = portable("portable_cell_8gb", CrazyStorageTier.SIZE_8GB, AEKeyType.items(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_32GB = portable("portable_cell_32gb", CrazyStorageTier.SIZE_32GB, AEKeyType.items(), 1600000d);
    public static final DeferredItem<CrazyPortableCellItem> PORTABLE_CELL_128GB = portable("portable_cell_128gb", CrazyStorageTier.SIZE_128GB, AEKeyType.items(), 1600000d);

    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_1MB = portable("fluid_portable_cell_1mb", CrazyStorageTier.SIZE_1MB, AEKeyType.fluids(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_4MB = portable("fluid_portable_cell_4mb", CrazyStorageTier.SIZE_4MB, AEKeyType.fluids(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_16MB = portable("fluid_portable_cell_16mb", CrazyStorageTier.SIZE_16MB, AEKeyType.fluids(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_64MB = portable("fluid_portable_cell_64mb", CrazyStorageTier.SIZE_64MB, AEKeyType.fluids(), 120000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_256MB = portable("fluid_portable_cell_256mb", CrazyStorageTier.SIZE_256MB, AEKeyType.fluids(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_1GB = portable("fluid_portable_cell_1gb", CrazyStorageTier.SIZE_1GB, AEKeyType.fluids(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_2GB = portable("fluid_portable_cell_2gb", CrazyStorageTier.SIZE_2GB, AEKeyType.fluids(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_8GB = portable("fluid_portable_cell_8gb", CrazyStorageTier.SIZE_8GB, AEKeyType.fluids(), 480000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_32GB = portable("fluid_portable_cell_32gb", CrazyStorageTier.SIZE_32GB, AEKeyType.fluids(), 1600000d);
    public static final DeferredItem<CrazyPortableCellItem> FLUID_PORTABLE_CELL_128GB = portable("fluid_portable_cell_128gb", CrazyStorageTier.SIZE_128GB, AEKeyType.fluids(), 1600000d);


    public static final DeferredItem<MaterialItem> FLUIXILIZED_CRYSTAL = registerItem("fluixilized_crystal",
            () -> new MaterialItem(new Item.Properties()));
    public static final DeferredItem<MaterialItem> QUANTUM_PROCESSOR = registerItem("quantum_processor",
            () -> new MaterialItem(new Item.Properties()));


    public static final DeferredItem<Item> IMPROVED_SPEED_CARD = registerItem("improved_speed_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties()));
    public static final DeferredItem<Item> ADVANCED_SPEED_CARD = registerItem("advanced_speed_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties()));
    public static final DeferredItem<Item> STACKS_CARD = registerItem("stacks_card",
            () -> Upgrades.createUpgradeCardItem(new Item.Properties()));


    public static final DeferredItem<ToolColorizer> COLORIZER = registerItem("colorizer",
            () -> new ToolColorizer(new Item.Properties().stacksTo(1)));

    public static List<DeferredItem<? extends Item>> getCells() {
        return Collections.unmodifiableList(CELLS);
    }

    public static List<DeferredItem<? extends Item>> getPortableCells() {
        return Collections.unmodifiableList(PORTABLE_CELLS);
    }

    public static void init() {}
}
