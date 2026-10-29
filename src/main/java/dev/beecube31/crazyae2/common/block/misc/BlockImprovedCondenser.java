package dev.beecube31.crazyae2.common.block.misc;

import net.minecraft.world.level.block.state.BlockState;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockImprovedCondenser extends AEBaseEntityBlock<TileImprovedCondenser> {
    public BlockImprovedCondenser() {
        super(metalProps());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.PASS;
        }
        var be = getBlockEntity(level, pos);
        if (be == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ContainerOpener.open(CrazyContainers.IMPROVED_CONDENSER.get(), player, MenuLocators.forBlockEntity(be));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
