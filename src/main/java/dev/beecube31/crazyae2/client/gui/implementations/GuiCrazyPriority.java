package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import guideme.PageAnchor;
import net.neoforged.neoforge.network.PacketDistributor;
import appeng.core.network.serverbound.SwitchGuisPacket;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.NumbersEntryWidget;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerCrazyPriority;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class GuiCrazyPriority extends CrazyAEBaseGui<ContainerCrazyPriority> {

    private final NumbersEntryWidget priority;

    public GuiCrazyPriority(ContainerCrazyPriority menu, Inventory playerInventory, Component title,
                            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.priority = widgets.addNumberEntryWidget("priority", NumberEntryType.UNITLESS, this.getGuiHue(), this.getTextHue());
        this.priority.setTextFieldStyle(style.getWidget("priorityInput"));
        this.priority.setMinValue(Integer.MIN_VALUE);
        this.priority.setLongValue(this.menu.getPriorityValue());
        this.priority.setOnChange(this::savePriority);
        this.priority.setOnConfirm(() -> {
            savePriority();
            AESubScreen.goBack();
        });
    }

    @Override
    public void init() {
        super.init();

        // Back button
        OptionSideButton priBtn = CrazyPriorityButton.create(this::getGuiHue, menu.getHost().getMainMenuIcon().getHoverName(),
                GuiCrazyPriority::goBack, menu.getHost().getMainMenuIcon());

        priBtn.setPosition(this.leftPos + CrazyPriorityButton.OFFSET_X, this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(priBtn);
    }

    public static void goBack() {
        PacketDistributor.sendToServer(SwitchGuisPacket.returnToParentMenu());
    }

    private void savePriority() {
        OptionalInt priority = this.priority.getIntValue();
        if (priority.isPresent()) {
            menu.setPriority(priority.getAsInt());
        }
    }

    @Override
    protected @Nullable PageAnchor getHelpTopic() {
        var topic = super.getHelpTopic();
        return topic != null && topic.anchor() == null ? new PageAnchor(topic.pageId(), "priority") : null;
    }
}
