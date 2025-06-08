package dev.beecube31.crazyae2.common.blocks.crafting;

import appeng.api.util.AEPartLocation;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.base.CrazyAEBlockAttribute;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockQuantumCPU extends CrazyAEBlockAttribute {

    public BlockQuantumCPU() {
        super(Material.IRON);

        this.setOpaque(false);
        this.lightOpacity = 1;
    }

    /**
     * NOTE: This is only used to determine how to render an item being held in hand.
     * For determining block rendering, the method below is used (canRenderInLayer).
     */
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(Utils.writeSpriteFlag(Sprite.INFO) + CrazyAETooltip.QUANTUM_CPU_WARNING.getLocal());
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (this.getTileEntity(w, pos) instanceof TileQuantumCPU tg && !p.isSneaking()) {
            CrazyAEGuiHandler.openGUI(p, tg, AEPartLocation.fromFacing(side), CrazyAEGuiBridge.QUANTUM_CPU_HOST);
            return true;
        }

        return false;
    }

    @Override
    public MachineAttributes getAttributes() {
        return new MachineAttributes().setRequiredAEPerTick(4096D).setRequireChannel(true);
    }
}
