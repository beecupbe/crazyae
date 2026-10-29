package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import appeng.core.localization.GuiText;
import appeng.menu.interfaces.IProgressProvider;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEProgressProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrazyProgressBar extends AbstractWidget implements ITooltip {

    private final ICrazyAEProgressProvider source;
    private final Blitter blitter;
    private final Direction layout;
    private final Rect2i sourceRect;
    private final Component titleName;
    private Component fullMsg;

    public CrazyProgressBar(ICrazyAEProgressProvider source, Blitter blitter, Direction dir, ColorContainer container) {
        this(source, blitter, dir, null, container);
    }

    public CrazyProgressBar(ICrazyAEProgressProvider source, Blitter blitter,
                            Direction dir, Component title, ColorContainer container) {
        super(0, 0, blitter.getSrcWidth(), blitter.getSrcHeight(), Component.empty());
        this.source = source;
        this.blitter = blitter.copy().color(container.getRed(), container.getGreen(), container.getBlue(), container.getAlpha());
        this.layout = dir;
        this.titleName = title;
        this.sourceRect = new Rect2i(
                blitter.getSrcX(),
                blitter.getSrcY(),
                blitter.getSrcWidth(),
                blitter.getSrcHeight());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            double max = this.source.getMaxProgress();
            double current = this.source.getCurrentProgress();

            if (current > max) {
                current = max;
            }

            double srcX = sourceRect.getX();
            double srcY = sourceRect.getY();
            double srcW = sourceRect.getWidth();
            double srcH = sourceRect.getHeight();
            double destX = getX();
            double destY = getY();

            if (this.layout == Direction.VERTICAL) {
                double diff = this.height - (max > 0 ? this.height * current / max : 0);
                destY += diff;
                srcY += diff;
                srcH -= diff;
            } else {
                double diff = this.width - (max > 0 ? this.width * current / max : 0);
                srcX += diff;
                srcW -= diff;
            }

            blitter.src((int) Math.min(srcX, Integer.MAX_VALUE), (int) Math.min(srcY, Integer.MAX_VALUE), (int) Math.min(srcW, Integer.MAX_VALUE), (int) Math.min(srcH, Integer.MAX_VALUE))
                    .dest((int) Math.min(destX, Integer.MAX_VALUE), (int) Math.min(destY, Integer.MAX_VALUE))
                    .blit(guiGraphics);
        }
    }

    public void setFullMsg(Component msg) {
        this.fullMsg = msg;
    }

    @Override
    public List<Component> getTooltipMessage() {
        if (this.fullMsg != null) {
            return Collections.singletonList(this.fullMsg);
        }

        Component result = this.titleName != null ? this.titleName : Component.empty();
        return Arrays.asList(
                result,
                Component.literal(this.source.getCurrentProgress() + " ")
                        .append(GuiText.Of.text().copy().append(" " + this.source.getMaxProgress())));
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX() - 2, getY() - 2, width + 4, height + 4);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return true;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {}

    public enum Direction {
        HORIZONTAL, VERTICAL
    }
}
