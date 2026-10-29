package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CrazyClientButton extends Button implements ITooltip {

    private final StateSprite icon;
    private final ColorContainer hue;
    private final Runnable onPress;
    private final List<Component> tooltip;

    public CrazyClientButton(StateSprite icon, List<Component> tooltip,
                             ColorContainer hue, Runnable onPress) {
        super(0, 0, 19, 21, Component.empty(), (btn) -> {}, Button.DEFAULT_NARRATION);
        this.icon = icon;
        this.hue = hue;
        this.onPress = onPress;
        this.tooltip = tooltip;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.run();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var icon = this.icon;

            Blitter blitter = icon.getBlitter();
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            Blitter bg = StateSprite.IMAGE_BUTTON_TOP.getBlitter().color(this.hue.getRed(), this.hue.getGreen(), this.hue.getBlue(), this.hue.getAlpha());
            bg.dest(getX(), getY()).blit(guiGraphics);
            icon.getBlitter().dest(getX() + 2, getY() + 2).blit(guiGraphics);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    public void setVisibility(boolean vis) {
        this.visible = vis;
        this.active = vis;
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(
                getX(),
                getY(),
                19,
                21);
    }

    @Override
    public List<Component> getTooltipMessage() {
        return this.tooltip;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }
}
