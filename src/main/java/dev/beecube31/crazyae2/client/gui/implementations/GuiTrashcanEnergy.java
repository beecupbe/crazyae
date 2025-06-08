package dev.beecube31.crazyae2.client.gui.implementations;


import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import dev.beecube31.crazyae2.common.containers.ContainerTrashcanEnergy;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanBase;
import net.minecraft.entity.player.InventoryPlayer;


public class GuiTrashcanEnergy extends GuiCrazyAEUpgradeable {


    public GuiTrashcanEnergy(final InventoryPlayer inventoryPlayer, final TileTrashcanBase te) {
        super(new ContainerTrashcanEnergy(inventoryPlayer, te));
        this.ySize = 256;
        this.setDisableDrawInventoryString(true);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void addButtons() {
        this.fuzzyMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28, Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);

        this.buttonList.add(this.fuzzyMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        this.drawString(this.getGuiDisplayName(this.getName().getLocal()), 8, 6);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
    }

    @Override
    protected String getBackground() {
        return "guis/trashcan.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.TRASHCAN;
    }
}
