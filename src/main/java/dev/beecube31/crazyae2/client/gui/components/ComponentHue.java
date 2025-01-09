package dev.beecube31.crazyae2.client.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

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
}
