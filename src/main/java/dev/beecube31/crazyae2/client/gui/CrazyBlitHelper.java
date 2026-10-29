package dev.beecube31.crazyae2.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class CrazyBlitHelper {
    private CrazyBlitHelper() {}

    public static void blitNineSliced(GuiGraphics g, ResourceLocation tex, int x, int y, int width, int height,
                                      int sliceWidth, int sliceHeight, int uWidth, int vHeight, int u, int v) {
        blitNineSliced(g, tex, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, u, v);
    }

    public static void blitNineSliced(GuiGraphics g, ResourceLocation tex, int x, int y, int width, int height,
                                      int left, int top, int right, int bottom,
                                      int uWidth, int vHeight, int u, int v) {
        left = Math.min(left, width / 2);
        right = Math.min(right, width / 2);
        top = Math.min(top, height / 2);
        bottom = Math.min(bottom, height / 2);

        if (width == uWidth && height == vHeight) {
            g.blit(tex, x, y, u, v, width, height);
        } else if (height == vHeight) {
            g.blit(tex, x, y, u, v, left, height);
            blitRepeating(g, tex, x + left, y, width - right - left, height, u + left, v, uWidth - right - left, vHeight);
            g.blit(tex, x + width - right, y, u + uWidth - right, v, right, height);
        } else if (width == uWidth) {
            g.blit(tex, x, y, u, v, width, top);
            blitRepeating(g, tex, x, y + top, width, height - bottom - top, u, v + top, uWidth, vHeight - bottom - top);
            g.blit(tex, x, y + height - bottom, u, v + vHeight - bottom, width, bottom);
        } else {
            g.blit(tex, x, y, u, v, left, top);
            blitRepeating(g, tex, x + left, y, width - right - left, top, u + left, v, uWidth - right - left, top);
            g.blit(tex, x + width - right, y, u + uWidth - right, v, right, top);
            g.blit(tex, x, y + height - bottom, u, v + vHeight - bottom, left, bottom);
            blitRepeating(g, tex, x + left, y + height - bottom, width - right - left, bottom,
                    u + left, v + vHeight - bottom, uWidth - right - left, bottom);
            g.blit(tex, x + width - right, y + height - bottom, u + uWidth - right, v + vHeight - bottom, right, bottom);
            blitRepeating(g, tex, x, y + top, left, height - bottom - top, u, v + top, left, vHeight - bottom - top);
            blitRepeating(g, tex, x + left, y + top, width - right - left, height - bottom - top,
                    u + left, v + top, uWidth - right - left, vHeight - bottom - top);
            blitRepeating(g, tex, x + width - right, y + top, left, height - bottom - top,
                    u + uWidth - right, v + top, right, vHeight - bottom - top);
        }
    }

    private static void blitRepeating(GuiGraphics g, ResourceLocation tex, int x, int y, int width, int height,
                                      int u, int v, int srcWidth, int srcHeight) {
        if (width <= 0 || height <= 0 || srcWidth <= 0 || srcHeight <= 0) {
            return;
        }
        int curX = x;
        for (int sliceW : slices(width, srcWidth)) {
            int uOff = (srcWidth - sliceW) / 2;
            int curY = y;
            for (int sliceH : slices(height, srcHeight)) {
                int vOff = (srcHeight - sliceH) / 2;
                g.blit(tex, curX, curY, u + uOff, v + vOff, sliceW, sliceH);
                curY += sliceH;
            }
            curX += sliceW;
        }
    }

    private static int[] slices(int target, int total) {
        int count = Mth.positiveCeilDiv(target, total);
        int[] result = new int[count];
        int quotient = target / count;
        int remainder = target % count;
        for (int i = 0; i < count; i++) {
            result[i] = quotient + (i < remainder ? 1 : 0);
        }
        return result;
    }
}
