package dev.beecube31.crazyae2.client.render;

import appeng.client.render.BasicUnbakedModel;
import dev.beecube31.crazyae2.common.enums.DriveSlotState;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class ImprovedDriveModel implements BasicUnbakedModel {
    public static final ResourceLocation ID = CrazyAE.makeId("block/improved_drive");
    private static final ResourceLocation MODEL_BASE = CrazyAE.makeId("block/improved_drive_base");

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter,
                           @NotNull ModelState modelTransform, @NotNull ResourceLocation modelLocation) {
        var base = baker.bake(MODEL_BASE, modelTransform);
        var cells = new EnumMap<DriveSlotState, BakedModel>(DriveSlotState.class);
        for (var s : DriveSlotState.values()) {
            cells.put(s, baker.bake(s.model, modelTransform));
        }
        return new ImprovedDriveBakedModel(modelTransform.getRotation(), base, cells);
    }

    @Override
    public @NotNull Collection<ResourceLocation> getDependencies() {
        List<ResourceLocation> deps = new ArrayList<>();
        deps.add(MODEL_BASE);
        for (var s : DriveSlotState.values()) {
            deps.add(s.model);
        }
        return deps;
    }
}
