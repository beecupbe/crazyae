package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.ISpriteProvider;
import dev.beecube31.crazyae2.common.interfaces.gui.ICheckboxProvider;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class Checkbox extends GuiButton implements ITooltipObj {
    private boolean visible = true;
    private final ICheckboxProvider state;
    private final int id;
    private String text;
    private boolean overrideState = false;
    private final ResourceLocation texture;

    private final ComponentHue textHue;

    private final ISpriteProvider spriteOn;
    private final ISpriteProvider spriteOff;

    public Checkbox(final int id, final int posX, final int posY, final int width, final int height, final ICheckboxProvider state, final String text, final ComponentHue textHue, ResourceLocation texture, ISpriteProvider spriteOn, ISpriteProvider spriteOff) {
        super(id, posX, posY, width, height, "");
        this.id = id;
        this.x = posX;
        this.y = posY;
        this.state = state;
        this.text = text;
        this.textHue = textHue;
        this.texture = texture;
        this.spriteOn = spriteOn;
        this.spriteOff = spriteOff;
    }

    @Override
    public void drawButton(final @NotNull Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        if (this.visible) {
            this.textHue.drawHue();
            par1Minecraft.fontRenderer.drawString(this.text, this.x + 18, this.y + 2, this.textHue.getIntColor());
            this.textHue.endDrawHue();

            par1Minecraft.getTextureManager().bindTexture(texture);

            this.drawTexturedModalRect(
                    this.x,
                    this.y,
                    (this.overrideState || this.state.getCheckboxCurrentState(this.id)) ? this.spriteOn.getTextureX() : this.spriteOff.getTextureX(),
                    (this.overrideState || this.state.getCheckboxCurrentState(this.id)) ? this.spriteOn.getTextureY() : this.spriteOff.getTextureY(),
                    (this.overrideState || this.state.getCheckboxCurrentState(this.id)) ? this.spriteOn.getSizeX() : this.spriteOff.getSizeX(),
                    (this.overrideState || this.state.getCheckboxCurrentState(this.id)) ? this.spriteOn.getSizeY() : this.spriteOff.getSizeY()
            );

            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public boolean getState() {
        return this.state.getCheckboxCurrentState();
    }

    public void setState(boolean state) {
        this.overrideState = state;
    }

    public boolean getState(int idx) {
        return this.state.getCheckboxCurrentState(idx);
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
