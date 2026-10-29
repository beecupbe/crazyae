package dev.beecube31.crazyae2.common.enums.crafting;

import appeng.block.crafting.ICraftingUnitType;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import net.minecraft.world.item.Item;

public enum QuantumCpuUnitType implements ICraftingUnitType {
    INSTANCE;

    @Override
    public long getStorageBytes() {
        return 0;
    }

    @Override
    public int getAcceleratorThreads() {
        return 0;
    }

    @Override
    public Item getItemFromType() {
        return CrazyBlocks.QUANTUM_CPU.get().asItem();
    }
}
