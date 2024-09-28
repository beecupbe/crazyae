package dev.beecube31.crazyae2.common.sync;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

public enum CrazyAETooltip {
    NOT_DEFINED,

    SHIFT_FOR_DETAILS,

    PASSIVE_GENERATION_DAY,
    PASSIVE_GENERATION_NIGHT,
    MAX_SOLAR_CAPACITY,
    USE_NET_TOOL_TO_CONTROL_SOLARS,

    QUANTUM_WIRELESS_BOOSTER_DESC;

    private final String root;

    CrazyAETooltip() {
        this.root = "tooltip." + Tags.MODID;
    }

    CrazyAETooltip(String r) {
        this.root = r;
    }

    public String getLocal() {
        return I18n.translateToLocal(this.getUnlocalized().toLowerCase());
    }

    public String getLocalWithSpaceAtEnd() {
        return I18n.translateToLocal(this.getUnlocalized().toLowerCase()) + " ";
    }

    public String getUnlocalized() {
        return this.root + '.' + this + ".name";
    }
}
