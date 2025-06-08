package dev.beecube31.crazyae2.client.gui.implementations;


import appeng.fluids.util.IAEFluidTank;
import dev.beecube31.crazyae2.client.gui.slot.AEFluidSlot;
import dev.beecube31.crazyae2.common.containers.ContainerTrashcanFluid;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanFluids;
import net.minecraft.entity.player.InventoryPlayer;


public class GuiTrashcanFluid extends GuiCrazyAEUpgradeable {

    private final TileTrashcanFluids te;

    public GuiTrashcanFluid(final InventoryPlayer inventoryPlayer, final TileTrashcanFluids te) {
        super(new ContainerTrashcanFluid(inventoryPlayer, te));
        this.ySize = 256;
        this.te = te;
        this.setDisableDrawInventoryString(true);
    }

    @Override
    public void initGui() {
        super.initGui();

        final IAEFluidTank inv = this.te.getConfig();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 9; x++) {
                this.guiSlots.add(new AEFluidSlot(inv, x + y * 9, x + y * 9, 8 + x * 18, 23 + y * 18));
            }
        }

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
