package dev.beecube31.crazyae2.common.enums;

import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public record CrazyStorageTier(int index, String namePrefix, long bytes, double idleDrain, Supplier<? extends Item> componentSupplier) {
    public static final CrazyStorageTier SIZE_1MB = new CrazyStorageTier(1, "1MB", 1024 * 1024, 6,
            CrazyItems.CELL_COMPONENT_1MB);
    public static final CrazyStorageTier SIZE_4MB = new CrazyStorageTier(2, "4MB", 1024 * 1024 * 4, 10,
            CrazyItems.CELL_COMPONENT_4MB);
    public static final CrazyStorageTier SIZE_16MB = new CrazyStorageTier(3, "16MB", 1024 * 1024 * 16, 12,
            CrazyItems.CELL_COMPONENT_16MB);
    public static final CrazyStorageTier SIZE_64MB = new CrazyStorageTier(4, "64MB", 1024 * 1024 * 64, 16,
            CrazyItems.CELL_COMPONENT_64MB);
    public static final CrazyStorageTier SIZE_256MB = new CrazyStorageTier(5, "256MB", 1024 * 1024 * 256, 20,
            CrazyItems.CELL_COMPONENT_256MB);
    public static final CrazyStorageTier SIZE_1GB = new CrazyStorageTier(6, "1GB", 1024 * 1024 * 1024, 24,
            CrazyItems.CELL_COMPONENT_1GB);
    public static final CrazyStorageTier SIZE_2GB = new CrazyStorageTier(7, "2GB", 1024L * 1024 * 1024 * 2, 32,
            CrazyItems.CELL_COMPONENT_2GB);
    public static final CrazyStorageTier SIZE_8GB = new CrazyStorageTier(8, "8GB", 1024L * 1024 * 1024 * 8, 48,
            CrazyItems.CELL_COMPONENT_8GB);
    public static final CrazyStorageTier SIZE_32GB = new CrazyStorageTier(9, "32GB", 1024L * 1024 * 1024 * 32, 64,
            CrazyItems.CELL_COMPONENT_32GB);
    public static final CrazyStorageTier SIZE_128GB = new CrazyStorageTier(10, "128GB", 1024L * 1024 * 1024 * 128, 96,
            CrazyItems.CELL_COMPONENT_128GB);

    public Item getComponent() {
        return componentSupplier.get();
    }
}
