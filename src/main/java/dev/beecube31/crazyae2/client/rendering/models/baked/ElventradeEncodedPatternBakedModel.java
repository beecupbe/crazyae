package dev.beecube31.crazyae2.client.rendering.models.baked;

import com.google.common.collect.ImmutableMap;
import dev.beecube31.crazyae2.common.items.patterns.ElventradeEncodedPattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class ElventradeEncodedPatternBakedModel implements IBakedModel {
    private final IBakedModel baseModel;

    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

    private final CustomOverrideList overrides;

    public static final IItemColor PATTERN_ITEM_COLOR_HANDLER = (stack, tintIndex) -> {
        ElventradeEncodedPattern iep = (ElventradeEncodedPattern) stack.getItem();
        ItemStack output = iep.getOutput(stack);
        if (!output.isEmpty() && isShiftKeyDown()) {
            return Minecraft.getMinecraft().getItemColors().colorMultiplier(output, tintIndex);
        }
        return 0xFFFFFF;
    };

    public ElventradeEncodedPatternBakedModel(IBakedModel baseModel, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
        this.baseModel = baseModel;
        this.transforms = transforms;
        this.overrides = new CustomOverrideList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.baseModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        if (this.baseModel instanceof IBakedModel) {
            return this.baseModel.handlePerspective(cameraTransformType);
        }

        return PerspectiveMapWrapper.handlePerspective(this, this.transforms, cameraTransformType);
    }

    private static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    private class CustomOverrideList extends ItemOverrideList {

        CustomOverrideList() {
            super(ElventradeEncodedPatternBakedModel.this.baseModel.getOverrides().getOverrides());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if (isShiftKeyDown()) {
                ElventradeEncodedPattern iep = (ElventradeEncodedPattern) stack.getItem();
                ItemStack output = iep.getOutput(stack);
                if (!output.isEmpty()) {
                    return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(output, world, entity);
                }
            }

            return ElventradeEncodedPatternBakedModel.this.baseModel.getOverrides().handleItemState(originalModel, stack, world, entity);
        }
    }
}
