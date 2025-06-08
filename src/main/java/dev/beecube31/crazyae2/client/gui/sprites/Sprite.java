package dev.beecube31.crazyae2.client.gui.sprites;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.ResourceLocation;

public enum Sprite implements ISpriteProvider {
    CRAFTING_ACCELERATOR("sprites/crafting_accelerator.png", 1024, 1024, 0, 0),
    CRAFTING_STORAGE("sprites/crafting_storage.png", 1024, 1024, 0, 0),
    BOTANIA_CATALYST("sprites/botania_catalyst.png", 1024, 1024, 0, 0),
    FRAME_48x48("sprites/frame.png", 52, 52, 0, 0),

    PROGRESS_BAR_FILLED("widgets/bars.png", 16, 18, 240, 0),

    ENERGY_BAR_EMPTY("widgets/bars.png", 72, 18, 100, 0),
    ENERGY_BAR_QE("widgets/bars.png", 72, 18, 100, 18),
    ENERGY_BAR_EXP("widgets/bars.png", 72, 18, 100, 36),
    ENERGY_BAR_NE("widgets/bars.png", 72, 18, 100, 54),
    ENERGY_BAR_SE("widgets/bars.png", 72, 18, 100, 72),
    ENERGY_BAR_EF("widgets/bars.png", 72, 18, 100, 90),
    ENERGY_BAR_EU("widgets/bars.png", 72, 18, 100, 108),

    WARN_GRAY("sprites/icons/warning_gray.png", 9, 9, 0, 0),
    INFO("sprites/icons/info.png", 9, 9, 0, 0),

    BAR("widgets/bar.png", 102, 5, 0, 0),
    BAR_FILL("widgets/bar.png", 100, 3, 0, 5),

    SUN("sprites/icons/sun.png", 16, 16, 0,0),
    MOON("sprites/icons/moon.png", 16, 16, 0,0),
    CAPACITY("sprites/icons/capacity.png", 32, 32, 0,0),

    NO("sprites/icons/x.png", 13, 13, 0,0),
    YES("sprites/icons/y.png", 15, 15, 0,0),



    STORAGE_256K_CELL("sprites/icons/storage_cell_256k.png", 16, 16, 0, 0),
    DUST("sprites/icons/dust.png", 16, 16, 0, 0),
    WATER_BUCKET("sprites/icons/water_bucket.png", 16, 16, 0, 0),
    CERTUS_WRENCH("sprites/icons/certus_quartz_wrench.png", 16, 16, 0, 0),
    ENERGY("sprites/icons/energy.png", 20, 20, 0, 0),
    IRON_INGOT("sprites/icons/iron_ingot.png", 16, 16, 0, 0),
    MANA_TABLET("sprites/icons/mana_tablet.png", 16, 16, 0, 0);



    private final String textureStr;
    private final int sizeX;
    private final int sizeY;

    private final int textureX;
    private final int textureY;

    Sprite
    (
        String textureStr,
        int sizeX,
        int sizeY,
        int textureX,
        int textureY
    ) {
        this.textureStr = textureStr;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public String getTextureStr() {
        return "guis/" + textureStr;
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(Tags.MODID, "textures/guis/" + textureStr);
    }

    public int getTextureX() {
        return this.textureX;
    }

    public int getTextureY() {
        return this.textureY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}
