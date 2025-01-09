package dev.beecube31.crazyae2.common.base;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public abstract class CrazyAEModelBlock extends CrazyAEBlockAttribute {
    public CrazyAEModelBlock(Material mat) {
        super(mat);
        this.setFullSize(this.setOpaque(false));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
