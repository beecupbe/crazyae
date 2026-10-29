package dev.beecube31.crazyae2.client.gui;

import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.core.config.CrazyAEClientConfig;

public final class CrazyGuiColors {
    private CrazyGuiColors() {}

    public static boolean isEnabled() {
        return CrazyAEClientConfig.CLIENT.isColorizingEnabled.get();
    }

    public static ColorContainer gui() {
        ColorContainer c = new ColorContainer(1F, 1F, 1F, 1F);
        if (isEnabled()) {
            var cfg = CrazyAEClientConfig.CLIENT;
            c.setParams(cfg.colorizerColorRed.get() / 255F, cfg.colorizerColorGreen.get() / 255F,
                    cfg.colorizerColorBlue.get() / 255F, 1F);
        }
        return c;
    }
}
