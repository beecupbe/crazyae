package dev.beecube31.crazyae2.client.rendering.models.crafting;//

import appeng.core.AppEng;
import com.google.common.collect.ImmutableList;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.blocks.BlockDenseCraftingUnit;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public
class DenseCraftingCubeModel implements IModel {
	private static final ResourceLocation RING_CORNER = texture("ring_corner");
	private static final ResourceLocation RING_SIDE_HOR = texture("ring_side_hor");
	private static final ResourceLocation RING_SIDE_VER = texture("ring_side_ver");
	private static final ResourceLocation UNIT_BASE = texture("unit_base");
	private static final ResourceLocation LIGHT_BASE = texture("light_base");
	private static final ResourceLocation MONITOR_BASE = texture("monitor_base");
	private static final ResourceLocation MONITOR_LIGHT_DARK = texture("monitor_light_dark");
	private static final ResourceLocation MONITOR_LIGHT_MEDIUM = texture("monitor_light_medium");
	private static final ResourceLocation MONITOR_LIGHT_BRIGHT = texture("monitor_light_bright");
	private static final ResourceLocation COPROCESSOR_4X_LIGHT = crazyAETexture("accelerator_4x_light");
	private static final ResourceLocation COPROCESSOR_16X_LIGHT = crazyAETexture("accelerator_16x_light");
	private static final ResourceLocation COPROCESSOR_64X_LIGHT = crazyAETexture("accelerator_64x_light");
	private static final ResourceLocation COPROCESSOR_256X_LIGHT = crazyAETexture("accelerator_256x_light");
	private static final ResourceLocation COPROCESSOR_1024X_LIGHT = crazyAETexture("accelerator_1024x_light");
	private static final ResourceLocation COPROCESSOR_4096X_LIGHT = crazyAETexture("accelerator_4096x_light");
	private static final ResourceLocation COPROCESSOR_16384X_LIGHT = crazyAETexture("accelerator_16384x_light");
	private static final ResourceLocation COPROCESSOR_65536X_LIGHT = crazyAETexture("accelerator_65536x_light");
	private static final ResourceLocation STORAGE_256K_LIGHT = crazyAETexture("crafting_storage_256k_light");
	private static final ResourceLocation STORAGE_1MB_LIGHT = crazyAETexture("crafting_storage_1mb_light");
	private static final ResourceLocation STORAGE_4MB_LIGHT = crazyAETexture("crafting_storage_4mb_light");
	private static final ResourceLocation STORAGE_16MB_LIGHT = crazyAETexture("crafting_storage_16mb_light");
	private static final ResourceLocation STORAGE_64MB_LIGHT = crazyAETexture("crafting_storage_64mb_light");
	private static final ResourceLocation STORAGE_256MB_LIGHT = crazyAETexture("crafting_storage_256mb_light");
	private static final ResourceLocation STORAGE_1024MB_LIGHT = crazyAETexture("crafting_storage_1gb_light");
	private static final ResourceLocation STORAGE_2048MB_LIGHT = crazyAETexture("crafting_storage_2gb_light");
	private final BlockDenseCraftingUnit.DenseCraftingUnitType type;

	public DenseCraftingCubeModel(BlockDenseCraftingUnit.DenseCraftingUnitType type) {
		this.type = type;
	}

	private static TextureAtlasSprite getLightTexture(Function<ResourceLocation, TextureAtlasSprite> textureGetter, BlockDenseCraftingUnit.DenseCraftingUnitType type) {
		return switch (type) {
			case STORAGE_256K -> textureGetter.apply(STORAGE_256K_LIGHT);
			case STORAGE_1024K -> textureGetter.apply(STORAGE_1MB_LIGHT);
			case STORAGE_4096K -> textureGetter.apply(STORAGE_4MB_LIGHT);
			case STORAGE_16384K -> textureGetter.apply(STORAGE_16MB_LIGHT);
			case STORAGE_65536K -> textureGetter.apply(STORAGE_64MB_LIGHT);
			case STORAGE_262144K -> textureGetter.apply(STORAGE_256MB_LIGHT);
			case STORAGE_1GB -> textureGetter.apply(STORAGE_1024MB_LIGHT);
			case STORAGE_2GB -> textureGetter.apply(STORAGE_2048MB_LIGHT);
			case COPROCESSOR_4X -> textureGetter.apply(COPROCESSOR_4X_LIGHT);
			case COPROCESSOR_16X -> textureGetter.apply(COPROCESSOR_16X_LIGHT);
			case COPROCESSOR_64X -> textureGetter.apply(COPROCESSOR_64X_LIGHT);
			case COPROCESSOR_256X -> textureGetter.apply(COPROCESSOR_256X_LIGHT);
			case COPROCESSOR_1024X -> textureGetter.apply(COPROCESSOR_1024X_LIGHT);
			case COPROCESSOR_4096X -> textureGetter.apply(COPROCESSOR_4096X_LIGHT);
			case COPROCESSOR_16384X -> textureGetter.apply(COPROCESSOR_16384X_LIGHT);
			case COPROCESSOR_65536X -> textureGetter.apply(COPROCESSOR_65536X_LIGHT);
		};
	}

	private static ResourceLocation texture(String name) {
		return new ResourceLocation(AppEng.MOD_ID, "blocks/crafting/" + name);
	}

	private static ResourceLocation crazyAETexture(String name) {
		return new ResourceLocation(Tags.MODID, "block/crafting/" + name);
	}

	public @NotNull Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	public @NotNull Collection<ResourceLocation> getTextures() {
		return ImmutableList.of(RING_CORNER,
			RING_SIDE_HOR,
			RING_SIDE_VER,
			UNIT_BASE,
			LIGHT_BASE,

			COPROCESSOR_4X_LIGHT,
			COPROCESSOR_16X_LIGHT,
			COPROCESSOR_64X_LIGHT,
			COPROCESSOR_256X_LIGHT,
			COPROCESSOR_1024X_LIGHT,
			COPROCESSOR_4096X_LIGHT,
			COPROCESSOR_16384X_LIGHT,
			COPROCESSOR_65536X_LIGHT,

			STORAGE_256K_LIGHT,
			STORAGE_1MB_LIGHT,
			STORAGE_4MB_LIGHT,
			STORAGE_16MB_LIGHT,
			STORAGE_64MB_LIGHT,
			STORAGE_256MB_LIGHT,
			STORAGE_1024MB_LIGHT,
			STORAGE_2048MB_LIGHT,

			MONITOR_BASE,
			MONITOR_LIGHT_DARK,
			MONITOR_LIGHT_MEDIUM, MONITOR_LIGHT_BRIGHT
		);
	}

	public @NotNull IBakedModel bake(@NotNull IModelState state, @NotNull VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		var ringCorner = bakedTextureGetter.apply(RING_CORNER);
		var ringSideHor = bakedTextureGetter.apply(RING_SIDE_HOR);
		var ringSideVer = bakedTextureGetter.apply(RING_SIDE_VER);

		return new DenseLightBakedModel(format,
			ringCorner,
			ringSideHor,
			ringSideVer,
			bakedTextureGetter.apply(LIGHT_BASE),
			getLightTexture(bakedTextureGetter, this.type)
		);
	}

	public @NotNull IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
}
