package dev.beecube31.crazyae2.client.rendering;

import appeng.bootstrap.BlockRenderingCustomizer;
import appeng.bootstrap.IBlockRendering;
import appeng.bootstrap.IItemRendering;
import dev.beecube31.crazyae2.client.rendering.tesr.ImprovedCrankTESR;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ImprovedCrankRendering extends BlockRenderingCustomizer {

    @Override
    @SideOnly(Side.CLIENT)
    public void customize(IBlockRendering rendering, IItemRendering itemRendering) {
        rendering.tesr(new ImprovedCrankTESR());
    }
}
