package dev.beecube31.crazyae2.client.render;

import appeng.client.model.SpinnableVariant;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import dev.beecube31.crazyae2.common.enums.DriveSlotState;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.joml.Vector3f;

import java.util.List;

public class ImprovedDriveModel implements DynamicBlockStateModel {
    public static final int COLS = 7;
    public static final int ROWS = 5;

    public static final Identifier ID = CrazyAE.makeId("improved_drive");
    private static final Identifier MODEL_BASE = CrazyAE.makeId("block/improved_drive_base");

    private final BlockStateModelPart baseModel;
    private final BlockStateModelPart[][] slotModels;

    public ImprovedDriveModel(BlockStateModelPart baseModel, BlockStateModelPart[][] slotModels) {
        this.baseModel = baseModel;
        this.slotModels = slotModels;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
                             List<BlockStateModelPart> parts) {
        parts.add(baseModel);

        var slots = level.getModelData(pos).get(DriveSlotState.SLOTS);
        if (slots == null) {
            return;
        }
        for (int i = 0; i < Math.min(slots.length, slotModels.length); i++) {
            var slotState = slots[i];
            if (slotState != null) {
                parts.add(slotModels[i][slotState.ordinal()]);
            }
        }
    }

    @Override
    public Material.Baked particleMaterial() {
        return baseModel.particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return baseModel.materialFlags();
    }

    public record Unbaked(SpinnableVariant variant) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<Unbaked> MAP_CODEC = SpinnableVariant.MAP_CODEC.xmap(Unbaked::new, Unbaked::variant);

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return MAP_CODEC;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            ModelState modelState = variant.modelState().asModelState();
            var states = DriveSlotState.values();

            var slotModels = new BlockStateModelPart[COLS * ROWS][states.length];
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    var offset = new Vector3f(-col * 2 / 16.0f, -row * 3 / 16.0f, 0);
                    var slotState = new ComposedModelState(modelState, new Transformation(offset, null, null, null));
                    for (var s : states) {
                        slotModels[row * COLS + col][s.ordinal()] = SimpleModelWrapper.bake(baker, s.model, slotState);
                    }
                }
            }

            var baseModel = SimpleModelWrapper.bake(baker, MODEL_BASE, modelState);
            return new ImprovedDriveModel(baseModel, slotModels);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(MODEL_BASE);
            for (var s : DriveSlotState.values()) {
                resolver.markDependency(s.model);
            }
        }
    }
}
