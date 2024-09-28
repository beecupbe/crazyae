package dev.beecube31.crazyae2.client.gui.implementations;


import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import dev.beecube31.crazyae2.common.containers.ContainerFastMAC;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import net.minecraft.entity.player.InventoryPlayer;


public class GuiFastMAC extends GuiCrazyAEUpgradeable {

    private final ContainerFastMAC container;

    public GuiFastMAC(final InventoryPlayer inventoryPlayer, final TileImprovedMAC te) {
        super(new ContainerFastMAC(inventoryPlayer, te));
        this.ySize = 197;
        this.container = (ContainerFastMAC) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void addButtons() {
        this.redstoneMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.buttonList.add(this.redstoneMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.fontRenderer.drawString(this.getGuiDisplayName(this.getName().getLocal()), 8, 6, 4210752);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
    }

    @Override
    protected String getBackground() {
        return "guis/fastmac.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.IMPROVED_MAC_GUI;
    }
}
