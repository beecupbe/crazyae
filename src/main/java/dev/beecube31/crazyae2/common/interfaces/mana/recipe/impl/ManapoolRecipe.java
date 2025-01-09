package dev.beecube31.crazyae2.common.interfaces.mana.recipe.impl;

import dev.beecube31.crazyae2.common.interfaces.mana.recipe.IManapoolRecipe;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.recipe.RecipeManaInfusion;

public class ManapoolRecipe extends RecipeManaInfusion implements IManapoolRecipe {
    public ManapoolRecipe(ItemStack output, Object input) {
        super(output, input, -1);
    }

    @Override
    public boolean recipeMatches(ItemStack stack) {
        return this.matches(stack);
    }
}
