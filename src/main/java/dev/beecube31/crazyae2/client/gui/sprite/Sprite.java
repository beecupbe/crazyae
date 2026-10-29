package dev.beecube31.crazyae2.client.gui.sprite;


import appeng.client.gui.style.Blitter;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public enum Sprite implements ISpriteProvider {
    CRAFTING_ACCELERATOR("sprites/crafting_accelerator.png", 1024, 1024, 0, 0),
    CRAFTING_STORAGE("sprites/crafting_storage.png", 1024, 1024, 0, 0);


    private final Blitter blitter;

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

        Identifier textureLocation = Identifier.fromNamespaceAndPath(CrazyAE.MODID, "textures/guis/" + textureStr);
        this.blitter = Blitter.texture(textureLocation, sizeX, sizeY)
                .src(textureX, textureY, sizeX, sizeY);
    }

    @Override
    public Blitter getBlitter() {
        return this.blitter.copy();
    }

    @Override
    public String getTextureStr() {
        return "guis/" + textureStr;
    }

    @Override
    public Identifier getTexture() {
        return Identifier.fromNamespaceAndPath(CrazyAE.MODID, "textures/guis/" + textureStr);
    }

    @Override
    public int getTextureX() {
        return this.textureX;
    }

    @Override
    public int getTextureY() {
        return this.textureY;
    }

    @Override
    public int getSizeX() {
        return sizeX;
    }

    @Override
    public int getSizeY() {
        return sizeY;
    }
}
