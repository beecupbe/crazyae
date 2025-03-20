package dev.beecube31.crazyae2.client.gui.sprites;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.ResourceLocation;

public enum StateSprite implements ISpriteProvider {
    WHITE_ARROW_RIGHT(0, 16, 16),
    WHITE_ARROW_DOWN(16, 16, 16),
    WHITE_ARROW_LEFT(32, 16, 16),
    WHITE_ARROW_UP(48, 16, 16),

    OPTION_SIDE_BUTTON(13, 26, 25),
    OPTION_SIDE_BUTTON_NO_TOP(9, 26, 25),
    OPTION_SIDE_BUTTON_NO_BOTTOM(7, 26, 25),
    OPTION_SIDE_BUTTON_NO_TOP_BOTTOM(11, 26, 25),

    HOVER_OPTION_SIDE_BUTTON(40, 32, 32),
    HOVER_OPTION_SIDE_BUTTON_SELECTED(42, 32, 32),
    HOVER_OPTION_SIDE_BUTTON_HOVERED(44, 32, 32),


    SELECT(64, 16, 16),
    DESELECT(80, 16, 16),

    HIGHLIGHT_BLOCK(81, 16, 16),
    FIND(97, 16, 16),

    COPY_TO_CLIPBOARD_SMALL(129, 8, 8),
    PASTE_FROM_CLIPBOARD_SMALL(130, 8, 8),

    COPY_TO_CLIPBOARD(131, 16, 16),
    PASTE_FROM_CLIPBOARD(132, 16, 16),


    RESTORE_DEFAULTS(96, 16, 16),
    ABC(225, 17, 7),
    QUARTZ_WRENCH(112, 16, 16),
    GUI(128, 16, 16),

    TRASH(1, 16, 16),
    MATTER_BALL(2, 16, 16),
    SINGULARITY(3, 16, 16),

    IO_PORT_MODE_0(17, 16, 16),
    IO_PORT_MODE_1(18, 16, 16),
    IO_PORT_MODE_2(19, 16, 16),

    PATTERN_ENCODED(33, 16, 16),
    PATTERN_ENCODED_DISABLED(34, 16, 16),

    ELVENTRADE_ENCODED_PATTERN(35, 16, 16),
    MANAPOOL_ENCODED_PATTERN(36, 16, 16),
    PETAL_ENCODED_PATTERN(37, 16, 16),
    PUREDAISY_ENCODED_PATTERN(38, 16, 16),
    RUNEALTAR_ENCODED_PATTERN(39, 16, 16),
    BREWERY_ENCODED_PATTERN(53, 16, 16),
    TERAPLATE_ENCODED_PATTERN(54, 16, 16),

    REDSTONE_CARD_MODE_ALWAYS_ACTIVE(49, 16, 16),
    REDSTONE_CARD_MODE_ACTIVE_WITHOUT_SIGNAL(50, 16, 16),
    REDSTONE_CARD_MODE_ACTIVE_WITH_SIGNAL(51, 16, 16),
    REDSTONE_CARD_MODE_ACTIVE_PER_PULSE(52, 16, 16),

    FUZZY_ANY(65, 16, 16),
    FUZZY_99(66, 16, 16),
    FUZZY_75(67, 16, 16),
    FUZZY_50(68, 16, 16),
    FUZZY_25(69, 16, 16),



    STORAGE_CELLS_SLOT(15, 16, 16),
    BLOCKS_SLOT(15 + 16, 16, 16),
    DUSTS_SLOT(15 + 16 * 2, 16, 16),
    STORAGE_COMPONENTS_SLOT(15 + 16 * 3, 16, 16),
    WIRELESS_TERM_SLOT(15 + 16 * 4, 16, 16),
    TRASH_SLOT(15 + 16 * 5, 16, 16),
    WIRELESS_BOOSTERS_SLOT(15 + 16 * 6, 16, 16),
    PATTERNS_SLOT_OLD(15 + 16 * 7, 16, 16),
    PATTERNS_SLOT_NEW(15 + 16 * 8, 16, 16),
    USB_DEVICE_SLOT(15 + 16 * 9, 16, 16),
    MATTER_BALL_SLOT(15 + 16 * 10, 16, 16),
    SPATIAL_CELLS_SLOT(15 + 16 * 11, 16, 16),
    FUEL_SLOT(15 + 16 * 12, 16, 16),
    CRAZYAE_UPGRADE_CARDS_SLOT(15 + 16 * 13, 16, 16),
    AE_UPGRADE_CARDS_SLOT(14 + 16 * 13, 16, 16),
    BIOMETRIC_CARDS_SLOT(15 + 16 * 14, 16, 16),

    FIND_SLOT(14 + 16 * 14, 16, 16),
    BREW(254, 16, 16),

    INSCRIBER_TOP_BOTTOM_INGREDIENT_SLOT(14 + 16 * 2, 16, 16),
    INGOT_SLOT(14 + 16 * 3, 16, 16),
    VIEW_CELLS_SLOT(14 + 16 * 4, 16, 16),
    CRAFTING_ACCELERATORS_SLOT(14 + 16 * 5, 16, 16),
    CRAFTING_STORAGES_SLOT(14 + 16 * 6, 16, 16),
    CRAFTING_BLOCKS_SLOT(14 + 16 * 7, 16, 16),
    CERTUS_QUARTZ(14 + 16 * 8, 16, 16),
    CHARGED_CERTUS_QUARTZ(14 + 16 * 9, 16, 16),
    BOTANIA_CATALYSTS_SLOT(14 + 16 * 10, 16, 16),
    QUANTUM_WIRELESS_BOOSTERS_SLOT(14 + 16 * 11, 16, 16),
    MEMORY_CARD_SLOT(14 + 16 * 12, 16, 16),

    IMAGE_BUTTON(255, 16, 16),
    PROGRESS_BAR_FILLED(16 * 13, 16, 18),

    CHECKBOX_ON(16 * 15 + 1, 15, 15),
    CHECKBOX_OFF(16 * 15 + 2, 15, 15),

    CHECKBOX_V2_ON(229, 21, 21),
    CHECKBOX_V2_OFF(227, 21, 21),


    SLIDER(16 * 15, 8, 13);


    private final int index;
    private final int sizeX;
    private final int sizeY;

    StateSprite
    (
        int index,
        int sizeX,
        int sizeY
    ) {
        if (index > 255) {
            throw new IllegalArgumentException("StateSprite index must be lower than 255");
        }

        this.index = index;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getIndex() {
        return index;
    }

    public int getTextureX() {
        return (this.getIndex() - this.getTextureY() * 16) * 16;
    }

    public int getTextureY() {
        return ((int) Math.floor((double) this.getIndex() / 16)) * 16;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public String getTextureStr() {
        return "guis/states.png";
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(Tags.MODID, "textures/" + this.getTextureStr());
    }
}
