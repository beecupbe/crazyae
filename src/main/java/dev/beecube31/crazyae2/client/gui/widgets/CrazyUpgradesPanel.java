package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.Upgrades;
import appeng.client.Point;
import appeng.client.gui.Rects;
import appeng.client.gui.Tooltip;
import appeng.client.gui.style.Blitter;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyCompositeWidget;
import dev.beecube31.crazyae2.common.containers.slots.CrazyAESlot;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CrazyUpgradesPanel implements ICrazyCompositeWidget {

    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 7;
    private static final int MAX_ROWS = 8;

    private static Blitter BACKGROUND;
    private static Blitter INNER_CORNER;

    private final List<Slot> slots;

    // The screen origin in window space (used to layout slots)
    private Point screenOrigin = Point.ZERO;

    // Relative to current screen origin (not window)
    private int x;
    private int y;

    private final Supplier<List<Component>> tooltipSupplier;

    public CrazyUpgradesPanel(List<Slot> slots, ColorContainer colorContainer) {
        this(slots, Collections::emptyList, colorContainer);
    }

    public CrazyUpgradesPanel(List<Slot> slots, IUpgradeableObject upgradeableObject, ColorContainer colorContainer) {
        this(slots, () -> Upgrades.getTooltipLinesForMachine(upgradeableObject.getUpgrades().getUpgradableItem()), colorContainer);
    }

    public CrazyUpgradesPanel(List<Slot> slots, Supplier<List<Component>> tooltipSupplier, ColorContainer colorContainer) {
        this.slots = slots;
        this.tooltipSupplier = tooltipSupplier;
        BACKGROUND = Blitter.texture(new ResourceLocation(CrazyAE.MODID, "textures/guis/upgrades.png"), 128, 128)
                .color(colorContainer.getRed(), colorContainer.getGreen(), colorContainer.getBlue(), colorContainer.getAlpha());
        BACKGROUND.copy().src(12, 33, SLOT_SIZE, SLOT_SIZE);
    }

    @Override
    public ICrazyCompositeWidget setPosition(Point position) {
        x = position.getX();
        y = position.getY();

        return this;
    }

    /**
     * Changes where the panel is positioned. Coordinates are relative to the current screen's origin.
     */
    @Override
    public ICrazyCompositeWidget setSize(int width, int height) {
        // Size of upgrades panel cannot be set via JSON

        return this;
    }

    /**
     * The overall bounding box in screen coordinates.
     */
    @Override
    public Rect2i getBounds() {
        int slotCount = getUpgradeSlotCount();

        int height = 2 * PADDING + Math.min(MAX_ROWS, slotCount) * SLOT_SIZE;
        int width = 2 * PADDING + (slotCount + MAX_ROWS - 1) / MAX_ROWS * SLOT_SIZE;
        return new Rect2i(x, y, width, height);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, CrazyAEBaseGui<?> screen) {
        this.screenOrigin = Point.fromTopLeft(bounds);
    }

    @Override
    public void updateBeforeRender() {
        int slotOriginX = this.x + PADDING;
        int slotOriginY = this.y + PADDING;

        for (Slot slot : slots) {
            if (!slot.isActive()) {
                continue;
            }

            slot.x = slotOriginX + 1;
            slot.y = slotOriginY + 1;
            slotOriginY += SLOT_SIZE;
        }
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        int slotCount = getUpgradeSlotCount();
        if (slotCount <= 0) {
            return;
        }

        // This is the absolute x,y coord of the first slot within the panel
        int slotOriginX = screenOrigin.getX() + this.x + PADDING;
        int slotOriginY = screenOrigin.getY() + this.y + PADDING;

        for (int i = 0; i < slotCount; i++) {
            // Unlike other UIs, this is drawn top-to-bottom,left-to-right
            int row = i % MAX_ROWS;
            int col = i / MAX_ROWS;

            int x = slotOriginX + col * SLOT_SIZE;
            int y = slotOriginY + row * SLOT_SIZE;

            boolean borderLeft = col == 0;
            boolean borderTop = row == 0;
            // The panel can have a "jagged" edge if the number of slots is not divisible by MAX_ROWS
            boolean lastSlot = i + 1 >= slotCount;
            boolean lastRow = row + 1 >= MAX_ROWS;
            boolean borderBottom = lastRow || lastSlot;
            boolean borderRight = i >= slotCount - MAX_ROWS;

            drawSlot(guiGraphics, x, y, borderLeft, borderTop, borderRight, borderBottom);

            // Cover up the inner corner when we just drew a rather ugly "inner corner"
            if (col > 0 && lastSlot && !lastRow) {
                INNER_CORNER.dest(x, y + SLOT_SIZE).blit(guiGraphics);
            }
        }
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        int offsetX = screenBounds.getX();
        int offsetY = screenBounds.getY();

        int slotCount = getUpgradeSlotCount();

        // Use a bit of a margin around the zone to avoid things looking too cramped
        final int margin = 2;

        // Add a single bounding rectangle for as many columns as are fully populated
        int fullCols = slotCount / MAX_ROWS;
        int rightEdge = offsetX + x;
        if (fullCols > 0) {
            int fullColWidth = PADDING * 2 + fullCols * SLOT_SIZE;
            exclusionZones.add(Rects.expand(new Rect2i(
                    rightEdge,
                    offsetY + y,
                    fullColWidth,
                    PADDING * 2 + MAX_ROWS * SLOT_SIZE), margin));
            rightEdge += fullColWidth;
        }

        // If there's a partially populated row at the end, add a smaller rectangle for it
        int remaining = slotCount - fullCols * MAX_ROWS;
        if (remaining > 0) {
            exclusionZones.add(Rects.expand(new Rect2i(
                    rightEdge,
                    offsetY + y,
                    // We need to add padding in case there's no full column that already includes it
                    SLOT_SIZE + (fullCols > 0 ? 0 : PADDING * 2),
                    PADDING * 2 + remaining * SLOT_SIZE), margin));
        }

    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        if (getUpgradeSlotCount() == 0) {
            return null;
        }

        List<Component> tooltip = this.tooltipSupplier.get();
        if (tooltip.isEmpty()) {
            return null;
        }

        return new Tooltip(tooltip);
    }

    private static void drawSlot(GuiGraphics guiGraphics, int x, int y,
            boolean borderLeft, boolean borderTop, boolean borderRight, boolean borderBottom) {
        int srcX = PADDING;
        int srcY = PADDING;
        int srcWidth = SLOT_SIZE;
        int srcHeight = SLOT_SIZE;

        if (borderLeft) {
            x -= PADDING;
            srcX = 0;
            srcWidth += PADDING;
        }
        if (borderRight) {
            srcWidth += PADDING;
        }
        if (borderTop) {
            y -= PADDING;
            srcY = 0;
            srcHeight += PADDING;
        }
        if (borderBottom) {
            srcHeight += PADDING;
        }

        BACKGROUND.src(srcX, srcY, srcWidth, srcHeight)
                .dest(x, y)
                .blit(guiGraphics);
    }

    /**
     * We need this function since the cell workbench can dynamically change how many upgrade slots are active based on
     * the cell in the workbench.
     */
    private int getUpgradeSlotCount() {
        int count = 0;
        for (Slot slot : slots) {
            if (slot instanceof CrazyAESlot && ((CrazyAESlot) slot).isSlotEnabled()) {
                count++;
            }
        }
        return count;
    }

}
