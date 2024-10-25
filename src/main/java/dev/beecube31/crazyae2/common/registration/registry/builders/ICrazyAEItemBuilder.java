package dev.beecube31.crazyae2.common.registration.registry.builders;

import appeng.bootstrap.IBootstrapComponent;
import appeng.bootstrap.ItemRenderingCustomizer;
import appeng.core.features.ItemDefinition;
import dev.beecube31.crazyae2.common.features.Features;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ICrazyAEItemBuilder {
	ICrazyAEItemBuilder bootstrap(Function<Item, IBootstrapComponent> var1);

	ICrazyAEItemBuilder features(Features... var1);

	ICrazyAEItemBuilder ifModPresent(String modid);

	ICrazyAEItemBuilder disableIfModPresent(String modid);

	ICrazyAEItemBuilder creativeTab(CreativeTabs var1);

	ICrazyAEItemBuilder rendering(ItemRenderingCustomizer var1);

	ICrazyAEItemBuilder dispenserBehavior(Supplier<IBehaviorDispenseItem> var1);

	ItemDefinition build();

	ICrazyAEItemBuilder hide();
}
