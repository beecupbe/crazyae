package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.CrazyGuiColors;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.ColorSlider;
import dev.beecube31.crazyae2.client.gui.widgets.CrazyTextField;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.HoverImageButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerColorizer;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerColorizer.ColorizerType;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.util.ColorUtils;
import dev.beecube31.crazyae2.core.config.CrazyAEClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class GuiColorizer extends CrazyAEBaseGui<ContainerColorizer> {
    private ColorizerType type = ColorizerType.GUI;

    private ColorSlider sliderR;
    private ColorSlider sliderG;
    private ColorSlider sliderB;
    private CrazyTextField hexField;
    private OptionSideButton typeButton;
    private boolean updatingHex;
    private boolean closeRequested;

    public GuiColorizer(ContainerColorizer menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        if (!CrazyGuiColors.isEnabled()) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(CrazyAEGuiText.COLORIZING_DISABLED.getComponent(), false);
            }
            this.closeRequested = true;
            return;
        }

        int x = this.leftPos;
        int y = this.topPos;

        this.sliderR = addRenderableWidget(new ColorSlider(x + 56, y + 25, 114, 0, 255, this::getGuiHue,
                this::onInteractionUpdate).disableColorizing(true));
        this.sliderG = addRenderableWidget(new ColorSlider(x + 56, y + 56, 114, 0, 255, this::getGuiHue,
                this::onInteractionUpdate).disableColorizing(true));
        this.sliderB = addRenderableWidget(new ColorSlider(x + 56, y + 87, 114, 0, 255, this::getGuiHue,
                this::onInteractionUpdate).disableColorizing(true));

        this.typeButton = new OptionSideButton(StateSprite.ABC, this.type.toLocal(),
                OptionSideButton.ButtonType.NO_BOTTOM, this::getGuiHue, this::switchType);
        this.typeButton.setPosition(x + 174, y);
        addRenderableWidget(this.typeButton);

        var restore = new OptionSideButton(StateSprite.RESTORE_DEFAULTS,
                CrazyAEGuiText.RESTORE_DEFAULTS.getComponent(), OptionSideButton.ButtonType.NO_TOP,
                this::getGuiHue, this::restoreDefaults);
        restore.setPosition(x + 174, y + StateSprite.OPTION_SIDE_BUTTON.getSizeY());
        addRenderableWidget(restore);

        this.hexField = new CrazyTextField(this.font, x + 52, y + 107, 51, 12, this::getGuiHue)
                .setReserveCursorSpace(false);
        this.hexField.setBordered(false);
        this.hexField.setMaxLength(7);
        this.hexField.setTextColor(0xFFFFFF);
        this.hexField.setFilter(s -> s.matches("#?[0-9a-fA-F]{0,6}"));
        this.hexField.setResponder(this::onHexChanged);
        addRenderableWidget(this.hexField);
        this.setInitialFocus(this.hexField);

        addRenderableWidget(new HoverImageButton(x + 106, y + 107, 12, 12, StateSprite.COPY_TO_CLIPBOARD_SMALL,
                CrazyAEGuiText.COPY_TO_CLIPBOARD.getComponent(), this::getGuiHue, this::copyToClipboard));
        addRenderableWidget(new HoverImageButton(x + 120, y + 107, 12, 12, StateSprite.PASTE_FROM_CLIPBOARD_SMALL,
                CrazyAEGuiText.PASTE_FROM_CLIPBOARD.getComponent(), this::getGuiHue, this::pasteFromClipboard));

        this.loadValues();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.closeRequested) {
            this.closeRequested = false;
            this.onClose();
        }
    }

    private void loadValues() {
        var cfg = CrazyAEClientConfig.CLIENT;
        if (this.type == ColorizerType.GUI) {
            this.sliderR.setValue(cfg.colorizerColorRed.get());
            this.sliderG.setValue(cfg.colorizerColorGreen.get());
            this.sliderB.setValue(cfg.colorizerColorBlue.get());
        } else {
            this.sliderR.setValue(cfg.colorizerTextColorRed.get());
            this.sliderG.setValue(cfg.colorizerTextColorGreen.get());
            this.sliderB.setValue(cfg.colorizerTextColorBlue.get());
        }
        this.typeButton.setIcon(this.type == ColorizerType.GUI ? StateSprite.ABC : StateSprite.GUI);
        this.typeButton.setTooltipText(this.type.toLocal());
        this.onInteractionUpdate();
    }

    private void saveValues() {
        if (this.sliderR == null) {
            return;
        }
        int r = this.sliderR.getIntValue();
        int g = this.sliderG.getIntValue();
        int b = this.sliderB.getIntValue();
        if (this.type == ColorizerType.GUI) {
            CrazyAEClientConfig.CLIENT.setGuiColorizerColors(r, g, b);
        } else {
            CrazyAEClientConfig.CLIENT.setTextColorizerColors(r, g, b);
        }
        CrazyAEClientConfig.CLIENT_SPEC.save();
    }

    private void onInteractionUpdate() {
        this.applyHue();
        this.updateTextField();
    }

    private void applyHue() {
        float r = this.sliderR.getIntValue() / 255F;
        float g = this.sliderG.getIntValue() / 255F;
        float b = this.sliderB.getIntValue() / 255F;
        if (this.type == ColorizerType.GUI) {
            this.getGuiHue().setParams(r, g, b, 1F);
        } else {
            this.getTextHue().setParams(r, g, b, 1F);
        }
    }

    private void updateTextField() {
        String hex = ColorUtils.getHexFromRGB(this.sliderR.getIntValue(), this.sliderG.getIntValue(),
                this.sliderB.getIntValue());
        if (hex != null && !hex.equals(this.hexField.getValue())) {
            this.updatingHex = true;
            this.hexField.setValue(hex);
            this.updatingHex = false;
        }
    }

    private void onHexChanged(String text) {
        if (this.updatingHex) {
            return;
        }

        Color color = ColorUtils.colorFromHex(text);
        if (color != null) {
            this.sliderR.setValue(color.getRed());
            this.sliderG.setValue(color.getGreen());
            this.sliderB.setValue(color.getBlue());
            this.applyHue();
        }
    }

    private void switchType() {
        this.saveValues();
        this.type = this.type.other();
        this.loadValues();
    }

    private void restoreDefaults() {
        float v = this.type == ColorizerType.GUI ? 255 : 0;
        this.sliderR.setValue(v);
        this.sliderG.setValue(v);
        this.sliderB.setValue(v);
        this.onInteractionUpdate();
    }

    private void copyToClipboard() {
        if (this.minecraft != null) {
            this.minecraft.keyboardHandler.setClipboard(this.hexField.getValue());
        }
    }

    private void pasteFromClipboard() {
        if (this.minecraft == null) {
            return;
        }
        String text = this.minecraft.keyboardHandler.getClipboard().trim();
        if (ColorUtils.colorFromHex(text) != null) {
            if (!text.startsWith("#")) {
                text = "#" + text;
            }
            this.hexField.setValue(text.toUpperCase());
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (var slider : new ColorSlider[] { this.sliderR, this.sliderG, this.sliderB }) {
            if (slider != null && slider.isDragging()) {
                slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (var slider : new ColorSlider[] { this.sliderR, this.sliderG, this.sliderB }) {
            if (slider != null && slider.isDragging()) {
                slider.onRelease(mouseX, mouseY);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.hexField != null && this.hexField.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            this.hexField.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void drawBG(GuiGraphics g, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(g, offsetX, offsetY, mouseX, mouseY, partialTicks);

        g.fill(offsetX + 13, offsetY + 19, offsetX + 37, offsetY + 43, 0xFFFF0000);
        g.fill(offsetX + 13, offsetY + 50, offsetX + 37, offsetY + 74, 0xFF00FF00);
        g.fill(offsetX + 13, offsetY + 81, offsetX + 37, offsetY + 105, 0xFF0000FF);
    }

    @Override
    public void removed() {
        super.removed();
        this.saveValues();
    }
}
