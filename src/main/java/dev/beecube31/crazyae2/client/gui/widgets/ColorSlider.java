package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ColorSlider extends AbstractWidget {
    private static final int HANDLE_W = 8;
    private static final int HANDLE_H = 13;

    private final float minScroll;
    private final float maxScroll;
    private float currentScroll;
    private boolean dragging;
    private boolean disableColorizing;
    private final Supplier<ColorContainer> hue;
    private final Runnable onChange;

    public ColorSlider(int x, int y, int trackWidth, float min, float max, Supplier<ColorContainer> hue,
            Runnable onChange) {
        super(x, y, trackWidth, HANDLE_H, Component.empty());
        this.minScroll = min;
        this.maxScroll = max;
        this.currentScroll = min;
        this.hue = hue;
        this.onChange = onChange;
    }

    public ColorSlider disableColorizing(boolean v) {
        this.disableColorizing = v;
        return this;
    }

    public float getRange() {
        return this.maxScroll - this.minScroll;
    }

    public float getValue() {
        return this.currentScroll;
    }

    public int getIntValue() {
        return Math.round(this.currentScroll);
    }

    public ColorSlider setValue(float value) {
        this.currentScroll = Math.max(Math.min(value, this.maxScroll), this.minScroll);
        return this;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.getRange() == 0) {
            return;
        }
        this.dragging = true;
        this.updateFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (this.dragging) {
            this.updateFromMouse(mouseX);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.dragging = false;
    }

    private void updateFromMouse(double mouseX) {
        double relativeX = (mouseX - 4) - this.getX();
        float percentage = (float) (relativeX / (this.width - HANDLE_W));
        this.setValue(Math.round(this.minScroll + percentage * this.getRange()));
        this.onChange.run();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var blitter = StateSprite.SLIDER.getBlitter().copy();
        int offset = 0;
        if (this.getRange() == 0) {
            blitter.color(0.5F, 0.5F, 0.5F, 1F);
        } else {
            if (!this.disableColorizing) {
                ColorContainer h = this.hue.get();
                blitter.color(h.getRed(), h.getGreen(), h.getBlue(), 1F);
            }
            offset = Math.round((this.currentScroll - this.minScroll) * (this.width - HANDLE_W) / this.getRange());
        }
        blitter.dest(this.getX() + offset, this.getY()).blit(guiGraphics);

        if (this.dragging || (this.isHovered && this.getRange() > 0)) {
            var font = Minecraft.getInstance().font;
            String valText = String.valueOf(this.getIntValue());
            int padding = 2;
            int rectWidth = font.width(valText) + padding * 2;
            int rectHeight = 10;
            int tooltipX = mouseX + 4;
            int tooltipY = mouseY + 12;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 400);
            guiGraphics.fill(tooltipX, tooltipY, tooltipX + rectWidth, tooltipY + rectHeight, 0xB0000000);
            guiGraphics.drawString(font, valText, tooltipX + padding, tooltipY + 1, 0xFFFFFF, true);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.literal(String.valueOf(this.getIntValue())));
    }
}
