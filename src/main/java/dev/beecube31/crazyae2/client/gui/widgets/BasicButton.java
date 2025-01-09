package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class BasicButton extends GuiButton {
    private final ComponentHue hue;

    public BasicButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, final ComponentHue hue) {
        super(buttonId, x, y, buttonText);

        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.hue = hue;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int mouseX, final int mouseY, float partialTicks) {
        if (this.visible) {
            this.hue.drawHue();
            FontRenderer fontrenderer = minecraft.fontRenderer;
            minecraft.renderEngine.bindTexture(new ResourceLocation("crazyae", "textures/guis/widgets/buttons.png"));
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(minecraft, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);

            this.hue.endDrawHue();
        }
    }
}
