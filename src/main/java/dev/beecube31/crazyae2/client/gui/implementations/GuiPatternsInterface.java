package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.CrazyGuiColors;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerPatternsInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiPatternsInterface extends GuiPatternProvider<ContainerPatternsInterface> implements ICrazyGui {
    public GuiPatternsInterface(ContainerPatternsInterface menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();
        replacePriorityButton();
    }

    private void replacePriorityButton() {
        for (var child : List.copyOf(this.children())) {
            if (child instanceof appeng.client.gui.widgets.TabButton) {
                this.removeWidget(child);
            }
        }
        var priority = CrazyPriorityButton
                .create(CrazyGuiColors::gui);
        priority.setPosition(this.leftPos + CrazyPriorityButton.OFFSET_X,
                this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(priority);
    }
}
