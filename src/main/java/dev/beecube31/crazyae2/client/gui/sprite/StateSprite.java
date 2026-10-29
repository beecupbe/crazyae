package dev.beecube31.crazyae2.client.gui.sprite;

import appeng.client.gui.style.Blitter;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public enum StateSprite implements ISpriteProvider {
    WHITE_ARROW_RIGHT(0, 16, 16,1),
    WHITE_ARROW_DOWN(16, 16, 16,1),
    WHITE_ARROW_LEFT(32, 16, 16,1),
    WHITE_ARROW_UP(48, 16, 16,1),

    OPTION_SIDE_BUTTON(13, 26, 25,1),
    OPTION_SIDE_BUTTON_CENTER(39, 26, 25,1),
    OPTION_SIDE_BUTTON_NO_TOP(9, 26, 25,1),
    OPTION_SIDE_BUTTON_NO_BOTTOM(7, 26, 25,1),
    OPTION_SIDE_BUTTON_NO_TOP_BOTTOM(11, 26, 25,1),

    HOVER_OPTION_SIDE_BUTTON(40, 32, 32,1),
    HOVER_OPTION_SIDE_BUTTON_SELECTED(42, 32, 32,1),
    HOVER_OPTION_SIDE_BUTTON_HOVERED(44, 32, 32,1),


    SELECT(64, 16, 16,1),
    DESELECT(80, 16, 16,1),

    HIGHLIGHT_BLOCK(81, 16, 16,1),
    FIND(97, 16, 16,1),

    COPY_TO_CLIPBOARD_SMALL(129, 8, 8,1),
    PASTE_FROM_CLIPBOARD_SMALL(130, 8, 8,1),

    COPY_TO_CLIPBOARD(131, 16, 16,1),
    PASTE_FROM_CLIPBOARD(132, 16, 16,1),


    RESTORE_DEFAULTS(96, 16, 16,1),
    ABC(225, 17, 7,1),
    QUARTZ_WRENCH(112, 16, 16,1),
    GUI(128, 16, 16,1),

    TRASH(1, 16, 16,1),
    MATTER_BALL(2, 16, 16,1),
    SINGULARITY(3, 16, 16,1),

    NBT(4, 16, 16,1),
    QUESTION_MARK(5, 16, 16,1),

    IO_PORT_MODE_0(17, 16, 16,1),
    IO_PORT_MODE_1(18, 16, 16,1),
    IO_PORT_MODE_2(19, 16, 16,1),

    PATTERN_ENCODED(33, 16, 16,1),
    PATTERN_ENCODED_DISABLED(34, 16, 16,1),

    REDSTONE_CARD_MODE_ALWAYS_ACTIVE(49, 16, 16,1),
    REDSTONE_CARD_MODE_ACTIVE_WITHOUT_SIGNAL(50, 16, 16,1),
    REDSTONE_CARD_MODE_ACTIVE_WITH_SIGNAL(51, 16, 16,1),
    REDSTONE_CARD_MODE_ACTIVE_PER_PULSE(52, 16, 16,1),

    STORAGE_CELLS_SLOT(15, 16, 16,1),
    BLOCKS_SLOT(15 + 16, 16, 16,1),
    DUSTS_SLOT(15 + 16 * 2, 16, 16,1),
    STORAGE_COMPONENTS_SLOT(15 + 16 * 3, 16, 16,1),
    WIRELESS_TERM_SLOT(15 + 16 * 4, 16, 16,1),
    TRASH_SLOT(15 + 16 * 5, 16, 16,1),
    WIRELESS_BOOSTERS_SLOT(15 + 16 * 6, 16, 16,1),
    PATTERNS_SLOT_OLD(15 + 16 * 7, 16, 16,1),
    PATTERNS_SLOT_NEW(15 + 16 * 8, 16, 16,1),
    USB_DEVICE_SLOT(15 + 16 * 9, 16, 16,1),
    MATTER_BALL_SLOT(15 + 16 * 10, 16, 16,1),
    SPATIAL_CELLS_SLOT(15 + 16 * 11, 16, 16,1),
    FUEL_SLOT(15 + 16 * 12, 16, 16,1),
    CRAZYAE_UPGRADE_CARDS_SLOT(15 + 16 * 13, 16, 16,1),
    AE_UPGRADE_CARDS_SLOT(14 + 16 * 13, 16, 16,1),
    BIOMETRIC_CARDS_SLOT(15 + 16 * 14, 16, 16,1),

    FIND_SLOT(14 + 16 * 14, 16, 16,1),
    BREW(254, 16, 16,1),

    INSCRIBER_TOP_BOTTOM_INGREDIENT_SLOT(14 + 16 * 2, 16, 16,1),
    INGOT_SLOT(14 + 16 * 3, 16, 16,1),
    VIEW_CELLS_SLOT(14 + 16 * 4, 16, 16,1),
    CRAFTING_ACCELERATORS_SLOT(14 + 16 * 5, 16, 16,1),
    CRAFTING_STORAGES_SLOT(14 + 16 * 6, 16, 16,1),
    CRAFTING_BLOCKS_SLOT(14 + 16 * 7, 16, 16,1),
    CERTUS_QUARTZ(14 + 16 * 8, 16, 16,1),
    CHARGED_CERTUS_QUARTZ(14 + 16 * 9, 16, 16,1),
    BOTANIA_CATALYSTS_SLOT(14 + 16 * 10, 16, 16,1),
    QUANTUM_WIRELESS_BOOSTERS_SLOT(14 + 16 * 11, 16, 16,1),
    MEMORY_CARD_SLOT(14 + 16 * 12, 16, 16,1),

    IMAGE_BUTTON_TOP(12 + 16 * 8, 19, 21,1),
    IMAGE_BUTTON_CENTER(12 + 16 * 10, 19, 21,1),
    IMAGE_BUTTON_BOTTOM(12 + 16 * 12, 19, 21,1),

    IMAGE_BUTTON(255, 16, 16,1),
    PROGRESS_BAR_FILLED(16 * 13, 16, 18,1),
    PROGRESS_BAR_FILLED_6X18(231, 6, 18,1),

    CHECKBOX_ON(16 * 15 + 1, 15, 15,1),
    CHECKBOX_OFF(16 * 15 + 2, 15, 15,1),

    CHECKBOX_V2_ON(229, 21, 21,1),
    CHECKBOX_V2_OFF(227, 21, 21,1),


    SLIDER(16 * 15, 8, 13, 1),

    //file index 2

    CRAFTING_STORAGE_128X(0, 128, 128, 2),
    CRAFTING_ACCELERATOR_128X(8, 128, 128, 2),

    ENERGY(128, 14, 20, 2);


    private final int index;
    private final int sizeX;
    private final int sizeY;
    private final int fileIndex;

    private Object blitter;

    StateSprite
    (
            int index,
            int sizeX,
            int sizeY,
            int fileIdx
    ) {
        if (fileIdx <= 0) {
            throw new IllegalArgumentException("StateSprite file index must be non-zero");
        }

        if (index > 255) {
            throw new IllegalArgumentException("StateSprite index must be lower than 256");
        }

        this.index = index;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.fileIndex = fileIdx;

    }

    public int getIndex() {
        return index;
    }

    public int getTextureX() {
        return (this.getIndex() % 16) * 16;
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

    public int getFileIndex() {
        return fileIndex;
    }

    public String getTextureStr() {
        return fileIndex > 1 ? "guis/states" + fileIndex + ".png" : "guis/states.png";
    }

    public Identifier getTexture() {
        return Identifier.fromNamespaceAndPath(CrazyAE.MODID, "textures/" + this.getTextureStr());
    }

    @Override
    public Blitter getBlitter() {
        if (this.blitter == null) {
            this.blitter = Blitter.texture(this.getTexture()).src(this.getTextureX(), this.getTextureY(), sizeX, sizeY);
        }
        return (Blitter) this.blitter;
    }
}
