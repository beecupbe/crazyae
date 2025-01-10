package dev.beecube31.crazyae2.integrations.jei;

import appeng.api.definitions.IItemDefinition;
import dev.beecube31.crazyae2.client.gui.CrazyAEGuiHandler;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
    public static IJeiRuntime runtime;
    public static CrazyAEGuiHandler aeGuiHandler;

    private static final List<IItemDefinition> disabledItems = new ArrayList<>();

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {}

    @Override
    public void register(@NotNull IModRegistry registry) {
        IModPlugin.super.register(registry);
        aeGuiHandler = new CrazyAEGuiHandler();
        registry.addAdvancedGuiHandlers(aeGuiHandler);
        registry.addGhostIngredientHandler(aeGuiHandler.getGuiContainerClass(), aeGuiHandler);

        disabledItems.forEach(item -> {
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(item.maybeStack(1).get());
        });

//        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RecipeTransferHandler<>(ContainerMechanicalBotaniaTileBase.class), Constants.UNIVERSAL_RECIPE_TRANSFER_UID);
    }

    public static void hideItemFromJEI(IItemDefinition is) {
        disabledItems.add(is);
    }
}
