package dev.beecube31.crazyae2.common.interfaces.mana.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IElventradeRecipe extends IBotaniaRecipe {
    boolean recipeMatches(List<ItemStack> stacks, boolean remove);
}
