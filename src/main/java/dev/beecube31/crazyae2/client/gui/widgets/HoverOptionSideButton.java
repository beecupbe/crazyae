package dev.beecube31.crazyae2.client.gui.widgets;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class HoverOptionSideButton extends GuiButton implements ITooltipObj {
    private final RenderItem itemRenderer;
    private final String tooltipMsg;
    private final String renderString;
    private StateSprite myIcon;
    private ItemStack myItem;
    private boolean isChecked;

    private final ComponentHue hue;
    private final ComponentHue textHue;


    public HoverOptionSideButton(
            final int x,
            final int y,
            final StateSprite ico,
            final String tooltipMsg,
            final String renderString,
            final RenderItem ir,
            final ComponentHue hue,
            final ComponentHue textHue,
            final int id
    ) {
        super(id, x, y, "");

        this.x = x;
        this.y = y;
        this.width = StateSprite.OPTION_SIDE_BUTTON.getSizeX();
        this.height = StateSprite.OPTION_SIDE_BUTTON.getSizeY();
        this.myIcon = ico;
        this.tooltipMsg = tooltipMsg;
        this.itemRenderer = ir;
        this.hue = hue;
        this.textHue = textHue;
        this.renderString = renderString;
    }

    public HoverOptionSideButton(
            final int x,
            final int y,
            final ItemStack ico,
            final String tooltipMsg,
            final String renderString,
            final RenderItem ir,
            final ComponentHue hue,
            final ComponentHue textHue,
            final int id
    ) {
        super(id, x, y, "");

        this.x = x;
        this.y = y;
        this.width = StateSprite.OPTION_SIDE_BUTTON.getSizeX();
        this.height = StateSprite.OPTION_SIDE_BUTTON.getSizeY();
        this.myItem = ico;
        this.tooltipMsg = tooltipMsg;
        this.itemRenderer = ir;
        this.hue = hue;
        this.textHue = textHue;
        this.renderString = renderString;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int x, final int y, float partial) {
        if (this.visible) {
            this.hue.drawHue();

            minecraft.renderEngine.bindTexture(
                    new ResourceLocation("crazyae", "textures/guis/states.png")
            );
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;


            if (this.isChecked) {
                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_SELECTED.getTextureX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_SELECTED.getTextureY(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_SELECTED.getSizeX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_SELECTED.getSizeY()
                );
            } else if (this.hovered) {
                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_HOVERED.getTextureX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_HOVERED.getTextureY(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_HOVERED.getSizeX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON_HOVERED.getSizeY()
                );
            } else {
                this.drawTexturedModalRect(
                        this.x,
                        this.y,
                        StateSprite.HOVER_OPTION_SIDE_BUTTON.getTextureX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON.getTextureY(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON.getSizeX(),
                        StateSprite.HOVER_OPTION_SIDE_BUTTON.getSizeY()
                );
            }

            this.hue.endDrawHue();

            if (this.renderString != null && !this.renderString.isEmpty()) {
                if (this.textHue != null) {
                    this.textHue.drawHue();
                    this.textHue.drawString(this.renderString, this.x + 3, this.y + 7, minecraft.fontRenderer);
                    this.textHue.endDrawHue();
                }
            }

            if (this.myIcon != null) {
                if (this.myIcon == StateSprite.ABC) {
                    this.drawTexturedModalRect(
                            this.x + 3,
                            this.y + 7,
                            this.myIcon.getTextureX(),
                            this.myIcon.getTextureY(),
                            this.myIcon.getSizeX(),
                            this.myIcon.getSizeY()
                    );
                } else {
                    this.drawTexturedModalRect(
                            this.x + 3,
                            this.y + 3,
                            this.myIcon.getTextureX(),
                            this.myIcon.getTextureY(),
                            this.myIcon.getSizeX(),
                            this.myIcon.getSizeY()
                    );
                }
            }

            this.mouseDragged(minecraft, x, y);

            if (this.myItem != null && this.myItem != ItemStack.EMPTY) {
                this.zLevel = 100.0F;
                this.itemRenderer.zLevel = 100.0F;

                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();
                this.itemRenderer.renderItemAndEffectIntoGUI(
                        this.myItem,
                        this.x + 4,
                        this.y + 3
                );
                GlStateManager.disableDepth();

                this.itemRenderer.zLevel = 0.0F;
                this.zLevel = 0.0F;
            }
        }
    }

    public void setChecked(boolean v) {
        this.isChecked = v;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public String getTooltipMsg() {
        return this.tooltipMsg;
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