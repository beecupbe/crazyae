package dev.beecube31.crazyae2.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class CrazyBlitHelper {
    private CrazyBlitHelper() {}

    public static void blitNineSliced(GuiGraphicsExtractor g, Identifier tex, int x, int y, int width, int height,
                                      int sliceWidth, int sliceHeight, int uWidth, int vHeight, int u, int v) {
        blitNineSliced(g, tex, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, u, v, -1);
    }

    public static void blitNineSliced(GuiGraphicsExtractor g, Identifier tex, int x, int y, int width, int height,
                                      int sliceWidth, int sliceHeight, int uWidth, int vHeight, int u, int v, int color) {
        blitNineSliced(g, tex, x, y, width, height, sliceWidth, sliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, u, v, color);
    }

    public static void blitNineSliced(GuiGraphicsExtractor g, Identifier tex, int x, int y, int width, int height,
                                      int left, int top, int right, int bottom,
                                      int uWidth, int vHeight, int u, int v, int color) {
        left = Math.min(left, width / 2);
        right = Math.min(right, width / 2);
        top = Math.min(top, height / 2);
        bottom = Math.min(bottom, height / 2);

        if (width == uWidth && height == vHeight) {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, width, height, 256, 256, color);
        } else if (height == vHeight) {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, left, height, 256, 256, color);
            blitRepeating(g, tex, x + left, y, width - right - left, height, u + left, v, uWidth - right - left, vHeight, color);
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x + width - right, y, u + uWidth - right, v, right, height, 256, 256, color);
        } else if (width == uWidth) {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, width, top, 256, 256, color);
            blitRepeating(g, tex, x, y + top, width, height - bottom - top, u, v + top, uWidth, vHeight - bottom - top, color);
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y + height - bottom, u, v + vHeight - bottom, width, bottom, 256, 256, color);
        } else {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, left, top, 256, 256, color);
            blitRepeating(g, tex, x + left, y, width - right - left, top, u + left, v, uWidth - right - left, top, color);
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x + width - right, y, u + uWidth - right, v, right, top, 256, 256, color);
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y + height - bottom, u, v + vHeight - bottom, left, bottom, 256, 256, color);
            blitRepeating(g, tex, x + left, y + height - bottom, width - right - left, bottom,
                    u + left, v + vHeight - bottom, uWidth - right - left, bottom, color);
            g.blit(RenderPipelines.GUI_TEXTURED, tex, x + width - right, y + height - bottom, u + uWidth - right, v + vHeight - bottom, right, bottom, 256, 256, color);
            blitRepeating(g, tex, x, y + top, left, height - bottom - top, u, v + top, left, vHeight - bottom - top, color);
            blitRepeating(g, tex, x + left, y + top, width - right - left, height - bottom - top,
                    u + left, v + top, uWidth - right - left, vHeight - bottom - top, color);
            blitRepeating(g, tex, x + width - right, y + top, left, height - bottom - top,
                    u + uWidth - right, v + top, right, vHeight - bottom - top, color);
        }
    }

    private static void blitRepeating(GuiGraphicsExtractor g, Identifier tex, int x, int y, int width, int height,
                                      int u, int v, int srcWidth, int srcHeight, int color) {
        if (width <= 0 || height <= 0 || srcWidth <= 0 || srcHeight <= 0) {
            return;
        }
        int curX = x;
        for (int sliceW : slices(width, srcWidth)) {
            int uOff = (srcWidth - sliceW) / 2;
            int curY = y;
            for (int sliceH : slices(height, srcHeight)) {
                int vOff = (srcHeight - sliceH) / 2;
                g.blit(RenderPipelines.GUI_TEXTURED, tex, curX, curY, u + uOff, v + vOff, sliceW, sliceH, 256, 256, color);
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
