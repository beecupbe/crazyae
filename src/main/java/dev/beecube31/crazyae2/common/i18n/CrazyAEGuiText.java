package dev.beecube31.crazyae2.common.i18n;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@SuppressWarnings("unused")
public enum CrazyAEGuiText {
    AUTOSEND_TO_ME,
    BIG_CRYSTAL_CHARGER,

    COLORIZER_GUI_GUI_TO_TEXT,
    COLORIZER_GUI_TEXT_TO_GUI,

    COLORIZING_DISABLED,

    CRAFT_STARTED_AT,

    COPY_TO_CLIPBOARD,
    CU_COMBINER,
    DENSE_PORTABLE_CELL,
    DESELECT_ALL,

    IMPROVED_DRIVE_GUI,
    IMPROVED_MAC_GUI,
    IMP_EXPORT_BUS,

    INITIATOR,

    IMP_FLUID_EXPORT_BUS,
    IMP_FLUID_IMPORT_BUS,
    IMP_IMPORT_BUS,
    ITEMS_USED,

    NOT_DEFINED,

    NO_LIMIT,
    OPEN_CRAFTING_BLOCK_LIST,


    OPEN_ENERGY_SETTINGS_PAGE,
    OPEN_PATTERN_STORAGE,
    PAGE,
    PASTE_FROM_CLIPBOARD,
    PATTERNS_INV_GUI,
    PATTERN_INTERFACE,
    PERFECT_INSCRIBER,

    PERFECT_INTERFACE,
    PROGRESS,

    PORTABLE_CELL_CHARGING,

    RESTORE_DEFAULTS,


    SELECT_ALL,
    TRASHCAN,
    USED,

    WIRELESS_TERMINALS_CHARGING;


    private final String root;

    CrazyAEGuiText() {
        this.root = "i18n." + CrazyAE.MODID;
    }

    CrazyAEGuiText(String r) {
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

    public String getUnlocalizedString() { // NOT IN LOWERCASE !!!11
        return this.root + '.' + this;
    }
}
