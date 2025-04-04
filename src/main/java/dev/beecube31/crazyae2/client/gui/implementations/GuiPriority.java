package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.core.localization.GuiText;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.BasicButton;
import dev.beecube31.crazyae2.client.gui.widgets.NumberTextField;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerPriority;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketUptadeTextField;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class GuiPriority extends CrazyAEBaseGui {

    private NumberTextField priority;
    private OptionSideButton backBtn;

    private BasicButton plus1;
    private BasicButton plus10;
    private BasicButton plus100;
    private BasicButton plus1000;
    private BasicButton minus1;
    private BasicButton minus10;
    private BasicButton minus100;
    private BasicButton minus1000;

    private Object origGui;

    public GuiPriority(final InventoryPlayer inventoryPlayer, final IPriorityHost te) {
        super(new ContainerPriority(inventoryPlayer, te));
        this.xSize += 26;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int a = AEConfig.instance().priorityByStacksAmounts(0);
        final int b = AEConfig.instance().priorityByStacksAmounts(1);
        final int c = AEConfig.instance().priorityByStacksAmounts(2);
        final int d = AEConfig.instance().priorityByStacksAmounts(3);

        this.buttonList.add(this.plus1 = new BasicButton(0, this.guiLeft + 20, this.guiTop + 32, 22, 20, "+" + a, this.getGuiHue()));
        this.buttonList.add(this.plus10 = new BasicButton(0, this.guiLeft + 48, this.guiTop + 32, 28, 20, "+" + b, this.getGuiHue()));
        this.buttonList.add(this.plus100 = new BasicButton(0, this.guiLeft + 82, this.guiTop + 32, 32, 20, "+" + c, this.getGuiHue()));
        this.buttonList.add(this.plus1000 = new BasicButton(0, this.guiLeft + 120, this.guiTop + 32, 38, 20, "+" + d, this.getGuiHue()));

        this.buttonList.add(this.minus1 = new BasicButton(0, this.guiLeft + 20, this.guiTop + 69, 22, 20, "-" + a, this.getGuiHue()));
        this.buttonList.add(this.minus10 = new BasicButton(0, this.guiLeft + 48, this.guiTop + 69, 28, 20, "-" + b, this.getGuiHue()));
        this.buttonList.add(this.minus100 = new BasicButton(0, this.guiLeft + 82, this.guiTop + 69, 32, 20, "-" + c, this.getGuiHue()));
        this.buttonList.add(this.minus1000 = new BasicButton(0, this.guiLeft + 120, this.guiTop + 69, 38, 20, "-" + d, this.getGuiHue()));

        final ContainerPriority con = ((ContainerPriority) this.inventorySlots);
        final ItemStack myIcon = con.getPriorityHost().getItemStackRepresentation();
        this.origGui = con.getPriorityHost() instanceof IPriHostGuiOverrider r
                ? r.getOverrideGui() : con.getPriorityHost().getGuiBridge();

        if (this.origGui != null && !myIcon.isEmpty()) {
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

        this.priority = new NumberTextField(this.fontRenderer, this.guiLeft + 62, this.guiTop + 57, 59, this.fontRenderer.FONT_HEIGHT, Long.class, 0);
        this.priority.setEnableBackgroundDrawing(false);
        this.priority.setMaxStringLength(16);
        this.priority.setTextColor(0xFFFFFF);
        this.priority.setVisible(true);
        this.priority.setFocused(true);
        ((ContainerPriority) this.inventorySlots).setTextField(this.priority);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawString(GuiText.Priority.getLocal(), 8, 6);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture("guis/priority.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

        this.priority.drawTextBox();
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        if (btn == this.backBtn) {
            if (this.origGui instanceof CrazyAEGuiBridge) {
                NetworkHandler.instance().sendToServer(new PacketSwitchGuis(this.origGui));
            } else {
                appeng.core.sync.network.NetworkHandler.instance().sendToServer(new appeng.core.sync.packets.PacketSwitchGuis((GuiBridge) this.origGui));
            }
        }

        final boolean isPlus = btn == this.plus1 || btn == this.plus10 || btn == this.plus100 || btn == this.plus1000;
        final boolean isMinus = btn == this.minus1 || btn == this.minus10 || btn == this.minus100 || btn == this.minus1000;

        if (isPlus || isMinus) {
            this.addQty(this.getQty(btn));
        }
    }

    private void addQty(final int i) {
        try {
            String out = this.priority.getText();

            boolean fixed = false;
            while (out.startsWith("0") && out.length() > 1) {
                out = out.substring(1);
                fixed = true;
            }

            if (fixed) {
                this.priority.setText(out);
            }

            if (out.isEmpty()) {
                out = "0";
            }

            long result = Long.parseLong(out);
            result += i;

            this.priority.setText(out = Long.toString(result));

            NetworkHandler.instance().sendToServer(new PacketUptadeTextField("PriorityHost.Priority", out));
        } catch (final NumberFormatException e) {
            this.priority.setText("0");
        } catch (final IOException e) {
            AELog.debug(e);
        }
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        if (!this.checkHotbarKeys(key)) {
            if ((key == 211 || key == 205 || key == 203 || key == 14 || character == '-' || Character.isDigit(character)) && this.priority
                    .textboxKeyTyped(character, key)) {
                try {
                    String out = this.priority.getText();

                    boolean fixed = false;
                    while (out.startsWith("0") && out.length() > 1) {
                        out = out.substring(1);
                        fixed = true;
                    }

                    if (fixed) {
                        this.priority.setText(out);
                    }

                    if (out.isEmpty()) {
                        out = "0";
                    }

                    NetworkHandler.instance().sendToServer(new PacketUptadeTextField("PriorityHost.Priority", out));
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
