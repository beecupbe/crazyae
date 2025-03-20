package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;

public class InvisibleButton extends GuiButton implements ITooltipObj {
    private String text;

    public InvisibleButton(final int id, final int posX, final int posY, final int width, final int height, final String text) {
        super(id, posX, posY, width, height, "");
        this.x = posX;
        this.y = posY;
        this.text = text;
    }

    @Override public void drawButton(final @NotNull Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        this.mouseDragged(par1Minecraft, par2, par3);
    }

    public void setText(String text) { this.text = text; }

    @Override public String getTooltipMsg() {
        return text;
    }

    @Override public int xPos() {
        return this.x;
    }

    @Override public int yPos() {
        return this.y;
    }

    @Override public int getWidth() {
        return this.width;
    }

    @Override public int getHeight() {
        return this.height;
    }

    @Override public boolean isVisible() {
        return true;
    }
}
