package dev.beecube31.crazyae2.client.gui.sprites;

import dev.beecube31.crazyae2.Tags;
import net.minecraft.util.ResourceLocation;

public enum Sprite {
    CRAFTING_ACCELERATOR("crafting_accelerator.png", 1024, 1024, 0, 0),
    CRAFTING_STORAGE("crafting_storage.png", 1024, 1024, 0, 0),
    BOTANIA_CATALYST("botania_catalyst.png", 1024, 1024, 0, 0);


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
        return "guis/sprites/" + textureStr;
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(Tags.MODID, "textures/guis/sprites" + textureStr);
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
