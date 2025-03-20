package dev.beecube31.crazyae2.common.interfaces.gui;

import net.minecraft.item.ItemStack;

public interface IStacksizeRenderTarget {
    ItemStack getDisplayStack();

    int xPos();

    int yPos();
}
