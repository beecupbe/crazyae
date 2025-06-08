package dev.beecube31.crazyae2.client.gui.implementations;


import dev.beecube31.crazyae2.common.containers.ContainerTrashcanMana;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanMana;
import net.minecraft.entity.player.InventoryPlayer;


public class GuiTrashcanMana extends GuiCrazyAEUpgradeable {

    public GuiTrashcanMana(final InventoryPlayer inventoryPlayer, final TileTrashcanMana te) {
        super(new ContainerTrashcanMana(inventoryPlayer, te));
        this.ySize = 256;
        this.setDisableDrawInventoryString(true);
    }

    @Override
    public void initGui() {
        super.initGui();
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
        return "guis/trashcan_empty.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.TRASHCAN;
    }
}
