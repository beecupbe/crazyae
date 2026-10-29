package dev.beecube31.crazyae2.client.gui.widgets;

import net.minecraft.client.input.InputWithModifiers;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.ISpriteProvider;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OptionSideButton extends WidgetBase {
    public enum ButtonType {
        DEFAULT(StateSprite.OPTION_SIDE_BUTTON),
        CENTER(StateSprite.OPTION_SIDE_BUTTON_CENTER),
        NO_TOP(StateSprite.OPTION_SIDE_BUTTON_NO_TOP),
        NO_BOTTOM(StateSprite.OPTION_SIDE_BUTTON_NO_BOTTOM),
        NO_TOP_BOTTOM(StateSprite.OPTION_SIDE_BUTTON_NO_TOP_BOTTOM);

        private final StateSprite background;

        ButtonType(StateSprite background) {
            this.background = background;
        }
    }

    private Object icon; // Can be ISpriteProvider or ItemStack
    private final ButtonType type;
    private final Supplier<ColorContainer> hue;
    private final Runnable onPress;

    public OptionSideButton(Object icon, Component tooltip, ButtonType type, Supplier<ColorContainer> hue,
            Runnable onPress) {
        super(0, 0, StateSprite.OPTION_SIDE_BUTTON.getSizeX(), StateSprite.OPTION_SIDE_BUTTON.getSizeY(), tooltip);

        this.icon = icon;
        Preconditions.checkArgument(this.icon != null && (this.icon instanceof ItemStack || this.icon instanceof ISpriteProvider), "Icon must be a ItemStack or ISpriteProvider");

        this.type = type;
        this.hue = hue;
        this.onPress = onPress;
        this.setTooltipText(tooltip);
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public void setTooltipText(Component text) {
        this.setMessage(text);
        this.setTooltip(Tooltip.create(text));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.run();
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ColorContainer h = this.hue.get();
        this.type.background.getBlitter().copy()
                .color(h.getRed(), h.getGreen(), h.getBlue(), 1F)
                .dest(getX(), getY())
                .blit(guiGraphics);

        if (this.icon != null) {
            if (this.icon instanceof ISpriteProvider sprite) {
                int dy = this.icon == StateSprite.ABC ? 7 : 3;
                sprite.getBlitter().copy().dest(getX() + 3, getY() + dy).blit(guiGraphics);
            } else if (this.icon instanceof ItemStack item) {
                guiGraphics.item(item, getX() + 3, getY() + 3);
                var font = Minecraft.getInstance().font;
                guiGraphics.itemDecorations(font, item, getX() + 3, getY() + 3);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
