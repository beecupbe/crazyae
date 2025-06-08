package dev.beecube31.crazyae2.common.i18n;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation, unused")
public enum CrazyAETooltip {
    AUTO_PICKUP,

    AUTO_PICKUP_HOW_TO_ENABLE,
    AUTO_PICKUP_TIP,
    COLORIZER_DESC,
    CONNECTED_TO,
    CTRL_FOR_PRECISE_DETAILS,
    DISABLED,

    DISABLED_LOWERCASE,
    ENABLED,
    ENABLED_LOWERCASE,

    ENCODED_BY,
    LINKED_WITH_MANA_POOL_AT_POS,
    MANA_CONNECTOR_BLOCK_LINKED,

    MANA_CONNECTOR_BLOCK_SAVED,
    MANA_CONNECTOR_CLEAR,
    MANA_CONNECTOR_DESC,
    MANA_CONNECTOR_LETS_CONNECT_TO_BUS,
    MAX_SOLAR_CAPACITY,
    MEMORYCARD_ELVENTRADE_SAVED,


    MEMORYCARD_ITEMS_SAVED,
    MEMORYCARD_MANAPOOL_SAVED,
    MEMORYCARD_PETAL_SAVED,
    MEMORYCARD_PUREDAISY_SAVED,

    MEMORYCARD_RUNEALTAR_SAVED,
    MORE_FLUID_PER_TICK,
    MORE_ITEMS_PER_TICK,
    MORE_OPERATIONS_PER_JOB,

    MORE_OPERATIONS_PER_TICK,
    MORE_TASKS_IN_JOB,
    NOT_DEFINED,
    NO_CONNECTION,

    PASSIVE_GENERATION_DAY,

    PASSIVE_GENERATION_NIGHT,
    QCM_DESC,

    QUANTUM_CPU_WARNING,
    QUANTUM_INTERFACE_WARNING,


    QUANTUM_WIRELESS_BOOSTER_DESC,
    REQUIRE_AE_PER_JOB,
    REQUIRE_AE_PER_TICK,
    REQUIRE_CHANNEL,
    REQUIRE_MANA_PER_JOB,

    REQUIRE_MANA_PER_TICK,
    SHIFT_FOR_DETAILS,
    USB_PATTERNS_STICK_DESC,
    USE_NET_TOOL_TO_CONTROL_SOLARS,
    WIRELESS_CONNECTOR_BLOCK_LINKED,

    WIRELESS_CONNECTOR_BLOCK_SAVED,
    WIRELESS_CONNECTOR_CLEAR;


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
        return I18n.translateToLocal(this.getUnlocalized().toLowerCase()) + ' ';
    }

    public String getUnlocalized() {
        return this.root + '.' + this + ".name";
    }
}
