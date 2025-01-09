package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Checkbox extends GuiButton implements ITooltipObj {
    private boolean visible = true;
    private boolean state;
    private String text;

    private final ComponentHue textHue;

    public Checkbox(final int id, final int posX, final int posY, final int width, final int height, final boolean state, final String text, final ComponentHue textHue) {
        super(id, posX, posY, width, height, "");
        this.x = posX;
        this.y = posY;
        this.state = state;
        this.text = text;
        this.textHue = textHue;
    }

    @Override
    public void drawButton(final @NotNull Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        if (this.visible) {
            this.textHue.drawHue();
            par1Minecraft.fontRenderer.drawString(this.text, this.x + 18, this.y + 2, this.textHue.getIntColor());
            this.textHue.endDrawHue();

            par1Minecraft.getTextureManager().bindTexture(new ResourceLocation("crazyae", "textures/guis/states.png"));

            this.drawTexturedModalRect(
                    this.x,
                    this.y,
                    this.state ? StateSprite.CHECKBOX_ON.getTextureX() : StateSprite.CHECKBOX_OFF.getTextureX(),
                    this.state ? StateSprite.CHECKBOX_ON.getTextureY() : StateSprite.CHECKBOX_OFF.getTextureY(),
                    this.state ? StateSprite.CHECKBOX_ON.getSizeX() : StateSprite.CHECKBOX_OFF.getSizeX(),
                    this.state ? StateSprite.CHECKBOX_ON.getSizeY() : StateSprite.CHECKBOX_OFF.getSizeY()
            );

            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public void toggleState() {
        this.state = !this.state;
    }

    public void setState(final boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return this.state;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getTooltipMsg() {
        return null;
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
