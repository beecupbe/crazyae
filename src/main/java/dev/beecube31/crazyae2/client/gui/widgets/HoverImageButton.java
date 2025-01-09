package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HoverImageButton extends GuiButton implements ITooltipObj {
    private final ComponentHue hue;

    private String message;
    private StateSprite myIcon;

    public HoverImageButton(int buttonId, int x, int y, int widthIn, int heightIn, StateSprite ico, String tooltipMsg, final ComponentHue hue) {
        super(buttonId, x, y, "");

        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.hue = hue;
        this.myIcon = ico;
        this.message = tooltipMsg;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int mouseX, final int mouseY, float partialTicks) {
        if (this.visible) {
            this.hue.drawHue();
            minecraft.renderEngine.bindTexture(new ResourceLocation("crazyae", "textures/guis/widgets/buttons.png"));
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(minecraft, mouseX, mouseY);

            final int offsetX = 2;
            final int offsetY = 2;

            minecraft.renderEngine.bindTexture(new ResourceLocation("crazyae", "textures/guis/states.png"));
            this.drawTexturedModalRect(
                    offsetX + this.x,
                    offsetY + this.y,
                    this.myIcon.getTextureX(),
                    this.myIcon.getTextureY(),
                    this.myIcon.getSizeX(),
                    this.myIcon.getSizeY()
            );

            this.hue.endDrawHue();
        }
    }

    public void setMyIcon(StateSprite myIcon) {
        this.myIcon = myIcon;
    }

    public void setMessage(String message) {
        this.message = message;
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
