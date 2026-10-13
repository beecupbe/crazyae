package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerDriveImproved;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiImprovedDrive extends CrazyAEBaseGui<ContainerDriveImproved> {
    public GuiImprovedDrive(ContainerDriveImproved menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        OptionSideButton priority = CrazyPriorityButton.create(this::getGuiHue);
        priority.setPosition(this.leftPos + CrazyPriorityButton.OFFSET_X, this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(priority);
    }
}
