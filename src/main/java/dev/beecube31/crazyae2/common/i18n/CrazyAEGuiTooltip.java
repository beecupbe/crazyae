package dev.beecube31.crazyae2.common.i18n;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation, unused")
public enum CrazyAEGuiTooltip {
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
        this.root = "gui.tooltip." + Tags.MODID;
    }

    CrazyAEGuiTooltip(String r) {
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
