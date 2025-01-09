package dev.beecube31.crazyae2.common.interfaces.mana.crafting;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPuredaisyPatternDetails {
    ItemStack getPattern();

    IAEItemStack getInput();

    IAEItemStack getOutput();

    int getPriority();

    void setPriority(int var1);
}
