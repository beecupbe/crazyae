package dev.beecube31.crazyae2.common.blocks.misc;

import appeng.api.util.AEPartLocation;
import appeng.block.AEBaseTileBlock;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockImprovedCondenser extends AEBaseTileBlock {

    public BlockImprovedCondenser() {
        super(Material.IRON);
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer player, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        if (Platform.isServer()) {
            final TileImprovedCondenser tc = this.getTileEntity(w, pos);
            if (tc != null && !player.isSneaking()) {
                CrazyAEGuiHandler.openGUI(player, tc, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.IMPROVED_CONDENSER);
                return true;
            }
        }

        return true;
    }
}
