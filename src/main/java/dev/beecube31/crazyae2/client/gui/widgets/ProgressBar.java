package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ICrazyAEProgressProvider;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class ProgressBar extends GuiButton implements ITooltipObj {

    private final ICrazyAEProgressProvider source;

    private final int id;
    private final Direction layout;
    private final String titleName;
    private String fullMsg;

    private final ComponentHue hue;

    public ProgressBar(final ICrazyAEProgressProvider source, final int posX, final int posY, final int width, final int height, final Direction dir, final int id, final ComponentHue hue) {
        this(source, posX, posY, width, height, dir, null, id, hue);
    }

    public ProgressBar(final ICrazyAEProgressProvider source, final int posX, final int posY, final int width, final int height, final Direction dir, final String title, final int id, final ComponentHue hue) {
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
    }

    @Override public void playPressSound(SoundHandler soundHandlerIn) {}

    @Override
    public void drawButton(final Minecraft par1Minecraft, final int par2, final int par3, final float partial) {
        if (this.visible) {
            this.hue.drawHue();
            par1Minecraft.getTextureManager().bindTexture(
                    new ResourceLocation("crazyae", "textures/guis/states.png")
            );
            final int max = this.source.getMaxProgress(this.id);
            final int current = this.source.getCurrentProgress(this.id);

            int texX = StateSprite.PROGRESS_BAR_FILLED.getTextureX();
            int texY = StateSprite.PROGRESS_BAR_FILLED.getTextureY();

            if (this.layout == Direction.VERTICAL) {
                final int diff = this.height - (max > 0 ? (this.height * current) / max : 0);
                this.drawTexturedModalRect(
                        this.x,
                        this.y + diff,
                        texX,
                        texY + diff,
                        this.width,
                        this.height - diff
                );
            } else {
                final int diff = this.width - (max > 0 ? (this.width * current) / max : 0);
                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        texX + diff,
                        texY,
                        this.width - diff,
                        this.height
                );
            }

            this.hue.endDrawHue();
            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    public void setVisible(boolean v) {
        this.visible = v;
    }

    public void setFullMsg(final String msg) {
        this.fullMsg = msg;
    }

    @Override
    public String getTooltipMsg() {
        if (this.fullMsg != null) {
            return this.fullMsg;
        }

        return (this.titleName != null ? this.titleName : "") + '\n' + this.source.getCurrentProgress(this.id) + ' ' + GuiText.Of.getLocal() + ' ' + this.source
                .getMaxProgress(this.id);
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
