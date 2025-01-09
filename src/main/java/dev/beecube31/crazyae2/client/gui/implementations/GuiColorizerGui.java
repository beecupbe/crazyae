package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.HexTextField;
import dev.beecube31.crazyae2.client.gui.widgets.HoverImageButton;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.Slider;
import dev.beecube31.crazyae2.common.containers.ContainerColorizerGui;
import dev.beecube31.crazyae2.common.enums.ColorizerType;
import dev.beecube31.crazyae2.common.interfaces.gui.IGuiElementsCallbackHandler;
import dev.beecube31.crazyae2.common.items.ColorizerObj;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketUptadeTextField;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.util.ColorUtils;
import dev.beecube31.crazyae2.common.util.SystemUtils;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.client.CrazyAEClientConfig;
import dev.beecube31.crazyae2.core.client.CrazyAEClientState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.io.IOException;

public class GuiColorizerGui extends CrazyAEBaseGui implements IGuiElementsCallbackHandler {

    private static final String texture = "guis/gui_colorizer.png";

    private OptionSideButton colorizerTypeBtn;
    private OptionSideButton restoreDefaults;
    private HoverImageButton copyToClipboard;
    private HoverImageButton pasteFromClipboard;
    private HexTextField textField;

    private Slider sliderR;
    private Slider sliderG;
    private Slider sliderB;

    private final ContainerColorizerGui container;

    protected final ColorizerType type;

