package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.*;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyServerSettingToggleButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerIOBus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiIOBus extends GuiCrazyUpgradeable<ContainerIOBus> {

    private final CrazyServerSettingToggleButton<RedstoneMode> redstoneMode;
    private final CrazyServerSettingToggleButton<FuzzyMode> fuzzyMode;
    private final CrazyServerSettingToggleButton<YesNo> craftMode;
    private final CrazyServerSettingToggleButton<SchedulingMode> schedulingMode;

    public GuiIOBus(ContainerIOBus menu, Inventory playerInventory, Component title,
                    ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new CrazyServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
        addToLeftToolbar(this.redstoneMode);
        this.fuzzyMode = new CrazyServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
        addToLeftToolbar(this.fuzzyMode);

        if (menu.getHost().getConfigManager().hasSetting(Settings.CRAFT_ONLY)) {
            this.craftMode = new CrazyServerSettingToggleButton<>(Settings.CRAFT_ONLY, YesNo.NO, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
            addToLeftToolbar(this.craftMode);
        } else {
            this.craftMode = null;
        }

        if (menu.getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new CrazyServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
            addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));
        if (this.craftMode != null) {
            this.craftMode.set(menu.getCraftingMode());
            this.craftMode.setVisibility(menu.hasUpgrade(AEItems.CRAFTING_CARD));
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.set(menu.getSchedulingMode());
        }
    }

}
