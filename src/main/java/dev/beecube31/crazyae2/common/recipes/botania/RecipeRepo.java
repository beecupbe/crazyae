package dev.beecube31.crazyae2.common.recipes.botania;

import com.google.common.collect.ImmutableList;
import appeng.tile.inventory.AppEngInternalInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipe;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipes;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.item.ModItems;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeRepo {
    public static ArrayList<RecipeTerraplate> terraplateRecipes = new ArrayList<>();

    public static boolean isBotaniaTweaksLoaded() {
        return Loader.isModLoaded("botania_tweaks") || Loader.isModLoaded("botaniatweaks");
    }


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
                500000
        ));
    }

    public static Optional<RecipeTerraplate> findMatchingRecipe(IItemHandler inv) {
        for (RecipeTerraplate recipe : terraplateRecipes) {
            if (recipe.matches(inv)) {
                return Optional.of(recipe);
            }
        }

        if (isBotaniaTweaksLoaded()) {
            for (AgglomerationRecipe recipe : AgglomerationRecipes.recipes) {
                final RecipeTerraplate wrapped = new RecipeTerraplate(recipe);
                if (wrapped.matches(inv)) {
                    return Optional.of(wrapped);
                }
            }
        }

        final Optional<RecipeTerraplate> botanicAdditionsMatch = findMatchingBotanicAdditionsRecipe(inv);
        if (botanicAdditionsMatch.isPresent()) {
            return botanicAdditionsMatch;
        }

        return Optional.empty();
    }

    private static Optional<RecipeTerraplate> findMatchingBotanicAdditionsRecipe(IItemHandler inv) {
        if (!Loader.isModLoaded("botanicadds") || !(inv instanceof AppEngInternalInventory appEngInv)) {
            return Optional.empty();
        }

        try {
            final Class<?> gaiaRecipesClass = Class.forName("tk.zeitheron.botanicadds.api.GaiaPlateRecipes");
            final Field gaiaRecipesField = gaiaRecipesClass.getField("gaiaRecipes");
            final Object recipes = gaiaRecipesField.get(null);
            if (!(recipes instanceof List<?> recipeList)) {
                return Optional.empty();
            }

            for (Object recipeObj : recipeList) {
                if (!(recipeObj instanceof RecipePetals recipePetals)) {
                    continue;
                }

                if (!recipePetals.matches(appEngInv)) {
                    continue;
                }

                int manaCost = 0;
                try {
                    final Method getManaMethod = recipeObj.getClass().getMethod("getMana");
                    final Object manaObj = getManaMethod.invoke(recipeObj);
                    if (manaObj instanceof Number number) {
                        manaCost = number.intValue();
                    }
                } catch (ReflectiveOperationException ignored) {
                }

                return Optional.of(new RecipeTerraplate(recipePetals.getOutput().copy(), manaCost));
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return Optional.empty();
    }

    public static void add(RecipeTerraplate recipe) {
        terraplateRecipes.add(recipe);
    }

    public static void remove(RecipeTerraplate recipe) {
        terraplateRecipes.remove(recipe);
    }

    private static ItemStack manaResource(int meta) {
        return new ItemStack(ModItems.manaResource, 1, meta);
    }
}
