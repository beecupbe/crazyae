package dev.beecube31.crazyae2.common.enums.crafting;

import appeng.block.crafting.ICraftingUnitType;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public enum CrazyAECraftingUnitType implements ICraftingUnitType {
    COMBINER(0, 0, "crafting_units_combiner"),
    ACCELERATOR_4X(0, 4, "4x_crafting_accelerator"),
    ACCELERATOR_16X(0, 16, "16x_crafting_accelerator"),
    ACCELERATOR_64X(0, 64, "64x_crafting_accelerator"),
    ACCELERATOR_256X(0, 256, "256x_crafting_accelerator"),
    ACCELERATOR_1024X(0, 1024, "1024x_crafting_accelerator"),
    ACCELERATOR_4096X(0, 4096, "4096x_crafting_accelerator"),
    ACCELERATOR_16384X(0, 16384, "16384x_crafting_accelerator"),
    ACCELERATOR_65536X(0, 65536, "65536x_crafting_accelerator"),
    ACCELERATOR_262144X(0, 262144, "262144x_crafting_accelerator"),
    ACCELERATOR_1048576X(0, 1048576, "1048576x_crafting_accelerator"),
    ACCELERATOR_4194304X(0, 4194304, "4194304x_crafting_accelerator"),
    ACCELERATOR_CREATIVE(0, 1 << 28, "creative_crafting_accelerator"),

    STORAGE_1MB(1024L, 0, "1mb_crafting_storage"),
    STORAGE_4MB(1024L * 4, 0, "4mb_crafting_storage"),
    STORAGE_16MB(1024L * 16, 0, "16mb_crafting_storage"),
    STORAGE_64MB(1024L * 64, 0, "64mb_crafting_storage"),
    STORAGE_256MB(1024L * 256, 0, "256mb_crafting_storage"),
    STORAGE_1GB(1024L * 1024, 0, "1gb_crafting_storage"),
    STORAGE_2GB(1024L * 1024 * 2, 0, "2gb_crafting_storage"),
    STORAGE_8GB(1024L * 1024 * 8, 0, "8gb_crafting_storage"),
    STORAGE_32GB(1024L * 1024 * 32, 0, "32gb_crafting_storage"),
    STORAGE_128GB(1024L * 1024 * 128, 0, "128gb_crafting_storage"),

    STORAGE_CREATIVE(1L << 42, 0, "creative_crafting_storage");

    private final long storageKb;
    private final int accelCount;
    private final String name;

    CrazyAECraftingUnitType(long storageKb, int accelCount, String name) {
        this.storageKb = storageKb;
        this.accelCount = accelCount;
        this.name = name;
    }

    public boolean isAccelerator() {
        return accelCount > 0;
    }

    public boolean isCreative() {
        return this == ACCELERATOR_CREATIVE || this == STORAGE_CREATIVE;
    }

    public int getAccelCount() {
        return accelCount;
    }

    @Override
    public long getStorageBytes() {
        return 1024L * this.storageKb;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getAcceleratorThreads() {
        return this.getAccelCount();
    }

    @Override
    public Item getItemFromType() {
        return CrazyBlocks.getCraftingBlock(this).asItem();
    }

    public static Optional<CrazyAECraftingUnitType> fromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        for (CrazyAECraftingUnitType type : values()) {
            if (type != COMBINER && stack.is(type.getItemFromType())) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
