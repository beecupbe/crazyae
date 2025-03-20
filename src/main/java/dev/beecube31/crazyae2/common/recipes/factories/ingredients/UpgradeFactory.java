package dev.beecube31.crazyae2.common.recipes.factories.ingredients;

import com.google.gson.JsonObject;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class UpgradeFactory implements IIngredientFactory {
	private static final String JSON_MATERIAL_KEY = "name";

	@Nonnull
	public Ingredient parse(JsonContext jsonContext, JsonObject jsonObject) {
		if (JsonUtils.isString(jsonObject, JSON_MATERIAL_KEY)) {
			final var upg = JsonUtils.getString(jsonObject, JSON_MATERIAL_KEY);
			var definition = CrazyAE.definitions().upgrades().getById(upg).orElse(null);
			if (definition != null) {
				return Ingredient.fromStacks(definition.maybeStack(1).orElse(ItemStack.EMPTY));
			}
		}
		return Ingredient.EMPTY;
	}
}
