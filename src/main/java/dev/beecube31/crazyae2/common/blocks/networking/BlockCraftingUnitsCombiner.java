package dev.beecube31.crazyae2.common.blocks.networking;

import appeng.api.util.AEPartLocation;
import appeng.helpers.ICustomCollision;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockCraftingUnitsCombiner extends CrazyAEBlockAttribute implements ICustomCollision {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockCraftingUnitsCombiner() {
        super(Material.IRON);

        this.setLightOpacity(2);
        this.setFullSize(this.setOpaque(false));
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        if (!worldIn.isRemote) {
            final TileCraftingUnitsCombiner te = this.getTileEntity(worldIn, pos);
            if (te != null) {
                te.removeCluster();
            }
        }
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            final TileCraftingUnitsCombiner te = this.getTileEntity(worldIn, pos);
            if (te != null) {
                te.removeCluster();
            }
        }
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }
        final TileCraftingUnitsCombiner tg = this.getTileEntity(w, pos);
        if (tg != null) {
            if (Platform.isServer()) {
                CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.CRAFTING_UNITS_COMBINER);
            }
            return true;
        }
        return false;
    }

    @Override
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(final World w, final BlockPos pos, final Entity thePlayer, final boolean b) {
        return Collections.singletonList(new AxisAlignedBB(0.0, 0, 0.0, 1.0, 0.3125, 1.0));
    }

    @Override
    public void addCollidingBlockToList(final World w, final BlockPos pos, final AxisAlignedBB bb, final List<AxisAlignedBB> out, final Entity e) {
        out.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequiredAEPerTick(1024.0D);
    }
}