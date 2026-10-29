package dev.beecube31.crazyae2.common.block.crafting;

import appeng.block.AEBaseEntityBlock;
import appeng.util.InteractionUtil;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockImprovedMAC extends AEBaseEntityBlock<TileImprovedMAC> {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockImprovedMAC(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileImprovedMAC be) {
        return currentState.setValue(POWERED, be.isActive());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.PASS;
        } else {
            TileImprovedMAC be = this.getBlockEntity(level, pos);
            if (be != null) {
                if (!level.isClientSide()) {
                    be.openMenu(player);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

}
