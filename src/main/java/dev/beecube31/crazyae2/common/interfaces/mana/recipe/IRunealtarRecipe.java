package dev.beecube31.crazyae2.common.interfaces.mana.recipe;

import net.minecraftforge.items.IItemHandler;

public interface IRunealtarRecipe extends IBotaniaRecipe {
    boolean recipeMatches(IItemHandler inv);
}
