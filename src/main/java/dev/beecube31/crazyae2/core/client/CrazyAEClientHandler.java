package dev.beecube31.crazyae2.core.client;

import appeng.api.implementations.items.IItemGroup;
import appeng.util.Platform;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.client.gui.sprites.ISpriteProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.text.Collator;
import java.util.*;

@SideOnly(Side.CLIENT)
public class CrazyAEClientHandler {
    public static final String ICON_INDENTATION = getIconIndentation();

    private static String getIconIndentation() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < CrazyAEClientConfig.getTooltipsIndentationSize(); i++) {
            s.append(" ");
        }
        return s.toString();
    }

    public static Collator getLocaleCollator() {
        Locale javaLocale = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale();
        return Collator.getInstance(javaLocale);
    }

    public static String getUpgradeLocalizedName(ItemStack stack, int limit, Set<ItemStack> groupContextKeys) {
        String name = null;
        Item itemFromStack = stack.getItem();

        if (itemFromStack instanceof IItemGroup ig) {
            Set<ItemStack> context = (groupContextKeys != null && !groupContextKeys.isEmpty()) ? groupContextKeys : Collections.singleton(stack);
            String str = ig.getUnlocalizedGroupName(context, stack);
            if (str != null) {
                name = Platform.gui_localize(str) + (limit > 1 ? " (" + limit + ')' : "");
            }
        }

        if (name == null) {
            name = stack.getDisplayName() + (limit > 1 ? " (" + limit + ')' : "");
        }
        return name;
    }

    public static List<Map.Entry<ItemStack, Integer>> getSortedItemEntries(Map<ItemStack, Integer> entriesMap, Set<ItemStack> groupContextKeys) {
        List<Map.Entry<ItemStack, Integer>> sortedList = new ArrayList<>(entriesMap.entrySet());
        Collator localeCollator = getLocaleCollator();

        if (localeCollator != null) {
            sortedList.sort((e1, e2) -> {
                String name1 = getUpgradeLocalizedName(e1.getKey(), e1.getValue(), groupContextKeys);
                String name2 = getUpgradeLocalizedName(e2.getKey(), e2.getValue(), groupContextKeys);
                return localeCollator.compare(name1, name2);
            });
        }
        return sortedList;
    }

    public static void renderItemIntoGUI(ItemStack stack, int x, int y, float scale) {
        Preconditions.checkNotNull(stack);

        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = mc.getRenderItem();

        GlStateManager.pushMatrix();

        GlStateManager.translate((float)x, (float)y, 100.0F);
        GlStateManager.scale(scale, scale, scale);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        IBakedModel model = itemRenderer.getItemModelWithOverrides(stack, null, null);

        itemRenderer.renderItem(stack, model);

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public static void drawItemIntoTooltip(ItemStack stack, int finalIconDrawX, int finalIconDrawY) {
        if (stack.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = mc.getRenderItem();
        float scale = 9.0F / 16.0F;
        int yOffset = 0;


        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float)finalIconDrawX, (float)finalIconDrawY + yOffset, 350.0F);
        GlStateManager.scale(scale, scale, 1.0F);

        float originalZLevel = itemRenderer.zLevel;
        itemRenderer.zLevel = 200.0F;
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.renderItemAndEffectIntoGUI(mc.player, stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        itemRenderer.zLevel = originalZLevel;

        GlStateManager.popMatrix();
    }

    public static void drawSpriteIntoTooltip(ISpriteProvider sprite, int finalIconDrawX, int finalIconDrawY) {
        if (sprite == null || sprite.getTexture() == null) return;

        int iconHeightInText = 9;
        int yOffset = 0;

        float scaleFactor = (sprite.getSizeY() > 0) ? (float) iconHeightInText / sprite.getSizeY() : 1.0f;

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float)finalIconDrawX, (float)finalIconDrawY + yOffset, 350.0F);
        GlStateManager.scale(scaleFactor, scaleFactor, 1.0f);

        GlStateManager.disableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        drawSprite(sprite.getTexture(), 0, 0, sprite.getSizeX(), sprite.getSizeY());

        GlStateManager.popMatrix();
    }

    public static int getCorrectTextLineY(int effectiveLineIndex, int initialTooltipY, int actualTitleLines, int totalEffectiveLines) {
        int currentY = initialTooltipY;
        int lineHeight = 10;

        for (int l = 0; l < effectiveLineIndex; l++) {
            currentY += lineHeight;
            if (l + 1 == actualTitleLines && totalEffectiveLines > actualTitleLines) {
                currentY += 2;
            }
        }
        return currentY;
    }

    public static void drawSprite(
            ResourceLocation tex,
            int x,
            int y,
            int width,
            int height
    ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0)
                .tex(0.0F, 1.0F)
                .endVertex();
        bufferbuilder.pos(x + width, y + height, 0)
                .tex(1.0F, 1.0F)
                .endVertex();
        bufferbuilder.pos(x + width, y, 0)
                .tex(1.0F, 0.0F)
                .endVertex();
        bufferbuilder.pos(x, y, 0)
                .tex(0.0F, 0.0F)
                .endVertex();

        tessellator.draw();
    }

    public static TooltipLayout drawTooltipAndGetComponents(@Nonnull final ItemStack stack, List<String> initialTextLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (initialTextLines == null || initialTextLines.isEmpty()) {
            return new TooltipLayout(new int[]{0,0,0,0,0}, Collections.emptyList(), Collections.emptyMap());
        }

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        List<String> effectiveLines = new ArrayList<>(initialTextLines);
        Map<Integer, Integer> logicalToEffectiveMap = new HashMap<>();

        int tooltipTextWidth = 0;
        for (String textLine : effectiveLines) {
            int textLineWidth = font.getStringWidth(textLine);
            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        boolean needsWrap = false;
        int titleActualLinesCount = initialTextLines.isEmpty() ? 0 : 1;

        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
                if (mouseX > screenWidth / 2) {
                    tooltipTextWidth = mouseX - 12 - 8;
                } else {
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                }
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (tooltipTextWidth < 0) tooltipTextWidth = 0;

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<>();
            int currentEffectiveIndex = 0;
            for (int i = 0; i < initialTextLines.size(); i++) {
                logicalToEffectiveMap.put(i, currentEffectiveIndex);
                String textLine = initialTextLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                if (i == 0) {
                    titleActualLinesCount = wrappedLine.size();
                }
                for (String line : wrappedLine) {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth) {
                        wrappedTooltipWidth = lineWidth;
                    }
                    wrappedTextLines.add(line);
                    currentEffectiveIndex++;
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            effectiveLines = wrappedTextLines;

            if (mouseX > screenWidth / 2) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
            } else {
                tooltipX = mouseX + 12;
            }

            if (tooltipX < 4) {
                tooltipX = 4;
            }

        } else {
            for (int i = 0; i < initialTextLines.size(); i++) {
                logicalToEffectiveMap.put(i, i);
            }
            if (initialTextLines.isEmpty()) titleActualLinesCount = 0;
        }

        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (!effectiveLines.isEmpty()) {
            tooltipHeight += (effectiveLines.size() - 1) * 10;
            if (effectiveLines.size() > titleActualLinesCount && titleActualLinesCount > 0) {
                tooltipHeight += 2;
            }
        } else {
            tooltipHeight = 0;
        }


        if (tooltipY < 4) {
            tooltipY = 4;
        } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - 4;
        }

        if (effectiveLines.isEmpty() && tooltipTextWidth == 0) {
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            return new TooltipLayout(new int[]{tooltipX, tooltipY, tooltipTextWidth, tooltipHeight, titleActualLinesCount},
                    effectiveLines, logicalToEffectiveMap);
        }

        final int zLevel = 300;
        int backgroundColor = 0xF0100010;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;

        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, effectiveLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
        MinecraftForge.EVENT_BUS.post(colorEvent);
        backgroundColor = colorEvent.getBackground();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();

        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);

        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, effectiveLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

        int currentTextY = tooltipY;
        for (int lineNumber = 0; lineNumber < effectiveLines.size(); ++lineNumber) {
            String line = effectiveLines.get(lineNumber);
            font.drawStringWithShadow(line, (float) tooltipX, (float) currentTextY, -1);

            if (lineNumber + 1 == titleActualLinesCount && effectiveLines.size() > titleActualLinesCount) {
                currentTextY += 2;
            }
            currentTextY += 10;
        }

        MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, effectiveLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        return new TooltipLayout(new int[]{tooltipX, tooltipY, tooltipTextWidth, tooltipHeight, titleActualLinesCount},
                effectiveLines, logicalToEffectiveMap);
    }

    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.pos(right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static class TooltipLayout {
        public final int[] posArray;
        public final List<String> effectiveLines;
        public final Map<Integer, Integer> logicalToEffectiveLineMap;


        public TooltipLayout(int[] posArray, List<String> effectiveLines, Map<Integer, Integer> logicalToEffectiveLineMap) {
            this.posArray = posArray;
            this.effectiveLines = effectiveLines;
            this.logicalToEffectiveLineMap = logicalToEffectiveLineMap;
        }
    }
}
