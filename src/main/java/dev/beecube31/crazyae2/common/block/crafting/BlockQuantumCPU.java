package dev.beecube31.crazyae2.common.block.crafting;

import dev.beecube31.crazyae2.common.enums.crafting.QuantumCpuUnitType;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockQuantumCPU extends BlockCraftingUnitsCombiner {
    public BlockQuantumCPU() {
        super(QuantumCpuUnitType.INSTANCE);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                       boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileQuantumCPU cpu) {
            cpu.recoverContents(player);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (state.getBlock() != replacement.getBlock() && !level.isClientSide()
                && level.getBlockEntity(pos) instanceof TileQuantumCPU cpu) {
            cpu.recoverContents(null);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
