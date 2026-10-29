package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.widgets.IResizableWidget;
import appeng.client.gui.widgets.ITooltip;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class CrazyTextField extends EditBox implements IResizableWidget, ITooltip {
    public static final Identifier TEXTURE = CrazyAE.makeId("textures/guis/widgets/buttons.png");

    private static final int TEX_U = 0;
    private static final int TEX_V = 106;
    private static final int TEX_WIDTH = 128;
    private static final int TEX_STATE_HEIGHT = 12;

    private static final int PADDING = 2;
    private static final int DEFAULT_SELECTION_COLOR = 0xFF000080;

    private int fontPad;
    private final Supplier<ColorContainer> hue;

    private boolean drawBackground = true;
    private int selectionColor = DEFAULT_SELECTION_COLOR;
    @Nullable
    private Integer textColorOverride;
    @Nullable
    private Component placeholder;
    private List<Component> tooltipMessage = Collections.emptyList();
    @Nullable
    private Runnable onConfirm;

    public CrazyTextField(Font font, int x, int y, int width, int height,
                          Supplier<ColorContainer> hue) {
        super(font, x + PADDING, y + PADDING,
                Math.max(0, width - 2 * PADDING - font.width("_")), Math.max(0, height - 2 * PADDING),
                Component.empty());
        this.fontPad = font.width("_");
        this.hue = hue;
        this.setBordered(false);
    }

    public CrazyTextField(Font font, int x, int y, int width, int height, ColorContainer hue) {
        this(font, x, y, width, height, () -> hue);
    }

    public CrazyTextField setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
        return this;
    }

    public CrazyTextField setReserveCursorSpace(boolean reserve) {
        int newPad = reserve ? Minecraft.getInstance().font.width("_") : 0;
        super.setWidth(Math.max(0, this.width + this.fontPad - newPad));
        this.fontPad = newPad;
        return this;
    }

    public void setSelectionColor(int color) {
        this.selectionColor = color;
    }

    public void setTextColorOverride(@Nullable Integer color) {
        this.textColorOverride = color;
    }

    public void setPlaceholder(@Nullable Component placeholder) {
        this.placeholder = placeholder;
    }

    @Nullable
    public Component getPlaceholder() {
        return placeholder;
    }

    public void setOnConfirm(@Nullable Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void setTooltipMessage(List<Component> tooltipMessage) {
        this.tooltipMessage = Objects.requireNonNull(tooltipMessage);
    }

    public void selectAll() {
        this.moveCursorToEnd(false);
        this.setHighlightPos(0);
    }

    private ColorContainer currentHue() {
        var c = hue != null ? hue.get() : null;
        return c != null ? c : new ColorContainer();
    }

    private int currentTextColor() {
        if (textColorOverride != null) {
            return 0xFF000000 | textColorOverride;
        }

        int rgb = 0xFFFFFF;
        return 0xFF000000 | rgb;
    }

    private static int muted(int argb) {
        int r = ((argb >> 16 & 255) + 0x80) / 2;
        int g = ((argb >> 8 & 255) + 0x80) / 2;
        int b = ((argb & 255) + 0x80) / 2;
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    @Override
    public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (!this.isVisible()) {
            return;
        }

        if (drawBackground) {
            int yOffset = 0;
            if (!this.isEditable()) {
                yOffset = TEX_STATE_HEIGHT;
            } else if (isFocused()) {
                yOffset = TEX_STATE_HEIGHT * 2;
            }

            var bounds = getVisualBounds();
            var h = currentHue();
            int v = TEX_V + yOffset;
            int bgHeight = Math.min(TEX_STATE_HEIGHT, bounds.bottom - bounds.top);

            int color = ARGB.colorFromFloat(this.alpha, h.getRed(), h.getGreen(), h.getBlue());
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, bounds.left, bounds.top, TEX_U, v, 1, bgHeight, 256, 256, color);
            int innerWidth = bounds.right - bounds.left - 2;
            int drawn = 0;
            while (drawn < innerWidth) {
                int part = Math.min(TEX_WIDTH - 2, innerWidth - drawn);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, bounds.left + 1 + drawn, bounds.top, TEX_U + 1, v, part, bgHeight, 256, 256, color);
                drawn += part;
            }
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, bounds.right - 1, bounds.top, TEX_U + TEX_WIDTH - 1, v, 1, bgHeight, 256, 256, color);
        }

        int textColor = currentTextColor();
        this.setTextColor(textColor);
        this.setTextColorUneditable(muted(textColor));

        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partial);

        if (placeholder != null && !isFocused() && getValue().isEmpty()) {
            guiGraphics.text(Minecraft.getInstance().font, placeholder, getX(), getY(), muted(textColor), false);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        var bounds = getVisualBounds();
        return this.visible && mouseX >= bounds.left && mouseX < bounds.right
                && mouseY >= bounds.top && mouseY < bounds.bottom;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            var mouseX = Mth.clamp(event.x(), getX(), getX() + width - 1);
            var mouseY = Mth.clamp(event.y(), getY(), getY() + height - 1);
            event = new MouseButtonEvent(mouseX, mouseY, event.buttonInfo());
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (canConsumeInput() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            if (this.onConfirm != null) {
                this.onConfirm.run();
            }
            return true;
        }

        if (super.keyPressed(event)) {
            return true;
        }

        return isFocused() && canConsumeInput() && keyCode != GLFW.GLFW_KEY_TAB && keyCode != GLFW.GLFW_KEY_ESCAPE;
    }


    @Override
    public void move(Point pos) {
        super.setX(pos.getX() + PADDING);
        setY(pos.getY() + PADDING);
    }

    @Override
    public void resize(int width, int height) {
        super.setWidth(Math.max(0, width - 2 * PADDING - fontPad));
        this.height = Math.max(0, height - 2 * PADDING);
    }

    private VisualBounds getVisualBounds() {
        int left = getX() - PADDING;
        int top = getY() - PADDING;
        int right = left + width + 2 * PADDING + fontPad;
        return new VisualBounds(left, top, right, top + height + 2 * PADDING);
    }


    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX() - PADDING, getY() - PADDING,
                width + 2 * PADDING + fontPad, height + 2 * PADDING);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    @NotNull
    @Override
    public List<Component> getTooltipMessage() {
        return tooltipMessage;
    }

    private record VisualBounds(int left, int top, int right, int bottom) {}
}
