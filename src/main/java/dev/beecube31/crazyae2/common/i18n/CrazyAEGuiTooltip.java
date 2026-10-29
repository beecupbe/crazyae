package dev.beecube31.crazyae2.common.i18n;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@SuppressWarnings("unused")
public enum CrazyAEGuiTooltip {
    CONFIG_AMOUNT_MIDDLE_CLICK,
    CONFIG_AMOUNT_SCROLL,
    CONFIG_AMOUNT_SCROLL_DOUBLE,

    CU_PLACE_ALL_ACCELERATORS_HERE,

    CU_PLACE_ALL_CRAFTING_STORAGES_HERE,
    EF_ENERGY,

    ENERGY_CELL_FORMATTING_HINT,
    FE_ENERGY,
    MANA,
    NE_ENERGY,

    NOT_DEFINED,
    PROGRESS,

    PROGRESS_PER_TICK,

    QE_ENERGY,
    QUEUED_ITEMS,
    SEND_ALL_ENERGY_TO_ME,
    SE_ENERGY,
    STORED,

    STORED_ITEM,
    STORED_MANA,

    THIS_DEVICE_SUPPORTS;

    private final String root;

    CrazyAEGuiTooltip() {
        this.root = "gui.tooltip." + CrazyAE.MODID;
    }

    CrazyAEGuiTooltip(String r) {
        this.root = r;
    }

    public MutableComponent getComponent() {
        return Component.translatable(this.getUnlocalizedString().toLowerCase());
    }

    public MutableComponent getComponentWithArgs(Object... args) {
        return Component.translatable(this.getUnlocalizedString().toLowerCase(), new Object[]{args});
    }

    public MutableComponent getComponentWithSpaceAtEnd() {
        return Component.translatable(this.getUnlocalizedString().toLowerCase()).append(" ");
    }

    public MutableComponent getComponentWithArgsAndSpaceAtEnd(Object... args) {
        return Component.translatable(this.getUnlocalizedString().toLowerCase(), new Object[]{args}).append(" ");
    }

    public String getUnlocalizedString() {
        return this.root + '.' + this;
    }
}
