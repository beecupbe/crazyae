package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import net.minecraft.client.input.InputWithModifiers;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.WidgetBase;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class HoverImageButton extends WidgetBase {
    private static final Identifier TEXTURE = CrazyAE.makeId("textures/guis/widgets/buttons.png");

    private final StateSprite icon;
    private final Supplier<ColorContainer> hue;
    private final Runnable onPress;

    public HoverImageButton(int x, int y, int width, int height, StateSprite icon, Component tooltip,
            Supplier<ColorContainer> hue, Runnable onPress) {
        super(x, y, width, height, tooltip);
        this.icon = icon;
        this.hue = hue;
        this.onPress = onPress;
        this.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTicks) {
        ColorContainer h = this.hue.get();
        int hoverState = !this.active ? 0 : (this.isHovered ? 2 : 1);
        int textureY = 46 + hoverState * 20;
        int halfWidth = this.width / 2;
        int halfHeight = this.height / 2;
        int secondHalfWidth = this.width - halfWidth;
        int secondHalfHeight = this.height - halfHeight;

        int color = ARGB.colorFromFloat(1F, h.getRed(), h.getGreen(), h.getBlue());
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 0, textureY, halfWidth, halfHeight, 256, 256, color);
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX() + halfWidth, getY(), 200 - secondHalfWidth, textureY, secondHalfWidth, halfHeight, 256, 256, color);
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY() + halfHeight, 0, textureY + (20 - secondHalfHeight), halfWidth, secondHalfHeight, 256, 256, color);
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX() + halfWidth, getY() + halfHeight, 200 - secondHalfWidth,
                textureY + (20 - secondHalfHeight), secondHalfWidth, secondHalfHeight, 256, 256, color);

        int ox = (this.width - this.icon.getSizeX()) / 2;
        int oy = (this.height - this.icon.getSizeY()) / 2;
        this.icon.getBlitter().copy().color(h.getRed(), h.getGreen(), h.getBlue(), 1F)
                .dest(getX() + ox, getY() + oy).blit(g);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
