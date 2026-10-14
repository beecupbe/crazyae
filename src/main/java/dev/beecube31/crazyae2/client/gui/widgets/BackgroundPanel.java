package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.style.Blitter;
import dev.beecube31.crazyae2.client.gui.ICrazyCompositeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

public class BackgroundPanel implements ICrazyCompositeWidget {
    private final Blitter background;

    // Relative to current screen origin (not window)
    private int x;
    private int y;

    public BackgroundPanel(Blitter background) {
        this.background = background;
    }

    @Override
    public ICrazyCompositeWidget setPosition(Point position) {
        x = position.getX();
        y = position.getY();

        return this;
    }

    @Override
    public ICrazyCompositeWidget setSize(int width, int height) {
        // Size of panels is implied by the background
        return this;
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x, y, background.getSrcWidth(), background.getSrcHeight());
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        background.dest(bounds.getX() + x, bounds.getY() + y).blit(guiGraphics);
    }
}
