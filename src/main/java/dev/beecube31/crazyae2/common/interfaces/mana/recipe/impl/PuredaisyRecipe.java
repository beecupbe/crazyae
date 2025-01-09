package dev.beecube31.crazyae2.common.interfaces.mana.recipe.impl;

import dev.beecube31.crazyae2.common.interfaces.mana.recipe.IBotaniaRecipe;
import net.minecraft.block.state.IBlockState;
import vazkii.botania.api.recipe.RecipePureDaisy;

public class PuredaisyRecipe extends RecipePureDaisy implements IBotaniaRecipe {
    public PuredaisyRecipe(Object input, IBlockState state) {
        super(input, state);
    }

    public PuredaisyRecipe(Object input, IBlockState state, int time) {
        super(input, state, time);
    }
}
