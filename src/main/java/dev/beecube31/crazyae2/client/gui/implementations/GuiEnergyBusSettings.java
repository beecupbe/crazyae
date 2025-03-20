package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.core.AELog;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.BasicButton;
import dev.beecube31.crazyae2.client.gui.widgets.NumberTextField;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerEnergyBusSettings;
import dev.beecube31.crazyae2.common.interfaces.IEnergyBus;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketUptadeTextField;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class GuiEnergyBusSettings extends CrazyAEBaseGui {

    private NumberTextField energy;
    private OptionSideButton backBtn;

    private BasicButton plus1;
    private BasicButton plus10;
    private BasicButton plus100;
    private BasicButton plus1000;
    private BasicButton minus1;
    private BasicButton minus10;
    private BasicButton minus100;
    private BasicButton minus1000;

    public GuiEnergyBusSettings(final InventoryPlayer inventoryPlayer, final IEnergyBus te) {
        super(new ContainerEnergyBusSettings(inventoryPlayer, te));
        this.xSize += 26;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(this.plus1 = new BasicButton(0, this.guiLeft + 20, this.guiTop + 32, 22, 20, "+1", this.getGuiHue()));
        this.buttonList.add(this.plus10 = new BasicButton(0, this.guiLeft + 48, this.guiTop + 32, 28, 20, "+10", this.getGuiHue()));
        this.buttonList.add(this.plus100 = new BasicButton(0, this.guiLeft + 82, this.guiTop + 32, 32, 20, "+100", this.getGuiHue()));
        this.buttonList.add(this.plus1000 = new BasicButton(0, this.guiLeft + 120, this.guiTop + 32, 38, 20, "+1000", this.getGuiHue()));

        this.buttonList.add(this.minus1 = new BasicButton(0, this.guiLeft + 20, this.guiTop + 69, 22, 20, "-1", this.getGuiHue()));
        this.buttonList.add(this.minus10 = new BasicButton(0, this.guiLeft + 48, this.guiTop + 69, 28, 20, "-10", this.getGuiHue()));
        this.buttonList.add(this.minus100 = new BasicButton(0, this.guiLeft + 82, this.guiTop + 69, 32, 20, "-100", this.getGuiHue()));
        this.buttonList.add(this.minus1000 = new BasicButton(0, this.guiLeft + 120, this.guiTop + 69, 38, 20, "-1000", this.getGuiHue()));

        final ContainerEnergyBusSettings con = ((ContainerEnergyBusSettings) this.inventorySlots);
        final ItemStack myIcon = con.getHost().getItemStackRepresentation();

        if (!myIcon.isEmpty()) {
            this.buttonList.add(
                    this.backBtn = new OptionSideButton(
                            this.guiLeft + 174,
                            this.guiTop,
                            myIcon,
                            myIcon.getDisplayName(),
                            "",
                            this.itemRender,
                            this.getGuiHue(),
                            this.getTextHue(),
                            0,
                            OptionSideButton.ButtonType.DEFAULT
                    )
            );
        }

        this.energy = new NumberTextField(this.fontRenderer, this.guiLeft + 62, this.guiTop + 57, 59, this.fontRenderer.FONT_HEIGHT, Long.class, 0);
        this.energy.setEnableBackgroundDrawing(false);
        this.energy.setMaxStringLength(16);
        this.energy.setTextColor(0xFFFFFF);
        this.energy.setVisible(true);
        this.energy.setFocused(true);
        ((ContainerEnergyBusSettings) this.inventorySlots).setTextField(this.energy);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawString(CrazyAEGuiText.ENERGY_SETTINGS_PAGE.getLocal(), 8, 6);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(this.getBackground());
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

        this.energy.drawTextBox();
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        if (btn == this.backBtn) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.ENERGY_BUS));
        }

        final boolean isPlus = btn == this.plus1 || btn == this.plus10 || btn == this.plus100 || btn == this.plus1000;
        final boolean isMinus = btn == this.minus1 || btn == this.minus10 || btn == this.minus100 || btn == this.minus1000;

        if (isPlus || isMinus) {
            this.addQty(this.getQty(btn));
        }
    }

    private void addQty(final int i) {
        try {
            String out = this.energy.getText();

            boolean fixed = false;
            while (out.startsWith("0") && out.length() > 1) {
                out = out.substring(1);
                fixed = true;
            }

            if (fixed) {
                this.energy.setText(out);
            }

            if (out.isEmpty()) {
                out = "0";
            }

            long result = Long.parseLong(out);
            result += i;

            this.energy.setText(out = Long.toString(result));

            NetworkHandler.instance().sendToServer(new PacketUptadeTextField("EnergyHost.Config", out));
        } catch (final NumberFormatException e) {
            this.energy.setText("0");
        } catch (final IOException e) {
            AELog.debug(e);
        }
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        if (!this.checkHotbarKeys(key)) {
            if ((key == 211 || key == 205 || key == 203 || key == 14 || character == '-' || Character.isDigit(character)) && this.energy
                    .textboxKeyTyped(character, key)) {
                try {
                    String out = this.energy.getText();

                    boolean fixed = false;
                    while (out.startsWith("0") && out.length() > 1) {
                        out = out.substring(1);
                        fixed = true;
                    }

                    if (fixed) {
                        this.energy.setText(out);
                    }

                    if (out.isEmpty()) {
                        out = "0";
                    }

                    NetworkHandler.instance().sendToServer(new PacketUptadeTextField("EnergyHost.Config", out));
                } catch (final IOException e) {
                    AELog.debug(e);
                }
            } else {
                super.keyTyped(character, key);
            }
        }
    }

    protected String getBackground() {
        return "guis/priority.png";
    }
}
