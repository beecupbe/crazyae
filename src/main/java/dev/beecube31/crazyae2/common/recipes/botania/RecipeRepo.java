package dev.beecube31.crazyae2.common.recipes.botania;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipe;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipes;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;
import java.util.Optional;

public class RecipeRepo {
    public static ArrayList<RecipeTerraplate> terraplateRecipes = new ArrayList<>();


    public static void copyFromBotaniaTweaks() {
        terraplateRecipes.clear();

        for (AgglomerationRecipe s : AgglomerationRecipes.recipes) {
            terraplateRecipes.add(new RecipeTerraplate(s));
        }
    }

    public static void addDefaultTerrasteelRecipe() {
        terraplateRecipes.add(new RecipeTerraplate(
                ImmutableList.of(manaResource(2), manaResource(0), manaResource(1)),
                manaResource(4),
                500000,
                0x0000FF,
                0x00FF00
        ));
    }

    public static Optional<RecipeTerraplate> findMatchingRecipe(IItemHandler inv) {
        for (RecipeTerraplate recipe : terraplateRecipes) {
            if(recipe.matches(inv))
                return Optional.of(recipe);
        }
        return Optional.empty();
    }

    private static ItemStack manaResource(int meta) {
        return new ItemStack(ModItems.manaResource, 1, meta);
    }
}
