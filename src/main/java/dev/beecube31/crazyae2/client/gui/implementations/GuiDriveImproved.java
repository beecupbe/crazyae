package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerDriveImproved;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiDriveImproved extends CrazyAEBaseGui {

    private OptionSideButton priority;

    public GuiDriveImproved(final InventoryPlayer inventoryPlayer, final TileImprovedDrive te) {
        super(new ContainerDriveImproved(inventoryPlayer, te));
        this.ySize = 199;
        this.xSize = 176 + 26;
    }

    @Override
    protected void actionPerformed(final GuiButton par1GuiButton) throws IOException {
        super.actionPerformed(par1GuiButton);
        if (par1GuiButton == this.priority) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.GUI_PRIORITY));
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(this.priority = new OptionSideButton(
                this.guiLeft + 174,
                this.guiTop,
                StateSprite.QUARTZ_WRENCH,
                GuiText.Priority.getLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.DEFAULT
        ));
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.drawString(this.getGuiDisplayName(CrazyAEGuiText.IMPROVED_DRIVE_GUI.getLocal()), 6, 6);
        this.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture("guis/driveimp.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }
}
