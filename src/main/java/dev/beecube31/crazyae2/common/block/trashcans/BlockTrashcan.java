package dev.beecube31.crazyae2.common.block.trashcans;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcan;
import dev.beecube31.crazyae2.common.enums.TrashcanType;
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

public class BlockTrashcan extends AEBaseEntityBlock<TileTrashcan> {
    private final TrashcanType kind;

    public BlockTrashcan(Properties props, TrashcanType kind) {
        super(metalProps(props).noOcclusion());
        this.kind = kind;
    }

    public TrashcanType getKind() {
        return kind;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        var be = getBlockEntity(level, pos);
        if (be == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ContainerOpener.open(CrazyContainers.TRASHCAN.get(), player, MenuLocators.forBlockEntity(be));
        }
        return InteractionResult.SUCCESS;
    }
}
