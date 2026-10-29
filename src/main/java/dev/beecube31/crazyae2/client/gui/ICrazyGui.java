package dev.beecube31.crazyae2.client.gui;

import dev.beecube31.crazyae2.client.components.ColorContainer;

public interface ICrazyGui {
    default ColorContainer getGuiHue() {
        return CrazyGuiColors.gui();
    }
}
