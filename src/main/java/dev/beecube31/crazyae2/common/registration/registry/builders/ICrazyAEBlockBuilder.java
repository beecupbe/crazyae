package dev.beecube31.crazyae2.common.registration.registry.builders;

import appeng.api.definitions.IBlockDefinition;
import appeng.bootstrap.BlockRenderingCustomizer;
import appeng.bootstrap.IBootstrapComponent;
import appeng.bootstrap.definitions.TileEntityDefinition;
import appeng.core.features.AEFeature;
import dev.beecube31.crazyae2.common.features.IFeature;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICrazyAEBlockBuilder {
	ICrazyAEBlockBuilder bootstrap(BiFunction<Block, Item, IBootstrapComponent> var1);

	ICrazyAEBlockBuilder features(IFeature... var1);

	ICrazyAEBlockBuilder aeFeatures(AEFeature... var1);

	ICrazyAEBlockBuilder rendering(BlockRenderingCustomizer var1);

	ICrazyAEBlockBuilder tileEntity(TileEntityDefinition var1);

	ICrazyAEBlockBuilder disableItem();

	ICrazyAEBlockBuilder useCustomItemModel();

	ICrazyAEBlockBuilder ifModPresent(String modid);

	ICrazyAEBlockBuilder disableIfModPresent(String modid);

	ICrazyAEBlockBuilder item(Function<Block, ItemBlock> var1);

	<T extends IBlockDefinition> T build();

	ICrazyAEBlockBuilder withJEIDescription();
}
