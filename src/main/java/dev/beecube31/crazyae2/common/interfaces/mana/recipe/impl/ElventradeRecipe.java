package dev.beecube31.crazyae2.common.interfaces.mana.recipe.impl;

import dev.beecube31.crazyae2.common.interfaces.mana.recipe.IElventradeRecipe;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.recipe.RecipeElvenTrade;

import java.util.List;

public class ElventradeRecipe extends RecipeElvenTrade implements IElventradeRecipe {
    public ElventradeRecipe(ItemStack[] outputs, Object... inputs) {
        super(outputs, inputs);
    }

    @Override
    public boolean recipeMatches(List<ItemStack> stacks, boolean remove) {
        return this.matches(stacks, remove);
    }
}
