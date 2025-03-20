package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ICrazyAEProgressProvider;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends GuiButton implements ITooltipObj {

    private final ICrazyAEProgressProvider source;

    private final int id;
    private final Direction layout;
    private final String titleName;
    private String forceMsg;
    private boolean disableHue;
    private final ComponentHue hue;

    private boolean disableMaxProgress = false;

    private final Sprite filledSprite;
    private final Sprite emptySprite;

    public ProgressBar(final ICrazyAEProgressProvider source, final int posX, final int posY, final int width, final int height, final Direction dir, final String title, final int id, final ComponentHue hue, Sprite filledSprite, Sprite emptySprite, boolean disableMaxProgress) {
        super(id, posX, posY, "");
        this.source = source;
        this.x = posX;
        this.y = posY;
        this.width = width;
        this.height = height;
        this.layout = dir;
        this.titleName = title;
        this.id = id;
        this.hue = hue;
        this.filledSprite = filledSprite;
        this.emptySprite = emptySprite;
        this.disableMaxProgress = disableMaxProgress;
    }

    @Override public void playPressSound(@NotNull SoundHandler soundHandlerIn) {}

    @Override
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        if (this.visible) {
            if (!this.disableHue) this.hue.drawHue();

            if (this.emptySprite != null) {
                par1Minecraft.getTextureManager().bindTexture(
                        this.emptySprite.getTexture()
                );
                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        this.emptySprite.getTextureX(),
                        this.emptySprite.getTextureY(),
                        this.width,
                        this.height
                );
            }

            final double max = this.source.getMaxProgress(this.id);
            final double current = this.source.getCurrentProgress(this.id);

            int texX = this.filledSprite.getTextureX();
            int texY = this.filledSprite.getTextureY();

            if (this.layout == Direction.VERTICAL) {
                final int diff = (int) Math.min((this.height * current / max), 100);
                par1Minecraft.getTextureManager().bindTexture(
                        this.filledSprite.getTexture()
                );

                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        texX,
                        texY,
                        this.width,
                        this.disableMaxProgress && current > 0 ? this.filledSprite.getSizeY() : diff
                );
            } else {
                final int diff = (int) Math.min((this.width * current / max), 100);
                par1Minecraft.getTextureManager().bindTexture(
                        this.filledSprite.getTexture()
                );

                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        texX,
                        texY,
                        this.disableMaxProgress && current > 0 ? this.filledSprite.getSizeX() : diff,
                        this.height
                );
            }

            if (!this.disableHue) this.hue.endDrawHue();
            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    public ProgressBar disableHue() {
        this.disableHue = true;
        return this;
    }

    public void setVisible(boolean v) {
        this.visible = v;
    }

    public void setForceMsg(final String msg) {
        this.forceMsg = msg;
    }

    @Override
    public String getTooltipMsg() {
        return this.forceMsg != null ? this.forceMsg : this.source.getTooltip(this.titleName, this.disableMaxProgress, this.id);
    }

    @Override
    public int xPos() {
        return this.x - 2;
    }

    @Override
    public int yPos() {
        return this.y - 2;
    }

    @Override
    public int getWidth() {
        return this.width + 4;
    }

    @Override
    public int getHeight() {
        return this.height + 4;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public enum Direction {
        HORIZONTAL, VERTICAL
    }
}
