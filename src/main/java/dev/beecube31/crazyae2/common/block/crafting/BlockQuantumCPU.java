package dev.beecube31.crazyae2.common.block.crafting;

import dev.beecube31.crazyae2.common.enums.crafting.QuantumCpuUnitType;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;

public class BlockQuantumCPU extends BlockCraftingUnitsCombiner {
    public BlockQuantumCPU(Properties properties) {
        super(properties, QuantumCpuUnitType.INSTANCE);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileQuantumCPU cpu) {
            cpu.recoverContents(player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileQuantumCPU cpu) {
            cpu.openMenu(player);
        }
        return InteractionResult.SUCCESS;
    }
}
