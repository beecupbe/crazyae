package dev.beecube31.crazyae2.common.components.base;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseEnergyEUDelegateSink extends BasicSink {
    public BaseEnergyEUDelegateSink(World world, BlockPos pos, double capacity, int tier) {
        super(world, pos, capacity, tier);
    }
}
