package dev.beecube31.crazyae2.common.recipes;

import appeng.api.AEApi;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class ManaPoolRecipesMaker {
    public static void make(@Nonnull ItemStack output, @Nonnull Object input, @Nonnegative int mana) {
        BotaniaAPI.manaInfusionRecipes.add(new RecipeManaInfusion(output, input, mana));
    }

    public static void init() {
        CrazyAE.definitions().materials().manaProcessor().maybeStack(1).ifPresent(manaProcessor -> {
            make(
                    manaProcessor,
                    AEApi.instance().definitions().materials().engProcessor().maybeStack(1).orElse(ItemStack.EMPTY),
                    1000
            );
        });
    }
}
