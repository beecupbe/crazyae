package dev.beecube31.crazyae2.client.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public class ColorContainer {
    public static final int DEFAULT_TEXT_COLOR = 4210752;
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public ColorContainer() {
        this(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public ColorContainer(int intColor) {
        this.red = (float)(intColor >> 16 & 255) / 255.0F;
        this.green = (float)(intColor >> 8 & 255) / 255.0F;
        this.blue = (float)(intColor & 255) / 255.0F;
        this.alpha = (float)(intColor >> 24 & 255) / 255.0F;
    }

    public ColorContainer(float red, float green, float blue, float alpha) {
        setParams(red, green, blue, alpha);
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
        this.red = Math.min(1.0f, red);
        this.green = Math.min(1.0f, green);
        this.blue = Math.min(1.0f, blue);
        this.alpha = Math.min(1.0f, alpha);
    }

    public int getIntColor() {
        int r = (int) (this.red * 255) & 255;
        int g = (int) (this.green * 255) & 255;
        int b = (int) (this.blue * 255) & 255;
        int a = (int) (this.alpha * 255) & 255;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void drawString(GuiGraphicsExtractor guiGraphics, Font font, String str, int x, int y) {
        guiGraphics.text(font, str, x, y, textColor(this.getIntColor()), false);
    }

    public void drawString(GuiGraphicsExtractor guiGraphics, Font font, Component component, int x, int y) {
        guiGraphics.text(font, component, x, y, textColor(this.getIntColor()), false);
    }

    public void drawCenteredString(GuiGraphicsExtractor guiGraphics, Font font, String text, int centerX, int y) {
        guiGraphics.centeredText(font, text, centerX, y, textColor(this.getIntColor()));
    }

    public void drawCenteredString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int centerX, int y) {
        guiGraphics.centeredText(font, text, centerX, y, textColor(this.getIntColor()));
    }

    public static int textColor(int color) {
        return (color & 0xFC000000) == 0 ? color | 0xFF000000 : color;
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

    public static void drawGradientRect(GuiGraphicsExtractor guiGraphics, int left, int top, int right, int bottom, int startColor, int endColor) {
        guiGraphics.fillGradient(left, top, right, bottom, startColor, endColor);
    }
}
