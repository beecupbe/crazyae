package dev.beecube31.crazyae2.client.rendering.models.baked;

import appeng.block.storage.DriveSlotState;
import appeng.block.storage.DriveSlotsState;
import dev.beecube31.crazyae2.common.blocks.storage.BlockDriveImproved;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImprovedDriveBakedModel implements IBakedModel {
    private final IBakedModel bakedBase;
    private final Map<DriveSlotState, IBakedModel> bakedCells;

    public ImprovedDriveBakedModel(IBakedModel bakedBase, Map<DriveSlotState, IBakedModel> bakedCells) {
        this.bakedBase = bakedBase;
        this.bakedCells = bakedCells;
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> result = new ArrayList<>(this.bakedBase.getQuads(state, side, rand));

        if (side == null && state instanceof IExtendedBlockState extState) {

            if (!extState.getUnlistedNames().contains(BlockDriveImproved.SLOTS_STATE)) {
                return result;
            }

            DriveSlotsState slotsState = extState.getValue(BlockDriveImproved.SLOTS_STATE);

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 7; col++) {
                    DriveSlotState slotState = slotsState.getState(row * 7 + col);

                    IBakedModel bakedCell = this.bakedCells.get(slotState);

                    Matrix4f transform = new Matrix4f();
                    transform.setIdentity();

                    float xOffset = -col * 2 / 16.0f;
                    float yOffset = -row * 3 / 16.0f;

                    transform.setTranslation(new Vector3f(xOffset, yOffset, 0));

                    MatrixVertexTransformer transformer = new MatrixVertexTransformer(transform);
                    for (BakedQuad bakedQuad : bakedCell.getQuads(state, null, rand)) {
                        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(bakedQuad.getFormat());
                        transformer.setParent(builder);
                        transformer.setVertexFormat(builder.getVertexFormat());
                        bakedQuad.pipe(transformer);
                        result.add(builder.build());
                    }
                }
            }
        }
        return result;
    }


    @Override
    public boolean isAmbientOcclusion() {
        return this.bakedBase.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.bakedBase.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.bakedBase.isGui3d();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleTexture() {
        return this.bakedBase.getParticleTexture();
    }

    @Override
    public @NotNull ItemCameraTransforms getItemCameraTransforms() {
        return this.bakedBase.getItemCameraTransforms();
    }

    @Override
    public @NotNull ItemOverrideList getOverrides() {
        return this.bakedBase.getOverrides();
    }
}
