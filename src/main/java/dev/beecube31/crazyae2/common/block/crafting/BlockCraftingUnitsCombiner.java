package dev.beecube31.crazyae2.common.block.crafting;

import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class BlockCraftingUnitsCombiner extends CraftingUnitBlock {
    public BlockCraftingUnitsCombiner() {
        this(CrazyAECraftingUnitType.COMBINER);
    }

    protected BlockCraftingUnitsCombiner(ICraftingUnitType type) {
        super(type);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof TileCraftingUnitsCombiner be) {
            if (!level.isClientSide()) {
                be.openMenu(player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
