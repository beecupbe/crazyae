package dev.beecube31.crazyae2.common.tile.base;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.HolderLookup;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import dev.beecube31.crazyae2.common.interfaces.IOperationsCounterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CrazyAENetworkTileOC extends AENetworkedInvBlockEntity implements IOperationsCounterTile {

    protected double completedOperations;

    public CrazyAENetworkTileOC(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        data.putDouble("completedOperations", this.completedOperations);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.completedOperations = data.getDoubleOr("completedOperations", 0);
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