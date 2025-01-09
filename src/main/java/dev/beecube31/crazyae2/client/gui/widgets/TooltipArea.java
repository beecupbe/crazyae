package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;

public class TooltipArea extends GuiButton implements ITooltipObj {
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
        this.mouseDragged(par1Minecraft, par2, par3);
    }

    @Override public void playPressSound(SoundHandler soundHandlerIn) {}

    @Override
    public String getTooltipMsg() {
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
