package dev.beecube31.crazyae2.common.block.grindstone;

import appeng.block.misc.CrankBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class ImprovedCrankBlock extends CrankBlock {
    public ImprovedCrankBlock() {
        super(defaultProps(MapColor.METAL, SoundType.METAL).isViewBlocking((state, level, pos) -> false).noCollission());
    }
}
