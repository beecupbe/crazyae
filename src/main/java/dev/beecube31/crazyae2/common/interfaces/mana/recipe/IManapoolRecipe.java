package dev.beecube31.crazyae2.common.interfaces.mana.recipe;

import net.minecraft.item.ItemStack;

public interface IManapoolRecipe extends IBotaniaRecipe {
    boolean recipeMatches(ItemStack stack);
}
