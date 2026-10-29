package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.CrazyToolboxPanel;
import dev.beecube31.crazyae2.client.gui.widgets.CrazyUpgradesPanel;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerCrazyUpgradeable;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import dev.beecube31.crazyae2.common.tile.networking.TilePerfectInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiCrazyUpgradeable<T extends ContainerCrazyUpgradeable<?>> extends CrazyAEBaseGui<T> {

    public GuiCrazyUpgradeable(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("upgrades", new CrazyUpgradesPanel(
                menu.getSlots(SlotSemantics.UPGRADE),
                this::getCompatibleUpgrades,
                this.getGuiHue()));
        if (menu.getToolbox().isPresent()) {
            this.widgets.add("toolbox", new CrazyToolboxPanel(style, menu.getToolbox().getName(), this.getGuiHue()));
        }
    }

    private List<Component> getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(menu.getUpgrades().getUpgradableItem()));
        return list;
    }

}
