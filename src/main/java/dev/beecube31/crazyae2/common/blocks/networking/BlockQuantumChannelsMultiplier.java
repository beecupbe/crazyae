package dev.beecube31.crazyae2.common.blocks.networking;

import appeng.block.AEBaseTileBlock;
import appeng.helpers.AEGlassMaterial;
import appeng.helpers.ICustomCollision;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import dev.beecube31.crazyae2.common.tile.networking.TileQuantumChannelsBooster;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class BlockQuantumChannelsMultiplier extends AEBaseTileBlock implements ICustomCollision {
    enum State implements IStringSerializable {
        OFF,
        ON,
        HAS_CHANNEL;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }

    public static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);

    public BlockQuantumChannelsMultiplier() {
        super(AEGlassMaterial.INSTANCE);
        this.setLightOpacity(0);
        this.setFullSize(false);
        this.setOpaque(false);
        this.setDefaultState(this.getDefaultState().withProperty(STATE, State.OFF));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        State teState = State.OFF;

        TileQuantumChannelsBooster te = this.getTileEntity(worldIn, pos);
        if (te != null) {
            if (te.isActive()) {
                teState = State.HAS_CHANNEL;
            } else if (te.isPowered()) {
                teState = State.ON;
            }
        }

        return super.getActualState(state, worldIn, pos)
                .withProperty(STATE, teState);
    }

    @Override
    protected IProperty[] getAEStates() {
        return new IProperty[]{STATE};
    }

    @Override
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(final World w, final BlockPos pos, final Entity thePlayer, final boolean b) {
        final TileQuantumChannelsBooster tile = this.getTileEntity(w, pos);
        if (tile != null) {
            final EnumFacing forward = tile.getForward();

            double minX = 0;
            double minY = 0;
            double minZ = 0;
            double maxX = 1;
            double maxY = 1;
            double maxZ = 1;

            switch (forward) {
                case DOWN:
                    minZ = minX = 3.0 / 16.0;
                    maxZ = maxX = 13.0 / 16.0;
                    maxY = 1.0;
                    minY = 5.0 / 16.0;
                    break;
                case EAST:
                    minZ = minY = 3.0 / 16.0;
                    maxZ = maxY = 13.0 / 16.0;
                    maxX = 11.0 / 16.0;
                    minX = 0.0;
                    break;
                case NORTH:
                    minY = minX = 3.0 / 16.0;
                    maxY = maxX = 13.0 / 16.0;
                    maxZ = 1.0;
                    minZ = 5.0 / 16.0;
                    break;
                case SOUTH:
                    minY = minX = 3.0 / 16.0;
                    maxY = maxX = 13.0 / 16.0;
                    maxZ = 11.0 / 16.0;
                    minZ = 0.0;
                    break;
                case UP:
                    minZ = minX = 3.0 / 16.0;
                    maxZ = maxX = 13.0 / 16.0;
                    maxY = 11.0 / 16.0;
                    minY = 0.0;
                    break;
                case WEST:
                    minZ = minY = 3.0 / 16.0;
                    maxZ = maxY = 13.0 / 16.0;
                    maxX = 1.0;
                    minX = 5.0 / 16.0;
                    break;
                default:
                    break;
            }

            return Collections.singletonList(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
        }
        return Collections.singletonList(new AxisAlignedBB(0.0, 0, 0.0, 1.0, 1.0, 1.0));
    }

    @Override
    public void addCollidingBlockToList(final World w, final BlockPos pos, final AxisAlignedBB bb, final List<AxisAlignedBB> out, final Entity e) {
        final TileQuantumChannelsBooster tile = this.getTileEntity(w, pos);
        if (tile != null) {
            final EnumFacing forward = tile.getForward();

            double minX = 0;
            double minY = 0;
            double minZ = 0;
            double maxX = 1;
            double maxY = 1;
            double maxZ = 1;

            switch (forward) {
                case DOWN:
                    minZ = minX = 3.0 / 16.0;
                    maxZ = maxX = 13.0 / 16.0;
                    maxY = 1.0;
                    minY = 5.0 / 16.0;
                    break;
                case EAST:
                    minZ = minY = 3.0 / 16.0;
                    maxZ = maxY = 13.0 / 16.0;
                    maxX = 11.0 / 16.0;
                    minX = 0.0;
                    break;
                case NORTH:
                    minY = minX = 3.0 / 16.0;
                    maxY = maxX = 13.0 / 16.0;
                    maxZ = 1.0;
                    minZ = 5.0 / 16.0;
                    break;
                case SOUTH:
                    minY = minX = 3.0 / 16.0;
                    maxY = maxX = 13.0 / 16.0;
                    maxZ = 11.0 / 16.0;
                    minZ = 0.0;
                    break;
                case UP:
                    minZ = minX = 3.0 / 16.0;
                    maxZ = maxX = 13.0 / 16.0;
                    maxY = 11.0 / 16.0;
                    minY = 0.0;
                    break;
                case WEST:
                    minZ = minY = 3.0 / 16.0;
                    maxZ = maxY = 13.0 / 16.0;
                    maxX = 1.0;
                    minX = 5.0 / 16.0;
                    break;
                default:
                    break;
            }

            out.add(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
        } else {
            out.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        final int qmc = CrazyAEConfig.QCMBoostAmt;
        tooltip.add(CrazyAETooltip.QCM_DESC.getLocal());
        tooltip.add(String.format(CrazyAETooltip.QCM_DESC1.getLocal(), qmc / 4));
        tooltip.add(String.format(CrazyAETooltip.QCM_DESC2.getLocal(), qmc, qmc * 256));

        super.addInformation(stack, player, tooltip, advanced);
    }
}
