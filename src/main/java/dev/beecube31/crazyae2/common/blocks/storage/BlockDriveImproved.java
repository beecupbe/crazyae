package dev.beecube31.crazyae2.common.blocks.storage;

import appeng.api.util.AEPartLocation;
import appeng.block.storage.DriveSlotsState;
import appeng.client.UnlistedProperty;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockDriveImproved extends CrazyAEBlockAttribute {

    public static final UnlistedProperty<DriveSlotsState> SLOTS_STATE = new UnlistedProperty<>("drive_slots_state", DriveSlotsState.class);

    public BlockDriveImproved() {
        super(Material.IRON);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, this.getAEStates(), new IUnlistedProperty[]{
                SLOTS_STATE,
                FORWARD,
                UP
        });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileImprovedDrive te = this.getTileEntity(world, pos);
        IExtendedBlockState extState = (IExtendedBlockState) super.getExtendedState(state, world, pos);
        return extState.withProperty(SLOTS_STATE, te == null ? DriveSlotsState.createEmpty(35) : DriveSlotsState.fromChestOrDrive(te));
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state, final EntityPlayer p, final EnumHand hand, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (this.getTileEntity(w, pos) instanceof TileImprovedDrive tg && !p.isSneaking()) {
            CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.IMPROVED_DRIVE);
            return true;
        }

        return super.onBlockActivated(w, pos, state, p, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequireChannel(true);
    }
}
