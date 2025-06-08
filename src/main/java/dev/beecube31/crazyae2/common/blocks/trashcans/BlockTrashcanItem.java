package dev.beecube31.crazyae2.common.blocks.trashcans;

import appeng.api.util.AEPartLocation;
import appeng.helpers.ICustomCollision;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockTrashcanItem extends CrazyAEBlockAttribute implements ICustomCollision {

    private static final List<AxisAlignedBB> COLLISION_BOXES = new ArrayList<>();

    static {
        COLLISION_BOXES.add(new AxisAlignedBB(0.375000, 0.937500, 0.375000, 0.625000, 1.000000, 0.625000));
        COLLISION_BOXES.add(new AxisAlignedBB(0.250000, 0.875000, 0.250000, 0.750000, 0.937500, 0.750000));
        COLLISION_BOXES.add(new AxisAlignedBB(0.187500, 0.062500, 0.187500, 0.812500, 0.812500, 0.812500));
        COLLISION_BOXES.add(new AxisAlignedBB(0.125000, 0.812500, 0.125000, 0.875000, 0.875000, 0.875000));
        COLLISION_BOXES.add(new AxisAlignedBB(0.250000, 0.000000, 0.250000, 0.750000, 0.062500, 0.750000));
    }

    @Override
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(final World w, final BlockPos pos, final Entity player, final boolean b) {
        return new ArrayList<>(COLLISION_BOXES);
    }

    @Override
    public void addCollidingBlockToList(final World w, final BlockPos pos, final AxisAlignedBB bb, final List<AxisAlignedBB> out, final Entity e) {
        out.addAll(COLLISION_BOXES);
    }

    public BlockTrashcanItem() {
        super(Material.IRON);

        this.setLightOpacity(0);
        this.setFullSize(this.setOpaque(false));
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }
        final TileEntity tg = this.getTileEntity(w, pos);
        if (tg != null) {
            if (Platform.isServer()) {
                CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.GUI_TRASHCAN_ITEMS);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequiredAEPerTick(4.0D).setRequireChannel(true);
    }
}