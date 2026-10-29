package dev.beecube31.crazyae2.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;

public abstract class WidgetBase extends AbstractButton {
    public WidgetBase(int p_93365_, int p_93366_, int p_93367_, int p_93368_, Component p_93369_) {
        super(p_93365_, p_93366_, p_93367_, p_93368_, p_93369_);
    }

    @Override
    public boolean isHoveredOrFocused() {
        return false;
    }
}
