package dev.beecube31.crazyae2.common.tile.energy;

import appeng.blockentity.networking.EnergyCellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileCrazyEnergyCell extends EnergyCellBlockEntity {
    public TileCrazyEnergyCell(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);

        this.getMainNode()
                .setVisualRepresentation(blockState.getBlock());
    }
}
