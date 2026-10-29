package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ToggleButton;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class CrazyToggleButton extends ToggleButton {

    private final ColorContainer hue;
    private final StateSprite sprite;

    private final Icon iconOn;
    private final Icon iconOff;
    private boolean on;

    public CrazyToggleButton(Icon on, Icon off, Component displayName, Component displayHint,
                             ColorContainer hue, StateSprite sprite, Listener listener) {
        super(on, off, displayName, displayHint, listener);
        this.iconOn = on;
        this.iconOff = off;
        this.hue = hue;
        this.sprite = sprite;
    }

    @Override
    public void setState(boolean isOn) {
        super.setState(isOn);
        this.on = isOn;
    }

    private boolean isLeftSideButton() {
        return this.sprite == StateSprite.IMAGE_BUTTON_TOP
                || this.sprite == StateSprite.IMAGE_BUTTON_CENTER
                || this.sprite == StateSprite.IMAGE_BUTTON_BOTTOM;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!this.visible) {
            return;
        }

        Icon icon = this.on ? this.iconOn : this.iconOff;
        Blitter iconBlitter = icon.getBlitter().copy();
        if (!this.active) {
            iconBlitter.opacity(0.5f);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        if (isFocused()) {
            if (isLeftSideButton()) {
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height + 6, 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 6, 0xFFFFFFFF);
            } else {
                guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
            }
        }

        Blitter bg = this.sprite.getBlitter().copy()
                .color(this.hue.getRed(), this.hue.getGreen(), this.hue.getBlue(), this.hue.getAlpha());
        bg.dest(getX() - 3, getY()).blit(guiGraphics);

        int offset = isLeftSideButton() ? 2 : 0;
        iconBlitter.dest(getX() + offset - 3, getY() + offset).blit(guiGraphics);

        RenderSystem.enableDepthTest();
    }
}
