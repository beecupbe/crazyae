package dev.beecube31.crazyae2.common.interfaces.mana.crafting;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

public interface IManapoolPatternDetails {
    ItemStack getPattern();

    IAEItemStack getInput();

    ItemStack getCatalyst();

    IAEItemStack getOutput();

    int getPriority();

    void setPriority(int var1);
}
