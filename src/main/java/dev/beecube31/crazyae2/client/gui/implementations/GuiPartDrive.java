package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerPartDrive;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.parts.implementations.PartDrive;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiPartDrive extends CrazyAEBaseGui {

    private OptionSideButton priority;

    public GuiPartDrive(final InventoryPlayer inventoryPlayer, final PartDrive te) {
        super(new ContainerPartDrive(inventoryPlayer, te));
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
        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.Drive.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture("guis/drive.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        this.getGuiHue().endDrawHue();
    }
}
