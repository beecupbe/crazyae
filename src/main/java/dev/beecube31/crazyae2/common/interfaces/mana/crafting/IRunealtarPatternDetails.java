package dev.beecube31.crazyae2.common.interfaces.mana.crafting;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

public interface IRunealtarPatternDetails {
    ItemStack getPattern();

    IAEItemStack[] getInputs();

    IAEItemStack[] getCondensedInputs();

    IAEItemStack getOutput();

    int getPriority();

    void setPriority(int var1);
}
