package dev.beecube31.crazyae2.common.sync;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

public enum CrazyAETooltip {
    NOT_DEFINED,

    SHIFT_FOR_DETAILS,
    CTRL_FOR_PRECISE_DETAILS,
    ENCODED_BY,
    AUTO_PICKUP,
    AUTO_PICKUP_TIP,
    AUTO_PICKUP_HOW_TO_ENABLE,

    MANA_CONNECTOR_BLOCK_LINKED,
    MANA_CONNECTOR_BLOCK_SAVED,
    MANA_CONNECTOR_CLEAR,

    WIRELESS_CONNECTOR_BLOCK_LINKED,
    WIRELESS_CONNECTOR_BLOCK_SAVED,
    WIRELESS_CONNECTOR_CLEAR,

    MEMORYCARD_ELVENTRADE_SAVED,
    MEMORYCARD_MANAPOOL_SAVED,
    MEMORYCARD_PETAL_SAVED,
    MEMORYCARD_RUNEALTAR_SAVED,
    MEMORYCARD_PUREDAISY_SAVED,
    MEMORYCARD_ITEMS_SAVED,


    ENABLED_LOWERCASE,
    DISABLED_LOWERCASE,
    ENABLED,
    DISABLED,

    PASSIVE_GENERATION_DAY,
    PASSIVE_GENERATION_NIGHT,
    MAX_SOLAR_CAPACITY,
    USE_NET_TOOL_TO_CONTROL_SOLARS,

    QUANTUM_WIRELESS_BOOSTER_DESC,
    MANA_CONNECTOR_DESC,
    MANA_CONNECTOR_LETS_CONNECT_TO_BUS,
    LINKED_WITH_MANA_POOL_AT_POS,

    QCM_DESC,

    COLORIZER_DESC,
    USB_PATTERNS_STICK_DESC,

    CONNECTED_TO,
    NO_CONNECTION,


    REQUIRE_MANA_PER_JOB,
    REQUIRE_MANA_PER_TICK,
    REQUIRE_AE_PER_JOB,
    REQUIRE_AE_PER_TICK;


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
