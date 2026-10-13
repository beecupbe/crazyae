package dev.beecube31.crazyae2.client.gui.widgets;

import appeng.client.Point;
import appeng.client.gui.Tooltip;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.ICrazyCompositeWidget;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CrazyToolboxPanel implements ICrazyCompositeWidget {

    // Backdrop for the 3x3 toolbox offered by the network-tool
    private final Blitter background;

    private final Component toolbeltName;

    // Relative to the origin of the current screen (not window)
    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    public CrazyToolboxPanel(ScreenStyle style, Component toolbeltName, ColorContainer colorContainer) {
        this.background = Blitter.texture(new ResourceLocation(CrazyAE.MODID, "textures/guis/toolbox.png"), 68, 68)
                .src(0,0,68,68)
                .color(colorContainer.getRed(), colorContainer.getGreen(), colorContainer.getBlue(), colorContainer.getAlpha());
        this.toolbeltName = toolbeltName;
    }

    @Override
    public void setPosition(Point position) {
        this.bounds = new Rect2i(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public void setSize(int width, int height) {
        this.bounds = new Rect2i(bounds.getX(), bounds.getY(), width, height);
    }

    @Override
    public Rect2i getBounds() {
        return bounds;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        background.dest(
                bounds.getX() + this.bounds.getX(),
                bounds.getY() + this.bounds.getY(),
                this.bounds.getWidth(),
                this.bounds.getHeight()).blit(guiGraphics);
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        return new Tooltip(
                this.toolbeltName,
                GuiText.UpgradeToolbelt.text().plainCopy().withStyle(ChatFormatting.GRAY));
    }

}
