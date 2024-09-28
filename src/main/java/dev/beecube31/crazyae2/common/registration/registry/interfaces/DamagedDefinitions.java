package dev.beecube31.crazyae2.common.registration.registry.interfaces;

import appeng.api.definitions.IItemDefinition;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEIModelProvider;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface DamagedDefinitions<T extends IItemDefinition, U extends CrazyAEIModelProvider> extends Definitions<T> {
	Collection<U> getEntries();

	@Nullable
	U getType(ItemStack is);
}
