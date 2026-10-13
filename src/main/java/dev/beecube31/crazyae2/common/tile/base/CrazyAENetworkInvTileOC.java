package dev.beecube31.crazyae2.common.tile.base;

import appeng.blockentity.grid.AENetworkInvBlockEntity;
import dev.beecube31.crazyae2.common.interfaces.IOperationsCounterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CrazyAENetworkInvTileOC extends AENetworkInvBlockEntity implements IOperationsCounterTile {

    protected double completedOperations;

    public CrazyAENetworkInvTileOC(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.putDouble("completedOperations", this.completedOperations);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.completedOperations = data.getDouble("completedOperations");
    }

    @Override
    public double getCompletedOperations() {
        return this.completedOperations;
    }

    @Override
    public void addCompletedOperations() {
        this.completedOperations++;
    }

    @Override
    public void addCompletedOperations(double amt) {
        this.completedOperations += amt;
    }
}