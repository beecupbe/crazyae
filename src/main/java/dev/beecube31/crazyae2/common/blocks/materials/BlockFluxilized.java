package dev.beecube31.crazyae2.common.blocks.materials;

import appeng.block.AEDecorativeBlock;
import appeng.client.render.effects.LightningFX;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFluxilized extends AEDecorativeBlock {

    public BlockFluxilized() {
        super(Material.ROCK);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World w, final BlockPos pos, final Random r) {
        if (!AEConfig.instance().isEnableEffects()) {
            return;
        }

        if (r.nextFloat() < 0.85) {
            return;
        }

        final double xOff = 0.0;
        final double yOff = 0.0;
        final double zOff = 0.0;

        for (int bolts = 0; bolts < 3; bolts++) {
            if (AppEng.proxy.shouldAddParticles(r)) {
                final LightningFX fx = new LightningFX(w, xOff + 0.5 + pos.getX(), yOff + 0.5 + pos.getY(), zOff + 0.5 + pos
                        .getZ(), 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(fx);
            }
        }
    }
}
