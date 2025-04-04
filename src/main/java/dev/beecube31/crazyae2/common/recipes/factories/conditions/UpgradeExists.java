package dev.beecube31.crazyae2.common.recipes.factories.conditions;

import com.google.gson.JsonObject;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class UpgradeExists implements IConditionFactory {

	private static final String JSON_MATERIAL_KEY = "name";

	@Override
	public BooleanSupplier parse(JsonContext jsonContext, JsonObject jsonObject) {
		final boolean result;

		if (JsonUtils.isString(jsonObject, JSON_MATERIAL_KEY)) {
			final var upg = JsonUtils.getString(jsonObject, JSON_MATERIAL_KEY);
			var definition = CrazyAE.definitions().upgrades().getById(upg).orElse(null);
			result = definition != null && definition.isEnabled();
		} else {
			result = false;
		}

		return () -> result;
	}
}