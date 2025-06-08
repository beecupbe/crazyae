package dev.beecube31.crazyae2.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {
    public static void renderBar(ResourceLocation bar, int x, int y, int color, float alpha, double current, double max) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        mc.renderEngine.bindTexture(bar);
        drawTexturedModalRect(x, y, 0.0F, 0, 0, 102, 5);
        int percentage = Math.max(0, (int)(current / max * 100.0));
        if (percentage == 0 && current > 0) {
            percentage = 1;
        }

        drawTexturedModalRect(x + 1, y + 1, 0.0F, 0, 5, 100, 3);
        Color col = new Color(color);
        GL11.glColor4ub((byte)col.getRed(), (byte)col.getGreen(), (byte)col.getBlue(), (byte)((int)(255.0F * alpha)));
        drawTexturedModalRect(x + 1, y + 1, 0.0F, 0, 5, Math.min(100, percentage), 3);
        GL11.glColor4ub((byte)-1, (byte)-1, (byte)-1, (byte)-1);
    }

    public static void drawTexturedModalRect(int par1, int par2, float z, int par3, int par4, int par5, int par6) {
        drawTexturedModalRect(par1, par2, z, par3, par4, par5, par6, 0.00390625F, 0.00390625F);
    }

    public static void drawTexturedModalRect(int par1, int par2, float z, int par3, int par4, int par5, int par6, float f, float f1) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(par1, par2 + par6, z).tex((float)par3 * f, (float)(par4 + par6) * f1).endVertex();
        tessellator.getBuffer().pos(par1 + par5, par2 + par6, z).tex((float)(par3 + par5) * f, (float)(par4 + par6) * f1).endVertex();
        tessellator.getBuffer().pos(par1 + par5, par2, z).tex((float)(par3 + par5) * f, (float)par4 * f1).endVertex();
        tessellator.getBuffer().pos(par1, par2, z).tex((float)par3 * f, (float)par4 * f1).endVertex();
        tessellator.draw();
    }
}
