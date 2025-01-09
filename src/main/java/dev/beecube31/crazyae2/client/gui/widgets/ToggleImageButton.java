package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class ToggleImageButton extends GuiButton implements ITooltipObj {
    private final String message;
    private StateSprite enabledIco;
    private StateSprite disabledIco;

    private final ComponentHue hue;

    private final Enum buttonSetting;
    private Enum currentValue;

    public ToggleImageButton(final int x, final int y, final StateSprite enabledIco, final StateSprite disabledIco, final String message, final ComponentHue hue, final int id, final Enum type, final Enum value) {
        super(id, 0, 0, "");

        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.enabledIco = enabledIco;
        this.disabledIco = disabledIco;
        this.message = message;
        this.hue = hue;
        this.buttonSetting = type;
        this.currentValue = value;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int x, final int y, float partial) {
        if (this.visible) {
            this.hue.drawHue();
            minecraft.renderEngine.bindTexture(new ResourceLocation("crazyae", "textures/guis/states.png"));
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;

            final int offsetX = 1;

            if (this.enabledIco != null) {
                this.drawTexturedModalRect(
                        offsetX + this.x,
                        this.y,
                        StateSprite.IMAGE_BUTTON.getTextureX(),
                        StateSprite.IMAGE_BUTTON.getTextureY(),
                        StateSprite.IMAGE_BUTTON.getSizeX(),
                        StateSprite.IMAGE_BUTTON.getSizeY()
                );

                this.drawTexturedModalRect(
                        offsetX + this.x,
                        this.y,
                        this.enabledIco.getTextureX(),
                        this.enabledIco.getTextureY(),
                        this.enabledIco.getSizeX(),
                        this.enabledIco.getSizeY()
                );
            }

            this.hue.endDrawHue();

            this.mouseDragged(minecraft, x, y);
        }
    }

    @Override
    public String getTooltipMsg() {
        return this.message;
    }

    @Override
    public int xPos() {
        return this.x;
    }

    @Override
    public int yPos() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
}
