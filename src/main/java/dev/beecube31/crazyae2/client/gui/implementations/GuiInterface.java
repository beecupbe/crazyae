package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyServerSettingToggleButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerPerfectInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiInterface<C extends ContainerPerfectInterface> extends GuiCrazyUpgradeable<C> {

    private final CrazyServerSettingToggleButton<FuzzyMode> fuzzyMode;

    public GuiInterface(C menu, Inventory playerInventory, Component title,
                        ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.fuzzyMode = new CrazyServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL, this.getGuiHue());
        addToLeftToolbar(this.fuzzyMode);

        widgets.addOpenPriorityButton();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));
    }
}
