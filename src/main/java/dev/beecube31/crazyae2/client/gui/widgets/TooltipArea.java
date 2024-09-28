package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TooltipArea extends GuiButton implements ITooltip {
    private final String tooltipMessageLocal;

    public TooltipArea(final int id, final int posX, final int posY, final int width, final int height, final String msg) {
        super(id, posX, posY, width, height, "");
        this.x = posX;
        this.y = posY;
        this.width = width;
        this.height = height;
        this.tooltipMessageLocal = msg;
    }

    @Override
    public void drawButton(final @NotNull Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        if (this.visible) {
            par1Minecraft.getTextureManager().bindTexture(new ResourceLocation("crazyae", "textures/guis/tooltip.png"));
            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    @Override
    public String getMessage() {
        return this.tooltipMessageLocal;
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
        return true;
    }
}
