package dev.beecube31.crazyae2.common.interfaces.mana.crafting;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPetalPatternDetails {
    ItemStack getPattern();

    IAEItemStack[] getInputs();

    IAEItemStack[] getCondensedInputs();

    IAEItemStack getOutput();

    int getPriority();

    void setPriority(int var1);
}
