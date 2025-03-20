package dev.beecube31.crazyae2.common.components.base;

import ic2.api.energy.prefab.BasicSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseEnergyEUDelegateSource extends BasicSource {
    public BaseEnergyEUDelegateSource(World world, BlockPos pos, double capacity, int tier) {
        super(world, pos, capacity, tier);
    }
}
