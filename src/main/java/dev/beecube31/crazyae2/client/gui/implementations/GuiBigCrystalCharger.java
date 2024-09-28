package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.client.gui.widgets.ProgressBar;
import dev.beecube31.crazyae2.common.containers.ContainerBigCrystalCharger;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBigCrystalCharger extends GuiCrazyAEUpgradeable {

    private final ContainerBigCrystalCharger container;
    private ProgressBar pb;

    private static final String texture = "guis/big_crystal_charger.png";

    public GuiBigCrystalCharger(final InventoryPlayer inventoryPlayer, final TileBigCrystalCharger te) {
        super(new ContainerBigCrystalCharger(inventoryPlayer, te));
        this.ySize = 197;
        this.container = (ContainerBigCrystalCharger)this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pb = new ProgressBar(this.container, texture, 80 + this.guiLeft, 54 + this.guiTop, 230, 54, 16, 18, ProgressBar.Direction.VERTICAL, CrazyAEGuiTooltip.PROGRESS.getLocal());
        this.buttonList.add(this.pb);
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
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.BIG_CRYSTAL_CHARGER;
    }

    @Override
    protected String getBackground() {
        return "guis/big_crystal_charger.png";
    }
}
