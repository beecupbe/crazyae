package dev.beecube31.crazyae2.common.interfaces.mana.recipe;

import net.minecraftforge.items.IItemHandler;

public interface IPetalRecipe extends IBotaniaRecipe {
    boolean recipeMatches(IItemHandler inv);
}
