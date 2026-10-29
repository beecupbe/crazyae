package dev.beecube31.crazyae2.common.tile.grindstone;

import appeng.block.misc.CrankBlock;
import appeng.blockentity.misc.CrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ImprovedCrankBlockEntity extends CrankBlockEntity {
    public ImprovedCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean power() {
        boolean started = super.power();
        if (started && level != null && getBlockState().getBlock() instanceof CrankBlock crank) {
            var crankable = crank.getCrankable(getBlockState(), level, getBlockPos());
            if (crankable != null && crankable.canTurn()) {
                crankable.applyTurn();
            }
        }
        return started;
    }
}
