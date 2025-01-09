package dev.beecube31.crazyae2.common.interfaces.mana.crafting;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IElventradePatternDetails {
    ItemStack getPattern();

    IAEItemStack[] getInputs();

    IAEItemStack[] getCondensedInputs();

    IAEItemStack[] getCondensedOutputs();

    default IAEItemStack getPrimaryOutput() {
        return this.getOutputs()[0];
    }

    IAEItemStack[] getOutputs();

    ItemStack getOutput(InventoryCrafting var1, World var2);

    int getPriority();

    void setPriority(int var1);
}
