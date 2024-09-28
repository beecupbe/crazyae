package dev.beecube31.crazyae2.common.registration.registry.builders;

import appeng.api.definitions.IBlockDefinition;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseItemBlock;
import appeng.block.AEBaseTileBlock;
import appeng.bootstrap.BlockRenderingCustomizer;
import appeng.bootstrap.IBlockRendering;
import appeng.bootstrap.IBootstrapComponent;
import appeng.bootstrap.IItemRendering;
import appeng.bootstrap.components.IBlockRegistrationComponent;
import appeng.bootstrap.components.IItemRegistrationComponent;
import appeng.bootstrap.components.IPreInitComponent;
import appeng.bootstrap.definitions.TileEntityDefinition;
import appeng.core.features.*;
import appeng.tile.AEBaseTile;
import appeng.util.Platform;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.IFeature;
import dev.beecube31.crazyae2.common.registration.definitions.CreativeTab;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEBlockRendering;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEItemRendering;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrazyAEBlockDefinitionBuilder implements ICrazyAEBlockBuilder {
	private final Registry registry;

	private final String registryName;

	private final Supplier<? extends Block> blockSupplier;

	private final List<BiFunction<Block, Item, IBootstrapComponent>> bootstrapComponents = new ArrayList<>();
	private final CreativeTabs creativeTab = CreativeTab.instance;
	@Nullable private IFeature[] features = null;
	@Nullable private AEFeature[] aeFeatures = null;
	private TileEntityDefinition tileEntityDefinition;

	private boolean disableItem = false;

	private Function<Block, ItemBlock> itemFactory;

	private String requiredMod;
	private String bannedMod;

	@SideOnly(Side.CLIENT)
	private CrazyAEBlockRendering blockRendering;

	@SideOnly(Side.CLIENT)
	private CrazyAEItemRendering itemRendering;

	@SideOnly(Side.CLIENT)
	private boolean jeiDescription;

	public CrazyAEBlockDefinitionBuilder(Registry registry, String id, Supplier<? extends Block> blockSupplier) {
		this.registry = registry;
		this.registryName = id;
		this.blockSupplier = blockSupplier;

		if (Platform.isClient()) {
			this.blockRendering = new CrazyAEBlockRendering();
			this.itemRendering = new CrazyAEItemRendering();
		}
	}

	@Override
	public CrazyAEBlockDefinitionBuilder bootstrap(BiFunction<Block, Item, IBootstrapComponent> callback) {
		this.bootstrapComponents.add(callback);
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder features(IFeature... features) {
		this.features = features;
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder aeFeatures(AEFeature... features) {
		this.aeFeatures = features;
		return this;
	}

	@Override
	public CrazyAEBlockDefinitionBuilder rendering(BlockRenderingCustomizer callback) {
		if (Platform.isClient()) {
			this.customizeForClient(callback);
		}

		return this;
	}

	@Override
	public ICrazyAEBlockBuilder tileEntity(TileEntityDefinition tileEntityDefinition) {
		this.tileEntityDefinition = tileEntityDefinition;
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder useCustomItemModel() {
		this.rendering(new BlockRenderingCustomizer() {
			@Override
			@SideOnly(Side.CLIENT)
			public void customize(IBlockRendering rendering, IItemRendering itemRendering) {
				var model = new ModelResourceLocation(new ResourceLocation(Tags.MODID,
					CrazyAEBlockDefinitionBuilder.this.registryName), "inventory");
				itemRendering.model(model).variants(model);
			}
		});

		return this;
	}

	@Override
	public ICrazyAEBlockBuilder ifModPresent(String modid) {
		this.requiredMod = modid;
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder disableIfModPresent(String modid) {
		this.bannedMod = modid;
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder item(Function<Block, ItemBlock> factory) {
		this.itemFactory = factory;
		return this;
	}

	@Override
	public ICrazyAEBlockBuilder disableItem() {
		this.disableItem = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	private void customizeForClient(BlockRenderingCustomizer callback) {
		callback.customize(this.blockRendering, this.itemRendering);
	}

	@Override
	public ICrazyAEBlockBuilder withJEIDescription() {
		if (Platform.isClient()) {
			this.jeiDescription = true;
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IBlockDefinition> T build() {
		if (this.features != null && Arrays.stream(this.features).noneMatch(IFeature::isEnabled)
			|| (this.aeFeatures != null && Arrays.stream(this.aeFeatures).noneMatch(AEFeature::isEnabled)))
		{
			return (T) new TileDefinition(this.registryName, null, null);
		}

		if (this.requiredMod != null) {
			if (Loader.isModLoaded(this.requiredMod)) {
				if (this.bannedMod == null || !Loader.isModLoaded(this.bannedMod)) {
					return this.buildBlock();
				}
			}

			return (T) new TileDefinition(this.registryName, null, null);
		}

		if (this.bannedMod != null) {
			if (Loader.isModLoaded(this.bannedMod)) {
				return (T) new TileDefinition(this.registryName, null, null);
			}

			return this.buildBlock();
		}

		return this.buildBlock();
	}

	private <T extends IBlockDefinition> T buildBlock() {
		var block = this.blockSupplier.get();
		block.setRegistryName(Tags.MODID, this.registryName);
		block.setTranslationKey(Tags.MODID + "." + this.registryName);

		var item = this.constructItemFromBlock(block);
		if (item != null) {
			item.setRegistryName(Tags.MODID, this.registryName);
		}

		this.registry.addBootstrapComponent((IBlockRegistrationComponent) (side, registry) -> registry.register(block));
		if (item != null) {
			this.registry.addBootstrapComponent((IItemRegistrationComponent) (side, registry) -> registry.register(item));
		}

		block.setCreativeTab(this.creativeTab);

		this.bootstrapComponents.forEach(component -> this.registry.addBootstrapComponent(component.apply(block,
				item)));

		if (this.tileEntityDefinition != null && block instanceof AEBaseTileBlock) {
			((AEBaseTileBlock) block).setTileEntity(this.tileEntityDefinition.getTileEntityClass());
			if (this.tileEntityDefinition.getName() == null) {
				this.tileEntityDefinition.setName(this.registryName);
			}

		}

		if (Platform.isClient()) {
			if (block instanceof AEBaseTileBlock tileBlock) {
				this.blockRendering.apply(this.registry, block, tileBlock.getTileEntityClass());
			} else {
				this.blockRendering.apply(this.registry, block, null);
			}

			if (item != null) {
				this.itemRendering.apply(this.registry, item);
			}
		}

		final T definition;

		if (block instanceof AEBaseTileBlock) {
			this.registry.addBootstrapComponent((IPreInitComponent) side ->
					AEBaseTile.registerTileItem(
							this.tileEntityDefinition == null ? ((AEBaseTileBlock) block).getTileEntityClass() :
									this.tileEntityDefinition.getTileEntityClass(),
							new BlockStackSrc(block, 0, ActivityState.Enabled)));

			if (this.tileEntityDefinition != null) {
				this.registry.tileEntityComponent.addTileEntity(this.tileEntityDefinition);
			}

			definition = (T) new TileDefinition(this.registryName, (AEBaseTileBlock) block, item);
		} else {
			definition = (T) new BlockDefinition(this.registryName, block, item);
		}

		return definition;
	}

	@Nullable
	private ItemBlock constructItemFromBlock(Block block) {
		if (this.disableItem) {
			return null;
		}

		if (this.itemFactory != null) {
			return this.itemFactory.apply(block);
		} else if (block instanceof AEBaseBlock) {
			return new AEBaseItemBlock(block);
		} else {
			return new ItemBlock(block);
		}
	}
}
