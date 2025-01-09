package dev.beecube31.crazyae2.common.sync;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

public enum CrazyAEGuiTooltip {
    NOT_DEFINED,

    CU_PLACE_ALL_ACCELERATORS_HERE,
    CU_PLACE_ALL_CRAFTING_STORAGES_HERE,

    MANA,
    STORED_MANA,

    PROGRESS,

    QUEUED_ITEMS,
    PROGRESS_PER_TICK;

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
        return I18n.translateToLocal(this.getUnlocalized().toLowerCase()) + " ";
    }

    public String getUnlocalized() {
        return this.root + '.' + this + ".name";
    }
}
