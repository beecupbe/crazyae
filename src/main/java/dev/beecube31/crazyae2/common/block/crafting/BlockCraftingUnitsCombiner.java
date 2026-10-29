package dev.beecube31.crazyae2.common.block.crafting;

import appeng.block.crafting.CraftingUnitBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
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
    public BlockCraftingUnitsCombiner(Properties props) {
        this(props, CrazyAECraftingUnitType.COMBINER);
    }

    protected BlockCraftingUnitsCombiner(Properties props, appeng.block.crafting.ICraftingUnitType unitType) {
        super(props, unitType);
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof TileCraftingUnitsCombiner be) {
            if (!level.isClientSide()) {
                ContainerOpener.open(CrazyContainers.CRAFTING_UNITS_COMBINER.get(), player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
