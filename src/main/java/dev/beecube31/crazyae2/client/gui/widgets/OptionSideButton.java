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

public class OptionSideButton extends GuiButton implements ITooltipObj {
    private final RenderItem itemRenderer;
    private final String tooltipMsg;
    private final String renderString;
    private StateSprite myIcon;
    private ItemStack myItem;

    private final ComponentHue hue;
    private final ComponentHue textHue;

    private ButtonType type;


    public OptionSideButton(
            final int x,
            final int y,
            final StateSprite ico,
            final String tooltipMsg,
            final String renderString,
            final RenderItem ir,
            final ComponentHue hue,
            final ComponentHue textHue,
            final int id,
            final ButtonType type
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
        this.type = type;
    }

    public OptionSideButton(
            final int x,
            final int y,
            final ItemStack ico,
            final String tooltipMsg,
            final String renderString,
            final RenderItem ir,
            final ComponentHue hue,
            final ComponentHue textHue,
            final int id,
            final ButtonType type
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
        this.type = type;
    }

    @Override
    public void drawButton(final Minecraft minecraft, final int x, final int y, float partial) {
        if (this.visible) {
            this.hue.drawHue();

            minecraft.renderEngine.bindTexture(
                    new ResourceLocation("crazyae", "textures/guis/states.png")
            );
            this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;

            switch (this.type) {
                case DEFAULT -> {
                    this.drawTexturedModalRect(
                            this.x,
                            this.y,
                            StateSprite.OPTION_SIDE_BUTTON.getTextureX(),
                            StateSprite.OPTION_SIDE_BUTTON.getTextureY(),
                            StateSprite.OPTION_SIDE_BUTTON.getSizeX(),
                            StateSprite.OPTION_SIDE_BUTTON.getSizeY()
                    );
                }

                case NO_TOP -> {
                    this.drawTexturedModalRect(
                            this.x,
                            this.y,
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP.getTextureX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP.getTextureY(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP.getSizeX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP.getSizeY()
                    );
                }

                case NO_BOTTOM -> {
                    this.drawTexturedModalRect(
                            this.x,
                            this.y,
                            StateSprite.OPTION_SIDE_BUTTON_NO_BOTTOM.getTextureX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_BOTTOM.getTextureY(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_BOTTOM.getSizeX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_BOTTOM.getSizeY()
                    );
                }

                case NO_TOP_BOTTOM -> {
                    this.drawTexturedModalRect(
                            this.x,
                            this.y,
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP_BOTTOM.getTextureX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP_BOTTOM.getTextureY(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP_BOTTOM.getSizeX(),
                            StateSprite.OPTION_SIDE_BUTTON_NO_TOP_BOTTOM.getSizeY()
                    );
                }
            }

            this.hue.endDrawHue();

            if (this.renderString != null && !this.renderString.isEmpty()) {
                if (this.textHue != null) {
                    this.hue.endDrawHue();

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

            if (this.textHue != null) {
                this.textHue.endDrawHue();
            }

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

    public enum ButtonType {
        DEFAULT, NO_TOP, NO_BOTTOM, NO_TOP_BOTTOM;
    }
}
