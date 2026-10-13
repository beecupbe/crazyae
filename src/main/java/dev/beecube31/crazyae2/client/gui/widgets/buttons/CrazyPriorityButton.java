package dev.beecube31.crazyae2.client.gui.widgets.buttons;

import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.common.network.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public final class CrazyPriorityButton {
    public static final int OFFSET_X = 174;
    public static final int OFFSET_Y = 0;

    private CrazyPriorityButton() {}

    public static OptionSideButton create(Supplier<ColorContainer> hue, Component name, Runnable action, Object icon) {
        return new OptionSideButton(icon, name,
                OptionSideButton.ButtonType.DEFAULT, hue, action);
    }

    public static OptionSideButton create(Supplier<ColorContainer> hue, Component name, Runnable action) {
        return create(hue, name, action, StateSprite.QUARTZ_WRENCH);
    }

    public static OptionSideButton create(Supplier<ColorContainer> hue, Component name) {
        return create(hue, name, CrazyPriorityButton::openPriority, StateSprite.QUARTZ_WRENCH);
    }

    public static OptionSideButton create(Supplier<ColorContainer> hue) {
        return create(hue, GuiText.Priority.text(), CrazyPriorityButton::openPriority, StateSprite.QUARTZ_WRENCH);
    }

    public static void openPriority() {
        CrazyNetworkHandler.instance().sendToServer(PacketSwitchGuis.openSubMenu(CrazyContainers.PRIORITY.get()));
    }
}
