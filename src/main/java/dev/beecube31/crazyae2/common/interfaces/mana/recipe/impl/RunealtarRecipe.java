package dev.beecube31.crazyae2.common.interfaces.mana.recipe.impl;

import dev.beecube31.crazyae2.common.interfaces.mana.recipe.IRunealtarRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.recipe.RecipeRuneAltar;

public class RunealtarRecipe extends RecipeRuneAltar implements IRunealtarRecipe {
    public RunealtarRecipe(ItemStack output, int mana, Object... inputs) {
        super(output, mana, inputs);
    }

    @Override
    public boolean recipeMatches(IItemHandler inv) {
        return this.matches(inv);
    }
}
