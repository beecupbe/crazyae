package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyServerSettingToggleButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerImprovedCondenser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiImprovedCondenser extends CrazyAEBaseGui<ContainerImprovedCondenser> implements ICrazyGui {
    private final CrazyServerSettingToggleButton<CondenserOutput> mode;

    public GuiImprovedCondenser(ContainerImprovedCondenser menu, Inventory playerInventory, Component title,
                                ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.mode = new CrazyServerSettingToggleButton<>(Settings.CONDENSER_OUTPUT, this.menu.getOutput(), this.getGuiHue());
        widgets.add("mode", this.mode);
        widgets.add("progressBar", new ProgressBar(this.menu, style.getImage("progressBar"),
                ProgressBar.Direction.VERTICAL, GuiText.StoredEnergy.text()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.mode.set(this.menu.getOutput());
    }
}
