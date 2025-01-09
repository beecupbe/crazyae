package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.ProgressBar;
import dev.beecube31.crazyae2.common.containers.ContainerImprovedCondenser;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketConfigButton;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiImprovedCondenser extends CrazyAEBaseGui {

    private final ContainerImprovedCondenser cvc;
    private ProgressBar pb;
    private GuiImgButton mode;

    public GuiImprovedCondenser(final InventoryPlayer inventoryPlayer, final TileImprovedCondenser te) {
        super(new ContainerImprovedCondenser(inventoryPlayer, te));
        this.cvc = (ContainerImprovedCondenser) this.inventorySlots;
        this.ySize = 197;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (this.mode == btn) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(Settings.CONDENSER_OUTPUT, backwards));
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pb = new ProgressBar(
                this.cvc,
                152 + this.guiLeft,
                12 + this.guiTop,
                6,
                18,
                ProgressBar.Direction.VERTICAL,
                GuiText.StoredEnergy.getLocal(),
                0,
                this.getGuiHue()
        );

        this.mode = new GuiImgButton(114 + this.guiLeft, 13 + this.guiTop, Settings.CONDENSER_OUTPUT, this.cvc.getOutput());

        this.buttonList.add(this.pb);
        this.buttonList.add(this.mode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawString(this.getGuiDisplayName(GuiText.Condenser.getLocal()), 8, 4);
        this.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3);
        this.getTextHue().endDrawHue();

        this.mode.set(this.cvc.getOutput());
        this.mode.setFillVar(String.valueOf(this.cvc.getOutput().requiredPower));
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture("guis/improved_condenser.png");

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }
}
