package dev.beecube31.crazyae2.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtils {
    public static ItemStack getItemStackFromBlock(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return ItemStack.EMPTY;
        }
        IBlockState blockState = world.getBlockState(pos);
        return blockState.getBlock().getPickBlock(blockState, null, world, pos, null);
    }
}
