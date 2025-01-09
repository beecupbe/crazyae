package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.IGuiElementsCallbackHandler;
import dev.beecube31.crazyae2.common.interfaces.gui.IScrollSrc;
import net.minecraft.client.renderer.GlStateManager;

public class Slider implements IScrollSrc {

    private float maxScroll;
    private float minScroll;
    private float currentScroll;

    private final int displayX;
    private final int displayY;

    private final int maxDisplayX;

    private final int width = 8;
    private final int height = 13;

    private final ComponentHue hue;
    private final IGuiElementsCallbackHandler gui;

    public Slider(final int x, final int y, final int maxX, final float minScroll, final float maxScroll, final ComponentHue hue, final IGuiElementsCallbackHandler gui) {
        this.displayX = x;
        this.displayY = y;
        this.maxDisplayX = maxX;
        this.minScroll = minScroll;
        this.maxScroll = maxScroll;

        this.hue = hue;
        this.gui = gui;

        this.currentScroll = minScroll;
    }

    public float getRange() {
        return this.maxScroll - this.minScroll;
    }

    public void setRange(final float min, final float max) {
        this.minScroll = min;
        this.maxScroll = max;

        if (this.minScroll > this.maxScroll) {
            this.maxScroll = this.minScroll;
        }

        this.checkRange();
    }

    public void setScroll(final float value) {
        this.currentScroll = Math.max(Math.min(value, this.maxScroll), this.minScroll);
    }

    public void setScroll(final float value, boolean shouldUpdate) {
        this.currentScroll = Math.max(Math.min(value, this.maxScroll), this.minScroll);
        if (shouldUpdate)
            this.gui.onInteractionUpdate();
    }

    @Override
    public float getCurrentScroll() {
        return this.currentScroll;
    }

    @Override
    public void click(CrazyAEBaseGui aeBaseGui, int mouseX, int mouseY) {
        if (this.getRange() == 0) {
            return;
        }

        if (mouseX >= this.displayX && mouseX <= this.displayX + this.maxDisplayX &&
                mouseY >= this.displayY && mouseY <= this.displayY + this.height
        ) {
            this.gui.onInteractionUpdate();

            int relativeX = (mouseX - 4) - this.displayX;
            float percentage = (float) relativeX / (this.maxDisplayX - this.width);
            this.currentScroll = this.minScroll + percentage * this.getRange();

            this.checkRange();
        }
    }

    @Override
    public void onClickEnd(CrazyAEBaseGui aeBaseGui, int x, int y) {
        this.gui.onInteractionEnd();
    }

    private void checkRange() {
        this.currentScroll = Math.max(Math.min(this.currentScroll, this.maxScroll), this.minScroll);
    }

    @Override
    public void draw(CrazyAEBaseGui g) {
        g.bindTexture("crazyae", "guis/states.png");

        if (this.getRange() == 0) {
            GlStateManager.color(0.5f, 0.5f, 0.5f, 1.0f);
            g.drawTexturedModalRect(
                    this.displayX,
                    this.displayY,
                    StateSprite.SLIDER.getTextureX(),
                    StateSprite.SLIDER.getTextureY(),
                    StateSprite.SLIDER.getSizeX(),
                    StateSprite.SLIDER.getSizeY()
            );
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            this.hue.drawHue();
            int offset = Math.round((this.currentScroll - this.minScroll) * (this.maxDisplayX - this.width) / this.getRange());
            g.drawTexturedModalRect(
                    this.displayX + offset,
                    this.displayY,
                    StateSprite.SLIDER.getTextureX(),
                    StateSprite.SLIDER.getTextureY(),
                    StateSprite.SLIDER.getSizeX(),
                    StateSprite.SLIDER.getSizeY()
            );
            this.hue.endDrawHue();
        }
    }

    @Override
    public void wheel(int delta) {}
}
