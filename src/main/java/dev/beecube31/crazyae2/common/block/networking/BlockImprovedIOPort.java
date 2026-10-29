package dev.beecube31.crazyae2.common.block.networking;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.util.InteractionUtil;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockImprovedIOPort extends AEBaseEntityBlock<TileImprovedIOPort> {

    public final static BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockImprovedIOPort() {
        super(metalProps());
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        final TileImprovedIOPort te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.updateRedstoneState();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileImprovedIOPort be) {
        return currentState.setValue(POWERED, be.isActive());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player p, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return InteractionResult.PASS;
        }

        final TileImprovedIOPort tg = this.getBlockEntity(level, pos);
        if (tg != null) {
            if (!level.isClientSide()) {
                tg.openMenu(p);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
