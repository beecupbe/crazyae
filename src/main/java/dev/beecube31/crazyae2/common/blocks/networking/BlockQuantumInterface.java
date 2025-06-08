package dev.beecube31.crazyae2.common.blocks.networking;

import appeng.api.util.AEPartLocation;
import appeng.api.util.IOrientable;
import appeng.util.Platform;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.networking.TileQuantumInterface;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockQuantumInterface extends CrazyAEBlockAttribute {

    private static final PropertyBool OMNIDIRECTIONAL = PropertyBool.create("omnidirectional");

    public BlockQuantumInterface() {
        super(Material.IRON);
    }

    @Override
    protected IProperty[] getAEStates() {
        return new IProperty[]{OMNIDIRECTIONAL};
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileQuantumInterface te = this.getTileEntity(world, pos);
        boolean omniDirectional = true; // The default
        if (te != null) {
            omniDirectional = te.isOmniDirectional();
        }

        return super.getActualState(state, world, pos)
                .withProperty(OMNIDIRECTIONAL, omniDirectional);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(Utils.writeSpriteFlag(Sprite.INFO) + CrazyAETooltip.QUANTUM_INTERFACE_WARNING.getLocal());
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }

        final TileQuantumInterface tg = this.getTileEntity(w, pos);
        if (tg != null) {
            if (Platform.isServer()) {
                CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.QUANTUM_INTERFACE);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasCustomRotation() {
        return true;
    }

    @Override
    protected void customRotateBlock(final IOrientable rotatable, final EnumFacing axis) {
        if (rotatable instanceof TileQuantumInterface) {
            ((TileQuantumInterface) rotatable).setSide(axis);
        }
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequireChannel(true);
    }
}
