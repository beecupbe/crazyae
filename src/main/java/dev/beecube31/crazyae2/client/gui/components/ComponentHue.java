package dev.beecube31.crazyae2.client.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@SuppressWarnings("unused")
public class ComponentHue {
    public static final int DEFAULT_TEXT_COLOR = 4210752;
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public ComponentHue() {
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 1.0F;
    }

    public ComponentHue(int intColor) {
        this.red = (float)(intColor >> 16 & 255) / 255.0F;
        this.blue = (float)(intColor >> 8 & 255) / 255.0F;
        this.green = (float)(intColor & 255) / 255.0F;
        this.alpha = (float)(intColor >> 24 & 255) / 255.0F;
    }

    public ComponentHue(float red, float green, float blue, float alpha) {
        if (red > 1.0) return;
        if (green > 1.0) return;
        if (blue > 1.0) return;
        if (alpha > 1.0) return;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void drawHue() {
        GlStateManager.color(this.red, this.green, this.blue, this.alpha);
    }

    public void drawHue(float red, float green, float blue, float alpha) {
        GlStateManager.color(red, green, blue, alpha);
    }

    public void endDrawHue() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void setRed(float red) {
        if (red > 1.0) return;
        this.red = red;
    }

    public void setGreen(float green) {
        if (green > 1.0) return;
        this.green = green;
    }

    public void setBlue(float blue) {
        if (blue > 1.0) return;
        this.blue = blue;
    }

    public void setAlpha(float alpha) {
        if (alpha > 1.0) return;
        this.alpha = alpha;
    }

    public void setParams(float red, float green, float blue, float alpha) {
        if (red > 1.0) return;
        if (green > 1.0) return;
        if (blue > 1.0) return;
        if (alpha > 1.0) return;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public int getIntColor() {
        int r = (int) (this.red * 255) & 255;
        int g = (int) (this.green * 255) & 255;
        int b = (int) (this.blue * 255) & 255;
        int a = (int) (this.alpha * 255) & 255;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void drawString(String str, int x, int y, FontRenderer renderer) {
        renderer.drawString(str, x, y, this.getIntColor());
    }

    public void drawCenteredText(String text, int centerX, int centerY, int color, FontRenderer renderer) {
        int textWidth = renderer.getStringWidth(text);
        int drawX = centerX - textWidth / 2;
        renderer.drawString(text, drawX, centerY, color);
    }

    public void drawCenteredText(String text, int centerX, int centerY, FontRenderer renderer) {
        int textWidth = renderer.getStringWidth(text);
        int drawX = centerX - textWidth / 2;
        renderer.drawString(text, drawX, centerY, this.getIntColor());
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public static void drawGradient(int x1, int y1, int x2, int y2, int colorLeft, int colorMid, int colorRight) {
        int midX = x1 + (x2 - x1) / 2;
        drawGradientRect(x1, y1, midX, y2, colorLeft, colorMid);
        drawGradientRect(midX, y1, x2, y2, colorMid, colorRight);
    }

    protected static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
