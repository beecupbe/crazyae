package dev.beecube31.crazyae2.common.recipes.botania;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
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

    public static boolean isGodAgglomerationPlateLoaded() {
        return Loader.isModLoaded("godagglomerationplate");
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
        final IItemHandler compactedInv = compactInventory(inv);

        for (RecipeTerraplate recipe : terraplateRecipes) {
            if (recipe.matches(compactedInv)) {
                return Optional.of(recipe);
            }
        }

        if (isBotaniaTweaksLoaded()) {
            for (AgglomerationRecipe recipe : AgglomerationRecipes.recipes) {
                final RecipeTerraplate wrapped = new RecipeTerraplate(recipe);
                if (wrapped.matches(compactedInv)) {
                    return Optional.of(wrapped);
                }
            }
        }

        final Optional<RecipeTerraplate> botanicAdditionsMatch = findMatchingBotanicAdditionsRecipe(compactedInv);
        if (botanicAdditionsMatch.isPresent()) {
            return botanicAdditionsMatch;
        }

        final Optional<RecipeTerraplate> godAgglomerationMatch = findMatchingGodAgglomerationRecipe(compactedInv);
        if (godAgglomerationMatch.isPresent()) {
            return godAgglomerationMatch;
        }

        return Optional.empty();
    }

    private static Optional<RecipeTerraplate> findMatchingBotanicAdditionsRecipe(IItemHandler inv) {
        if (!Loader.isModLoaded("botanicadds")) {
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

                if (!recipePetals.matches(inv)) {
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

                return Optional.of(new RecipeTerraplate(recipePetals.getOutput().copy(), manaCost, RecipeTerraplate.PlateType.GAIA_PLATE));
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return Optional.empty();
    }

    private static Optional<RecipeTerraplate> findMatchingGodAgglomerationRecipe(IItemHandler inv) {
        if (!isGodAgglomerationPlateLoaded()) {
            return Optional.empty();
        }

        try {
            final Class<?> recipesClass = Class.forName("com.wdcftgg.godagglomerationplate.recipes.GodAgglomerationRecipes");
            final Field recipesField = recipesClass.getField("recipes");
            final Object recipes = recipesField.get(null);
            if (!(recipes instanceof List<?> recipeList)) {
                return Optional.empty();
            }

            for (Object recipeObj : recipeList) {
                final Field stacksField = recipeObj.getClass().getField("recipeStacks");
                final Field oreKeysField = recipeObj.getClass().getField("recipeOreKeys");
                final Field outputField = recipeObj.getClass().getField("recipeOutput");
                final Field manaField = recipeObj.getClass().getField("manaCost");

                final Object stacksObj = stacksField.get(recipeObj);
                final Object oreKeysObj = oreKeysField.get(recipeObj);
                final Object outputObj = outputField.get(recipeObj);
                final Object manaObj = manaField.get(recipeObj);

                if (!(stacksObj instanceof List<?> stackList)
                        || !(oreKeysObj instanceof List<?> oreKeyList)
                        || !(outputObj instanceof ItemStack outputStack)
                        || !(manaObj instanceof Number manaNumber)) {
                    continue;
                }

                final ArrayList<Object> inputs = new ArrayList<>();
                for (Object stack : stackList) {
                    if (stack instanceof ItemStack itemStack) {
                        inputs.add(itemStack.copy());
                    }
                }
                for (Object oreKey : oreKeyList) {
                    if (oreKey instanceof String key) {
                        inputs.add(key);
                    }
                }

                final RecipeTerraplate wrapped = new RecipeTerraplate(
                        ImmutableList.copyOf(inputs),
                        outputStack.copy(),
                        manaNumber.intValue(),
                        RecipeTerraplate.PlateType.GOD_AGGLOMERATION_PLATE
                );
                if (wrapped.matches(inv)) {
                    return Optional.of(wrapped);
                }
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

    private static IItemHandler compactInventory(IItemHandler inv) {
        ItemStackHandler compacted = new ItemStackHandler(inv.getSlots());
        int index = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                compacted.setStackInSlot(index++, stack.copy());
            }
        }
        return compacted;
    }
}
