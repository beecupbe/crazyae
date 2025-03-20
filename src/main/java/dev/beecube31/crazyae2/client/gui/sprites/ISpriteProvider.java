package dev.beecube31.crazyae2.client.gui.sprites;

import net.minecraft.util.ResourceLocation;

public interface ISpriteProvider {
    int getTextureX();

    int getTextureY();

    int getSizeX();

    int getSizeY();

    String getTextureStr();

    ResourceLocation getTexture();
}
