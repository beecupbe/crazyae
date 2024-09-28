package dev.beecube31.crazyae2.common.registration.registry;

import appeng.api.definitions.IItemDefinition;
import appeng.api.util.AEColor;
import appeng.api.util.AEColoredItemDefinition;
import appeng.bootstrap.IBootstrapComponent;
import appeng.core.features.ActivityState;
import appeng.core.features.ColoredItemDefinition;
import appeng.core.features.ItemStackSrc;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.registration.registry.builders.ICrazyAEBlockBuilder;
import dev.beecube31.crazyae2.common.registration.registry.builders.ICrazyAEItemBuilder;
import dev.beecube31.crazyae2.common.registration.registry.builders.CrazyAEBlockDefinitionBuilder;
import dev.beecube31.crazyae2.common.registration.registry.builders.CrazyAEItemDefinitionBuilder;
import dev.beecube31.crazyae2.common.registration.registry.components.CrazyAEBuiltInModelComponent;
import dev.beecube31.crazyae2.common.registration.registry.components.CrazyAEModelOverrideComponent;
import dev.beecube31.crazyae2.common.registration.registry.components.CrazyAETileEntityComponent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Registry {

	public final CrazyAETileEntityComponent tileEntityComponent;
	private final Map<Class<? extends IBootstrapComponent>, List<IBootstrapComponent>> bootstrapComponents;
	@SideOnly(Side.CLIENT)
	private CrazyAEModelOverrideComponent modelOverrideComponent;
	@SideOnly(Side.CLIENT)
	private CrazyAEBuiltInModelComponent builtInModelComponent;

	public Registry() {
		this.bootstrapComponents = new HashMap<>();

		this.tileEntityComponent = new CrazyAETileEntityComponent();
		this.addBootstrapComponent(this.tileEntityComponent);

		if (Platform.isClient()) {
			this.modelOverrideComponent = new CrazyAEModelOverrideComponent();
			this.addBootstrapComponent(this.modelOverrideComponent);

			this.builtInModelComponent = new CrazyAEBuiltInModelComponent();
			this.addBootstrapComponent(this.builtInModelComponent);
		}
	}

	public ICrazyAEBlockBuilder block(String id, Supplier<Block> block) {
		return new CrazyAEBlockDefinitionBuilder(this, id, block);
	}

	public ICrazyAEItemBuilder item(String id, Supplier<Item> item) {
		return new CrazyAEItemDefinitionBuilder(this, id, item);
	}

	public AEColoredItemDefinition colored(IItemDefinition target, int offset) {
		var definition = new ColoredItemDefinition();

		target.maybeItem().ifPresent(targetItem ->
		{
			for (final var color : AEColor.VALID_COLORS) {
				final var state = ActivityState.from(target.isEnabled());

				definition.add(color, new ItemStackSrc(targetItem, offset + color.ordinal(), state));
			}
		});

		return definition;
	}

	public void addBootstrapComponent(IBootstrapComponent component) {
		Arrays.stream(component.getClass().getInterfaces())
			.filter(IBootstrapComponent.class::isAssignableFrom)
			.forEach(i -> this.addBootstrapComponent((Class<? extends IBootstrapComponent>) i, component));
	}

	private <T extends IBootstrapComponent> void addBootstrapComponent(Class<? extends IBootstrapComponent> eventType,
	                                                                   T component) {
		this.bootstrapComponents.computeIfAbsent(eventType, c -> new ArrayList<>()).add(component);
	}

	@SideOnly(Side.CLIENT)
	public void addBuiltInModel(String path, IModel model) {
		this.builtInModelComponent.addModel(path, model);
	}

	@SideOnly(Side.CLIENT)
	public void addModelOverride(String resourcePath,
	                             BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer) {
		this.modelOverrideComponent.addOverride(resourcePath, customizer);
	}

	public <T extends IBootstrapComponent> Iterator<T> getBootstrapComponents(Class<T> eventType) {
		return (Iterator<T>) this.bootstrapComponents.getOrDefault(eventType, Collections.emptyList()).iterator();
	}
}