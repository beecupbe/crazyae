package dev.beecube31.crazyae2.common.blocks.botania;

import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.base.CrazyAEModelBlock;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalManapool;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMechanicalManapool extends CrazyAEModelBlock {

    public BlockMechanicalManapool() {
        super(Material.IRON);
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer player, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        if (Platform.isServer()) {
            final TileMechanicalManapool tc = this.getTileEntity(w, pos);
            if (tc != null && !player.isSneaking()) {
                CrazyAEGuiHandler.openGUI(player, tc, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.GUI_MANAPOOL_MECHANICAL);
                return true;
            }
        }

        return true;
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequiredAEPerTick(64.0D).setRequireChannel(true);
    }
}
