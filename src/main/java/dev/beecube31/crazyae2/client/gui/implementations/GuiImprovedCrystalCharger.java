package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.CrazyProgressBar;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerBigCrystalCharger;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiImprovedCrystalCharger extends GuiCrazyUpgradeable<ContainerBigCrystalCharger> {

    private final CrazyProgressBar pb;

    public GuiImprovedCrystalCharger(ContainerBigCrystalCharger menu, Inventory playerInventory, Component title,
                                     ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new CrazyProgressBar(this.menu, StateSprite.PROGRESS_BAR_FILLED.getBlitter(), CrazyProgressBar.Direction.VERTICAL, this.getGuiHue());
        widgets.add("progressBar", this.pb);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        double progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));
    }
}
