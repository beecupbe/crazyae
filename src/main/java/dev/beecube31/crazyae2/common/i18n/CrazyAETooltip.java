package dev.beecube31.crazyae2.common.i18n;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@SuppressWarnings("unused")
public enum CrazyAETooltip {
    AUTO_PICKUP,

    AUTO_PICKUP_HOW_TO_ENABLE,
    AUTO_PICKUP_TIP,
    COLORIZER_DESC,
    CONNECTED_TO,
    DISABLED,

    DISABLED_LOWERCASE,
    ENABLED,
    ENABLED_LOWERCASE,

    ENCODED_BY,

    MAX_SOLAR_CAPACITY,
    NOT_DEFINED,
    NO_CONNECTION,

    PASSIVE_GENERATION_DAY,

    PASSIVE_GENERATION_NIGHT,

    REQUIRE_AE_PER_TICK,
    REQUIRE_AE_PER_JOB,

    REQUIRE_CHANNEL;

    private final String root;

    CrazyAETooltip() {
        this.root = "tooltip." + CrazyAE.MODID;
    }

    CrazyAETooltip(String r) {
        this.root = r;
    }

    public MutableComponent getComponent() {
        return Component.translatable(this.getUnlocalizedString().toLowerCase());
    }

    public MutableComponent getComponentWithArgs(Object... args) {
        return Component.translatable(this.getUnlocalizedString().toLowerCase(), args);
    }

    public MutableComponent getComponentWithSpaceAtEnd() {
        return Component.translatable(this.getUnlocalizedString().toLowerCase()).append(" ");
    }

    public MutableComponent getComponentWithArgsAndSpaceAtEnd(Object... args) {
        return Component.translatable(this.getUnlocalizedString().toLowerCase(), args).append(" ");
    }

    public String getUnlocalizedString() {
        return this.root + '.' + this;
    }
}
