package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import appeng.api.config.Setting;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.SettingToggleButton;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.common.network.packets.PacketConfigButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class CrazyServerSettingToggleButton<T extends Enum<T>> extends SettingToggleButton<T> {

    private ColorContainer hue;
    private StateSprite sprite;

    public CrazyServerSettingToggleButton(Setting<T> setting, T val, ColorContainer hue, StateSprite sprite) {
        super(setting, val, CrazyServerSettingToggleButton::sendToServer);
        this.hue = hue;
        this.sprite = sprite;
    }

    private static <T extends Enum<T>> void sendToServer(SettingToggleButton<T> button, boolean backwards) {
        CrazyNetworkHandler.instance().sendToServer(new PacketConfigButton(button.getSetting(), backwards));
    }

    private boolean isLeftSideButton() {
        return this.sprite == StateSprite.IMAGE_BUTTON_TOP || this.sprite == StateSprite.IMAGE_BUTTON_CENTER || this.sprite == StateSprite.IMAGE_BUTTON_BOTTOM;
    }

    public CrazyServerSettingToggleButton<T> setSprite(StateSprite sprite) {
        this.sprite = sprite;
        return this;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var icon = this.getIcon();

            Blitter blitter = Blitter.icon(icon);
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
            }


            if (isFocused()) {
                if (isLeftSideButton()) {
                    // left
                    guiGraphics.fill(getX() - 4, getY(), getX(), getY() + height + 6, 0xFFFFFFFF);
                    // bottom
                    guiGraphics.fill(getX() - 4, getY() + height, getX() + width - 1, getY() + height + 6, 0xFFFFFFFF);
                } else {
                    // Draw 1px border with 4 quads, don't rely on the background as it can be disabled.
                    // top
                    guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                    // left
                    guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                    // right
                    guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                    // bottom
                    guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
                }
            }

            if (this.isHalfSize()) {
                var pose = guiGraphics.pose();
                pose.pushMatrix();
                pose.translate(getX(), getY()); 
                pose.scale(0.5f, 0.5f);

                if (!this.isDisableBackground()) {
                    Blitter bg = StateSprite.IMAGE_BUTTON.getBlitter().color(this.hue.getRed(), this.hue.getGreen(), this.hue.getBlue(), this.hue.getAlpha());
                    bg.dest(0, 0).blit(guiGraphics);
                }
                blitter.dest(0, 0).blit(guiGraphics);
                pose.popMatrix();
            } else {
                if (!this.isDisableBackground()) {
                    Blitter bg = this.sprite.getBlitter().color(this.hue.getRed(), this.hue.getGreen(), this.hue.getBlue(), this.hue.getAlpha());
                    bg.dest(getX() - (isLeftSideButton() ? 3 : 0), getY()).blit(guiGraphics);
                }
                blitter.dest(getX() + (isLeftSideButton() ? -1 : 0), getY() + (isLeftSideButton() ? 2 : 0)).blit(guiGraphics);
            }

            var item = this.getItemOverlay();
            if (item != null) {
                guiGraphics.item(new ItemStack(item), getX(), getY());
            }
        }
    }

}
