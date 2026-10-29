package dev.beecube31.crazyae2.client.gui.widgets.renderonly;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * Invisible area that only shows a tooltip. Coordinates are absolute screen coordinates.
 */
public class TooltipArea implements Renderable, ITooltip {
    private final int x, y, w, h;
    private final Supplier<List<Component>> lines;

    public TooltipArea(int x, int y, int w, int h, Supplier<List<Component>> lines) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.lines = lines;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public List<Component> getTooltipMessage() {
        return lines.get();
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(x, y, w, h);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return true;
    }
}
