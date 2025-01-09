package dev.beecube31.crazyae2.common.sync;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.text.translation.I18n;

public enum CrazyAEGuiText {
    NOT_DEFINED,

    SELECT_ALL,
    DESELECT_ALL,

    COPY_TO_CLIPBOARD,
    PASTE_FROM_CLIPBOARD,

    RESTORE_DEFAULTS,

    COLORIZER_GUI_GUI_TO_TEXT,
    COLORIZER_GUI_TEXT_TO_GUI,


    LINK_WITH_MANA_CONNECTOR,
    LINK_WITH_MANA_CONNECTOR2,
    LINK_WITH_MANA_CONNECTOR3,
    LINKED_WITH_MANA_POOL,

    MANA_AMT_TO_MAX,

    HIGHLIGHT_MANA_BLOCK,
    MANA_BLOCK_HIGHLIGHTED_IN,
    MANA_BLOCK_HIGHLIGHTED_IN_ANOTHER_DIMENSION,
    MANA_BLOCK_HIGHLIGHTED_IN_ANOTHER_DIMENSION_WITH_POS,
    COLORIZING_DISABLED,


    ELVENTRADE_PATTERN,
    MANAPOOL_PATTERN,
    PETAL_PATTERN,
    PUREDAISY_PATTERN,
    RUNEALTAR_PATTERN,

    OPEN_PATTERN_STORAGE,



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
    MANA_EXPORT_BUS,

    MECHANICAL_ELVENTRADE,
    MECHANICAL_PETAL,
    MECHANICAL_RUNEALTAR,
    MECHANICAL_PUREDAISY,
    MECHANICAL_MANAPOOL,

    PATTERNS_INV_GUI,


    BUTTON_PATTERN_FAST_PLACE,

    GUI_COLORIZER_GUI,
    GUI_COLORIZER_TEXT,


    CHECKBOX_QWAP_TEXT;



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
