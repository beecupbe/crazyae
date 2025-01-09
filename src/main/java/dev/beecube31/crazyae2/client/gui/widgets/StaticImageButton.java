package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class StaticImageButton extends GuiButton implements ITooltipObj {
    private String message;
    private StateSprite myIcon;

    private final ComponentHue hue;

    private boolean disableHue = false;

    public StaticImageButton(final int x, final int y, @Nullable final StateSprite ico, final String message, final ComponentHue guiHue, final int id) {
        super(id, 0, 0, "");

        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.myIcon = ico;
        this.message = message;
        this.hue = guiHue;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int x, final int y, float partial) {
        if (this.visible) {
            minecraft.renderEngine.bindTexture(new ResourceLocation("crazyae", "textures/guis/states.png"));
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;

            final int offsetX = 1;

            if (this.myIcon != null) {
                this.hue.drawHue();
                this.drawTexturedModalRect(
                        offsetX + this.x,
                        this.y,
                        StateSprite.IMAGE_BUTTON.getTextureX(),
                        StateSprite.IMAGE_BUTTON.getTextureY(),
                        StateSprite.IMAGE_BUTTON.getSizeX(),
                        StateSprite.IMAGE_BUTTON.getSizeY()
                );

                this.hue.endDrawHue();

                if (!this.disableHue) this.hue.drawHue();
                this.drawTexturedModalRect(
                        offsetX + this.x,
                        this.y,
                        this.myIcon.getTextureX(),
                        this.myIcon.getTextureY(),
                        this.myIcon.getSizeX(),
                        this.myIcon.getSizeY()
                );

                this.hue.endDrawHue();
            }

            this.mouseDragged(minecraft, x, y);
        }
    }

    public void setMyIcon(StateSprite myIcon) {
        this.myIcon = myIcon;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDisableHue(boolean v) {
        this.disableHue = v;
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

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }
}
