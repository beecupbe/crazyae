package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.client.gui.widgets.ProgressBar;
import dev.beecube31.crazyae2.common.containers.ContainerBigCrystalCharger;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBigCrystalCharger extends GuiCrazyAEUpgradeable {

    private final ContainerBigCrystalCharger container;
    private ProgressBar pb;

    private static final String texture = "guis/big_crystal_charger-v2.png";

    public GuiBigCrystalCharger(final InventoryPlayer inventoryPlayer, final TileBigCrystalCharger te) {
        super(new ContainerBigCrystalCharger(inventoryPlayer, te));
        this.ySize = 197;
        this.container = (ContainerBigCrystalCharger)this.inventorySlots;
        this.setDisableDrawInventoryString(true);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pb = new ProgressBar(
                this.container,
                80 + this.guiLeft,
                54 + this.guiTop,
                16,
                18,
                ProgressBar.Direction.VERTICAL,
                CrazyAEGuiTooltip.PROGRESS.getLocal(),
                0,
                this.getGuiHue(),
                Sprite.PROGRESS_BAR_FILLED,
                null,
                false
        );

        this.buttonList.add(this.pb);
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
        return texture;
    }
}
