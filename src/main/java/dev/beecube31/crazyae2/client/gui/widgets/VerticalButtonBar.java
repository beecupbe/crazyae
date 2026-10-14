package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.Point;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyCompositeWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VerticalButtonBar implements ICrazyCompositeWidget {

    // Vertical space between buttons
    private static final int VERTICAL_SPACING = 4;

    // The margin between the right side of the buttons and the GUI
    private static final int MARGIN = 2;

    private final List<Button> buttons = new ArrayList<>();

    // The origin of the last initialized screen in window coordinates
    private Point screenOrigin = Point.ZERO;
    // This bounding rectangle relative to the screens origin
    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    private Point position;

    public VerticalButtonBar() {}

    public void add(Button button) {
        buttons.add(button);
    }

    @Override
    public ICrazyCompositeWidget setPosition(Point position) {
        this.position = position;

        return this;
    }

    @Override
    public ICrazyCompositeWidget setSize(int width, int height) {
        // Setting the size for this control is not supported

        return this;
    }

    @Override
    public Rect2i getBounds() {
        return bounds;
    }

    /**
     * We need to update every frame because buttons can become visible/invisible at any point in time.
     */
    @Override
    public void updateBeforeRender() {
        int currentY = position.getY() + MARGIN;
        int maxWidth = 0;

        // Align the button's right edge with the UI and account for margin
        for (Button button : buttons) {
            if (!button.visible) {
                continue;
            }

            // Vanilla widgets need to be in window space
            button.setX(screenOrigin.getX() + position.getX() - MARGIN - button.getWidth());
            button.setY(screenOrigin.getY() + currentY);

            currentY += button.getHeight() + VERTICAL_SPACING;
            maxWidth = Math.max(button.getWidth(), maxWidth);
        }

        // Set up a bounding rectangle for JEI exclusion zones
        if (maxWidth == 0) {
            bounds = new Rect2i(0, 0, 0, 0);
        } else {
            int boundX = position.getX() - maxWidth - 2 * MARGIN;
            int boundY = position.getY();
            bounds = new Rect2i(
                    boundX,
                    boundY,
                    maxWidth + 2 * MARGIN,
                    currentY - boundY);
        }
    }

    /**
     * Called when the parent UI is repositioned or resized. All buttons need to be re-added since Vanilla clears the
     * widgets when this happens.
     */
    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, CrazyAEBaseGui<?> screen) {
        this.screenOrigin = Point.fromTopLeft(bounds);
        for (var button : this.buttons) {
            if (button.isFocused()) {
                button.setFocused(false);
            }
            addWidget.accept(button);
        }
    }
}
