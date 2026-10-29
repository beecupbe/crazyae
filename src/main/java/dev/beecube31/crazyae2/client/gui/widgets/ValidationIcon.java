package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.util.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyButton;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ValidationIcon extends CrazyButton implements ITooltip {
    private final List<Component> tooltip = new ArrayList<>();

    private final ColorContainer hue;

    public ValidationIcon(OnPress onPress, ColorContainer hue, ColorContainer textHue) {
        super(0, 0, 16, 16, Component.empty(), onPress, CrazyButton.DEFAULT_NARRATION, hue);

        this.hue = hue;
    }

    public void setValid(boolean valid) {
        setVisibility(!valid);
        if (valid) {
            this.tooltip.clear();
        }
    }

    public void setVisibility(boolean vis) {
        this.visible = vis;
        this.active = vis;
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundHandler) {}

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {

        if (this.visible) {
            var icon = Icon.INVALID;

            Blitter blitter = Blitter.icon(icon);
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            this.width = 8;
            this.height = 8;


            var pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate(getX(), getY());
            pose.scale(0.5f, 0.5f);

            blitter.color(this.hue.getRed(), this.hue.getGreen(), this.hue.getBlue(), this.active ? this.hue.getAlpha() : this.hue.getAlpha() * 0.5f);

            blitter.dest(0, 0).blit(guiGraphics);
            pose.popMatrix();

        }
    }

    @Override
    public List<Component> getTooltipMessage() {
        return tooltip;
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), 8, 8);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    public void setTooltip(List<Component> lines) {
        this.tooltip.clear();
        this.tooltip.addAll(lines);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent focusNavigationEvent) {
        // Cannot focus this element
        return null;
    }
}
