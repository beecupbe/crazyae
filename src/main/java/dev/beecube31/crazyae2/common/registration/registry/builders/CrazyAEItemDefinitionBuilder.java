package dev.beecube31.crazyae2.common.registration.registry.builders;

import appeng.bootstrap.IBootstrapComponent;
import appeng.bootstrap.ItemRenderingCustomizer;
import appeng.bootstrap.components.IItemRegistrationComponent;
import appeng.bootstrap.components.IPostInitComponent;
import appeng.core.features.ItemDefinition;
import appeng.util.Platform;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.features.IFeature;
import dev.beecube31.crazyae2.common.registration.definitions.CreativeTab;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEItemRendering;
import dev.beecube31.crazyae2.integrations.jei.JEIPlugin;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrazyAEItemDefinitionBuilder implements ICrazyAEItemBuilder {

	private final Registry registry;

	private final String registryName;

	private final Supplier<Item> itemSupplier;
	private final List<Function<Item, IBootstrapComponent>> boostrapComponents = new ArrayList<>();
	@Nullable
	private Features[] features = null;
	private Supplier<IBehaviorDispenseItem> dispenserBehaviorSupplier;

	@SideOnly(Side.CLIENT)
	private CrazyAEItemRendering itemRendering;

	private CreativeTabs creativeTab = CreativeTab.instance;

	private String requiredMod;
	private String bannedMod;


	private boolean hidden;

	public CrazyAEItemDefinitionBuilder(Registry registry, String registryName, Supplier<Item> itemSupplier) {
		this.registry = registry;
		this.registryName = registryName;
		this.itemSupplier = itemSupplier;
		if (Platform.isClient()) {
			this.itemRendering = new CrazyAEItemRendering();
		}
	}

	@Override
	public ICrazyAEItemBuilder bootstrap(Function<Item, IBootstrapComponent> component) {
		this.boostrapComponents.add(component);
		return this;
	}

	@Override
	public ICrazyAEItemBuilder features(Features... features) {
		this.features = features;
		return this;
	}

	@Override
	public ICrazyAEItemBuilder ifModPresent(String modid) {
		this.requiredMod = modid;
		return this;
	}

	@Override
	public ICrazyAEItemBuilder disableIfModPresent(String modid) {
		this.bannedMod = modid;
		return this;
	}

	@Override
	public ICrazyAEItemBuilder creativeTab(CreativeTabs tab) {
		this.creativeTab = tab;
		return this;
	}

	@Override
	public ICrazyAEItemBuilder rendering(ItemRenderingCustomizer callback) {
		if (Platform.isClient()) {
			this.customizeForClient(callback);
		}

		return this;
	}

	@Override
	public ICrazyAEItemBuilder dispenserBehavior(Supplier<IBehaviorDispenseItem> behavior) {
		this.dispenserBehaviorSupplier = behavior;
		return this;
	}

	@SideOnly(Side.CLIENT)
	private void customizeForClient(ItemRenderingCustomizer callback) {
		callback.customize(this.itemRendering);
	}

	@Override
	public ICrazyAEItemBuilder hide() {
		this.hidden = true;
		return this;
	}

	@Override
	public ItemDefinition build() {
		if (this.features != null && Arrays.stream(this.features).noneMatch(IFeature::isEnabled)) {
			return new ItemDefinition(this.registryName, null);
		}

		if (this.requiredMod != null) {
			if (Loader.isModLoaded(this.requiredMod)) {
				if (this.bannedMod == null || !Loader.isModLoaded(this.bannedMod)) {
					return this.buildItem();
				}
			}

			return new ItemDefinition(this.registryName, null);
		}

		if (this.bannedMod != null) {
			if (Loader.isModLoaded(this.bannedMod)) {
				return new ItemDefinition(this.registryName, null);
			}

			return this.buildItem();
		}

		return this.buildItem();
	}

	private ItemDefinition buildItem() {
		Item item = this.itemSupplier.get();
		item.setRegistryName(Tags.MODID, this.registryName);

		ItemDefinition definition = new ItemDefinition(this.registryName, item);

		item.setTranslationKey(Tags.MODID + "." + this.registryName);
		if (!this.hidden) {
			item.setCreativeTab(this.creativeTab);
		}

		// Register all extra handlers
		this.boostrapComponents.forEach(component -> this.registry.addBootstrapComponent(component.apply(item)));

		// Register custom dispenser behavior if requested
		if (this.dispenserBehaviorSupplier != null) {
			this.registry.addBootstrapComponent((IPostInitComponent) side ->
			{
				IBehaviorDispenseItem behavior = this.dispenserBehaviorSupplier.get();
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, behavior);
			});
		}

		this.registry.addBootstrapComponent((IItemRegistrationComponent) (side, reg) -> reg.register(item));

		if (Platform.isClient()) {
			if (this.hidden && Platform.isModLoaded("jei")) {
				JEIPlugin.hideItemFromJEI(definition);
			}
			this.itemRendering.apply(this.registry, item);
		}

		return definition;
	}
}