    public GuiColorizerGui(final InventoryPlayer inventoryPlayer, final ColorizerObj te) {
        super(new ContainerColorizerGui(inventoryPlayer, te));
        this.container = (ContainerColorizerGui) this.inventorySlots;
        this.ySize = 128;
        this.xSize += 26;
        this.type = ColorizerType.GUI;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        if (btn == this.restoreDefaults) {
            this.restoreDefaults();
        }

        if (btn == colorizerTypeBtn) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(this.type.toGUI()));
        }

        if (btn == this.copyToClipboard) {
            SystemUtils.copyToClipboard(this.textField.getText());
        }

        if (btn == this.pasteFromClipboard) {
            String toPaste = SystemUtils.pasteFromClipboard();

            if (toPaste != null && ColorUtils.getRGBFromHex(toPaste) != null) {
                this.textField.setText("");
                this.textField.writeText(toPaste);
                NetworkHandler.instance().sendToServer(new PacketUptadeTextField("Colorizer.Text", toPaste));
            }
        }
    }

    private void restoreDefaults() {
        this.sliderR.setScroll(255);
        this.sliderG.setScroll(255);
        this.sliderB.setScroll(255);
        this.onInteractionUpdate();
    }

    @Override
    public void initGui() {
        super.initGui();

        if (!CrazyAEClientConfig.isColorizingEnabled()) {
            this.mc.player.sendStatusMessage(new TextComponentString(CrazyAEGuiText.COLORIZING_DISABLED.getLocal()), false);
            mc.player.closeScreen();
            return;
        }

        (this.sliderR = new Slider(
                56,
                25,
                114,
                0,
                255,
                this.getGuiHue(),
                this)
        ).setScroll(CrazyAEClientConfig.getColorizerColorRed());
        (this.sliderG = new Slider(
                56,
                56,
                114,
                0,
                255,
                this.getGuiHue(),
                this)
        ).setScroll(CrazyAEClientConfig.getColorizerColorGreen());
        (this.sliderB = new Slider(
                56,
                87,
                114,
                0,
                255,
                this.getGuiHue(),
                this)
        ).setScroll(CrazyAEClientConfig.getColorizerColorBlue());

        this.registerScrollSrc(this.sliderR);
        this.registerScrollSrc(this.sliderG);
        this.registerScrollSrc(this.sliderB);

        this.buttonList.add(this.colorizerTypeBtn = new OptionSideButton(
                this.guiLeft + 174,
                this.guiTop,
                StateSprite.ABC,
                this.type.toLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.NO_BOTTOM
        ));

        this.buttonList.add(this.restoreDefaults = new OptionSideButton(
                this.guiLeft + 174,
                this.guiTop + StateSprite.OPTION_SIDE_BUTTON.getSizeY(),
                StateSprite.RESTORE_DEFAULTS,
                CrazyAEGuiText.RESTORE_DEFAULTS.getLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.NO_TOP
        ));

        this.textField = new HexTextField(
                this.fontRenderer,
                this.guiLeft + 54,
                this.guiTop + 109,
                60,
                this.fontRenderer.FONT_HEIGHT,
                0,
                this.sliderR,
                this.sliderG,
                this.sliderB
        );
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setFocused(true);
        this.container.setTextField(this.textField);

        this.buttonList.add(this.copyToClipboard = new HoverImageButton(
                0,
                this.guiLeft + 106,
                this.guiTop + 107,
                12,
                12,
                StateSprite.COPY_TO_CLIPBOARD_SMALL,
                CrazyAEGuiText.COPY_TO_CLIPBOARD.getLocal(),
                this.getGuiHue()
        ));

        this.buttonList.add(this.pasteFromClipboard = new HoverImageButton(
                0,
                this.guiLeft + 120,
                this.guiTop + 107,
                12,
                12,
                StateSprite.PASTE_FROM_CLIPBOARD_SMALL,
                CrazyAEGuiText.PASTE_FROM_CLIPBOARD.getLocal(),
                this.getGuiHue()
        ));

        this.updateTextField();
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawString(this.getGuiDisplayName(CrazyAEGuiText.GUI_COLORIZER_GUI.getLocal()), 8, 6);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(texture);
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        this.textField.drawTextBox();

        drawRect(this.guiLeft + 13, this.guiTop + 19, this.guiLeft + 37, this.guiTop + 43, new Color(0xFF0000).getRGB());
        drawRect(this.guiLeft + 13, this.guiTop + 50, this.guiLeft + 37, this.guiTop + 74, new Color(0x00FF00).getRGB());
        drawRect(this.guiLeft + 13, this.guiTop + 81, this.guiLeft + 37, this.guiTop + 105, new Color(0x0000FF).getRGB());
    }

    @Override
    public void onInteractionStart() {}

    @Override
    public void onInteractionUpdate() {
        CrazyAEClientState.applyColorizerGui(
                (int) this.sliderR.getCurrentScroll(),
                (int) this.sliderG.getCurrentScroll(),
                (int) this.sliderB.getCurrentScroll(),
                this.getGuiHue()
        );

        this.updateTextField();
    }

    private void updateTextField() {
        try {
            String hex = ColorUtils.getHexFromRGB(
                    (int) this.sliderR.getCurrentScroll(),
                    (int) this.sliderG.getCurrentScroll(),
                    (int) this.sliderB.getCurrentScroll()
            );
            if (hex != null && !hex.isEmpty()) {
                this.textField.setText(hex);

                NetworkHandler.instance().sendToServer(new PacketUptadeTextField(
                        "Colorizer.Text",
                        ColorUtils.getHexFromRGB(
                                (int) this.sliderR.getCurrentScroll(),
                                (int) this.sliderG.getCurrentScroll(),
                                (int) this.sliderB.getCurrentScroll()
                        )
                ));
            }
        } catch (IOException e) {
            // :(
        }
    }

    @Override
    public void onInteractionEnd() {}

    @Override
    public CrazyAEBaseGui getCallbackHandler() {
        return this;
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        if (!this.checkHotbarKeys(key)) {
            if ((key == 211 || key == 205 || key == 203 || key == 14 || Character.toString(character).equals("#") || ColorUtils.isCharHex(character)) && this.textField.textboxKeyTyped(character, key)) {
                try {
                    String out = this.textField.getText();
                    NetworkHandler.instance().sendToServer(new PacketUptadeTextField("Colorizer.Text", out));
                } catch (final IOException e) {
                    CrazyAE.logger().debug(e);
                }
            } else {
                super.keyTyped(character, key);
            }
        }
    }
}
