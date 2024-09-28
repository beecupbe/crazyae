package dev.beecube31.crazyae2.client.rendering.tesr;


import appeng.client.render.FacingToRotation;
import dev.beecube31.crazyae2.common.tile.grindstone.TileImprovedCrank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ImprovedCrankTESR extends TileEntitySpecialRenderer<TileImprovedCrank> {

    @Override
    public void render(TileImprovedCrank te, double x, double y, double z, float partialTicks, int destroyStage, float p_render_10_) {
        // Most of this is blatantly copied from FastTESR
        Tessellator tessellator = Tessellator.getInstance();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        IBlockState blockState = te.getWorld().getBlockState(te.getPos());

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(blockState);

        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        // The translation ensures the vertex buffer positions are relative to 0,0,0 instead of the block pos
        // This makes the translations that follow much easier
        buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
        dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, blockState, te.getPos(), buffer, false);
        buffer.setTranslation(0, 0, 0);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        // Apply GL transformations relative to the center of the block: 1) TE rotation and 2) crank rotation
        GlStateManager.translate(0.5, 0.5, 0.5);
        FacingToRotation.get(te.getForward(), te.getUp()).glRotateCurrentMat();
        GlStateManager.rotate(te.getVisibleRotation(), 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        tessellator.draw();

        GlStateManager.popMatrix();

        RenderHelper.enableStandardItemLighting();
    }


}
