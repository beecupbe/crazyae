package dev.beecube31.crazyae2.common.blocks.networking;

import appeng.api.config.Upgrades;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockIOPortImp extends CrazyAEBlockAttribute {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockIOPortImp() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(POWERED, false));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileImprovedIOPort te = this.getTileEntity(worldIn, pos);
        boolean powred = te != null && te.isActive();

        return super.getActualState(state, worldIn, pos)
                .withProperty(POWERED, powred);
    }

    @Override
    protected IProperty[] getAEStates() {
        return new IProperty[]{POWERED};
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        final TileImprovedIOPort te = this.getTileEntity(world, pos);
        if (te != null) {
            if (te.getInstalledUpgrades(Upgrades.REDSTONE) != 0) {
                te.updateRedstoneState();
            }
        }
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }
        final TileImprovedIOPort tg = this.getTileEntity(w, pos);
        if (tg != null) {
            if (Platform.isServer()) {
                CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.IMPROVED_IO_PORT);
            }
            return true;
        }
        return false;
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequiredAEPerTick(64.0D);
    }
}