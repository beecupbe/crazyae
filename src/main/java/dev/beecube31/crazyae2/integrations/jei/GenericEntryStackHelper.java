package dev.beecube31.crazyae2.integrations.jei;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public final class GenericEntryStackHelper {
    private GenericEntryStackHelper() {}

    @Nullable
    public static <T> GenericStack ingredientToStack(IIngredientType<T> type, T ingredient) {
        if (ingredient instanceof ItemStack itemStack && type == VanillaTypes.ITEM_STACK) {
            return GenericStack.fromItemStack(itemStack);
        }
        if (ingredient instanceof FluidStack fluidStack && type == NeoForgeTypes.FLUID_STACK) {
            var key = AEFluidKey.of(fluidStack);
            return key != null ? new GenericStack(key, fluidStack.getAmount()) : null;
        }
        return null;
    }

    @Nullable
    public static ITypedIngredient<?> stackToIngredient(IIngredientManager manager, GenericStack stack) {
        if (stack.what() instanceof AEItemKey itemKey) {
            return manager.createTypedIngredient(VanillaTypes.ITEM_STACK,
                    itemKey.toStack((int) Math.max(1, Math.min(Integer.MAX_VALUE, stack.amount())))).orElse(null);
        }
        if (stack.what() instanceof AEFluidKey fluidKey) {
            return manager.createTypedIngredient(NeoForgeTypes.FLUID_STACK,
                    fluidKey.toStack((int) Math.max(1, Math.min(Integer.MAX_VALUE, stack.amount())))).orElse(null);
        }
        return null;
    }
}
