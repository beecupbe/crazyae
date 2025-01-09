package dev.beecube31.crazyae2.common.interfaces.mana.recipe.impl;

import dev.beecube31.crazyae2.common.interfaces.mana.recipe.IPetalRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.recipe.RecipePetals;

public class PetalRecipe extends RecipePetals implements IPetalRecipe {
    public PetalRecipe(ItemStack output, Object... inputs) {
        super(output, inputs);
    }

    @Override
    public boolean recipeMatches(IItemHandler inv) {
        return this.matches(inv);
    }
}
