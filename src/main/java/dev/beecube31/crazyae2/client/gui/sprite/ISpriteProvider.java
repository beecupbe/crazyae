package dev.beecube31.crazyae2.client.gui.sprite;

import appeng.client.gui.style.Blitter;
import net.minecraft.resources.ResourceLocation;

public interface ISpriteProvider {
    int getTextureX();

    int getTextureY();

    int getSizeX();

    int getSizeY();

    String getTextureStr();

    ResourceLocation getTexture();

    Blitter getBlitter();
}
