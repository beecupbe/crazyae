package dev.beecube31.crazyae2.common.sync;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

public enum CrazyAEGuiText {
    NOT_DEFINED,

    LINK_WITH_MANA_CONNECTOR,
    LINK_WITH_MANA_CONNECTOR2,
    LINK_WITH_MANA_CONNECTOR3,
    LINKED_WITH_MANA_POOL,



    MANA_CELLS,
    MANA_TERMINAL,

    UPDATE_FOUND,
    DOWNLOAD_LINK,
    DISABLE_UPDATES_TIP,

    IMPROVED_DRIVE_GUI,
    IMPROVED_MAC_GUI,
    BIG_CRYSTAL_CHARGER,
    CU_COMBINER,
    IMP_IMPORT_BUS,
    IMP_EXPORT_BUS,
    IMP_FLUID_IMPORT_BUS,
    IMP_FLUID_EXPORT_BUS,
    PATTERN_INTERFACE,
    MANA_IMPORT_BUS,
    MANA_EXPORT_BUS;


    private final String root;

    CrazyAEGuiText() {
        this.root = "gui." + Tags.MODID;
    }

    CrazyAEGuiText(String r) {
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
