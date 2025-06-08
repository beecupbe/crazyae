package dev.beecube31.crazyae2.common.enums;

import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;

public enum ColorizerType {
    GUI, TEXT;

    public String toLocal() {
        return switch (this.ordinal()) {
            case 0 -> CrazyAEGuiText.COLORIZER_GUI_GUI_TO_TEXT.getLocal();
            case 1 -> CrazyAEGuiText.COLORIZER_GUI_TEXT_TO_GUI.getLocal();
            default -> throw new IllegalStateException("Unexpected value: " + this.ordinal());
        };
    }

    public CrazyAEGuiBridge toGUI() {
        return switch (this.ordinal()) {
            case 0 -> CrazyAEGuiBridge.GUI_ITEM_COLORIZER_TEXT;
            case 1 -> CrazyAEGuiBridge.GUI_ITEM_COLORIZER_GUI;
            default -> throw new IllegalStateException("Unexpected value: " + this.ordinal());
        };
    }
}
