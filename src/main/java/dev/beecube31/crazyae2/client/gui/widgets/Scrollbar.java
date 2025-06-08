package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.common.interfaces.gui.IGuiElementsCallbackHandler;
import dev.beecube31.crazyae2.common.interfaces.gui.IScrollSrc;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.Nullable;

public class Scrollbar implements IScrollSrc {
    private int displayX = 0;
    private int displayY = 0;
    private int width = 12;
    private int height = 16;
    private int pageSize = 1;

    private int maxScroll = 0;
    private int minScroll = 0;
    private int currentScroll = 0;

    private final ComponentHue hue;
    private final IGuiElementsCallbackHandler gui;

    public Scrollbar(final ComponentHue hue, @Nullable final IGuiElementsCallbackHandler gui) {
        this.hue = hue;
        this.gui = gui;
    }

    @Override
    public void draw(final CrazyAEBaseGui g) {
        g.bindTexture("minecraft", "gui/container/creative_inventory/tabs.png");
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (this.getRange() == 0) {
            g.drawTexturedModalRect(this.displayX, this.displayY, 232 + this.width, 0, this.width, 15);
        } else {
            this.hue.drawHue();
            final int offset = (this.currentScroll - this.minScroll) * (this.height - 15) / this.getRange();
            g.drawTexturedModalRect(this.displayX, offset + this.displayY, 232, 0, this.width, 15);
            this.hue.endDrawHue();
        }
    }

    private int getRange() {
        return this.maxScroll - this.minScroll;
    }

    public int getLeft() {
        return this.displayX;
    }

    public Scrollbar setLeft(final int v) {
        this.displayX = v;
        return this;
    }

    public int getTop() {
        return this.displayY;
    }

    public Scrollbar setTop(final int v) {
        this.displayY = v;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public Scrollbar setWidth(final int v) {
        this.width = v;
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    public Scrollbar setHeight(final int v) {
        this.height = v;
        return this;
    }

    public void setRange(final int min, final int max, final int pageSize) {
        this.minScroll = min;
        this.maxScroll = max;
        this.pageSize = pageSize;

        if (this.minScroll > this.maxScroll) {
            this.maxScroll = this.minScroll;
        }

        this.applyRange();
    }

    private void applyRange() {
        this.currentScroll = Math.max(Math.min(this.currentScroll, this.maxScroll), this.minScroll);
    }

    @Override
    public float getCurrentScroll() {
        return this.currentScroll;
    }

    @Override
    public void click(final CrazyAEBaseGui aeBaseGui, final int x, final int y) {
        if (this.getRange() == 0) {
            return;
        }

        if (this.gui != null) {
            this.gui.onInteractionUpdate();
        }

        if (x > this.displayX && x <= this.displayX + this.width) {
            if (y > this.displayY && y <= this.displayY + this.height) {
                this.currentScroll = (y - this.displayY);
                this.currentScroll = this.minScroll + ((this.currentScroll * 2 * this.getRange() / this.height));
                this.currentScroll = (this.currentScroll + 1) >> 1;
                this.applyRange();
            }
        }
    }

    @Override
    public void onClickEnd(CrazyAEBaseGui aeBaseGui, int x, int y) {
        if (this.gui != null) {
            this.gui.onInteractionEnd();
        }
    }

    @Override
    public void wheel(int delta) {
        delta = Math.max(Math.min(-delta, 1), -1);
        this.currentScroll += delta * this.pageSize;
        this.applyRange();
    }
}
