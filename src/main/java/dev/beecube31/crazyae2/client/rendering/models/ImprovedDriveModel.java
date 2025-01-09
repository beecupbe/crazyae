package dev.beecube31.crazyae2.client.rendering.models;

import appeng.block.storage.DriveSlotState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.beecube31.crazyae2.client.rendering.models.baked.ImprovedDriveBakedModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class ImprovedDriveModel implements IModel {

    private static final ResourceLocation MODEL_BASE = new ResourceLocation("crazyae:block/driveimp_base");

    private static final Map<DriveSlotState, ResourceLocation> MODELS_CELLS = ImmutableMap.<DriveSlotState, ResourceLocation>builder()
            .put(DriveSlotState.EMPTY, new ResourceLocation("crazyae:block/drive_cells/drive_cell_empty"))
            .put(DriveSlotState.OFFLINE, new ResourceLocation("crazyae:block/drive_cells/drive_cell_off"))
            .put(DriveSlotState.ONLINE, new ResourceLocation("crazyae:block/drive_cells/drive_cell_on"))
            .put(DriveSlotState.TYPES_FULL, new ResourceLocation("crazyae:block/drive_cells/drive_cell_types_full"))
            .put(DriveSlotState.FULL, new ResourceLocation("crazyae:block/drive_cells/drive_cell_full"))
            .put(DriveSlotState.NO_CONTENTS, new ResourceLocation("crazyae:block/drive_cells/drive_cell_no_contents"))
            .build();


    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.<ResourceLocation>builder().add(MODEL_BASE).addAll(MODELS_CELLS.values()).build();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        EnumMap<DriveSlotState, IBakedModel> cellModels = new EnumMap<>(DriveSlotState.class);

        // Load the base model and the model for each cell state.
        IModel baseModel;
        try {
            baseModel = ModelLoaderRegistry.getModel(MODEL_BASE);
            for (DriveSlotState slotState : MODELS_CELLS.keySet()) {
                IModel model = ModelLoaderRegistry.getModel(MODELS_CELLS.get(slotState));
                cellModels.put(slotState, model.bake(state, format, bakedTextureGetter));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        IBakedModel bakedBase = baseModel.bake(state, format, bakedTextureGetter);
        return new ImprovedDriveBakedModel(bakedBase, cellModels);
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
