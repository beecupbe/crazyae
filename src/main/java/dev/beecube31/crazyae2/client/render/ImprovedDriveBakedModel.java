package dev.beecube31.crazyae2.client.render;

import appeng.client.render.DelegateBakedModel;
import com.mojang.math.Transformation;
import dev.beecube31.crazyae2.common.enums.DriveSlotState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImprovedDriveBakedModel extends DelegateBakedModel {
    public static final int COLS = 7;
    public static final int ROWS = 5;

    private final List<BakedQuad>[][] slotQuads;

    @SuppressWarnings("unchecked")
    public ImprovedDriveBakedModel(Transformation rotation, BakedModel base, Map<DriveSlotState, BakedModel> cells) {
        super(base);
        var states = DriveSlotState.values();
        slotQuads = new List[COLS * ROWS][states.length];
        var rand = RandomSource.create(42);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                var offset = new Vector3f(-col * 2 / 16.0f, -row * 3 / 16.0f, 0);
                rotation.getLeftRotation().transform(offset);
                for (var s : states) {
                    var src = cells.get(s).getQuads(null, null, rand, ModelData.EMPTY, null);
                    var moved = new ArrayList<BakedQuad>(src.size());
                    for (var q : src) {
                        moved.add(translate(q, offset));
                    }
                    slotQuads[row * COLS + col][s.ordinal()] = moved;
                }
            }
        }
    }

    private static BakedQuad translate(BakedQuad quad, Vector3f offset) {
        int[] data = Arrays.copyOf(quad.getVertices(), quad.getVertices().length);
        int stride = data.length / 4;
        for (int v = 0; v < 4; v++) {
            int i = v * stride;
            data[i] = Float.floatToRawIntBits(Float.intBitsToFloat(data[i]) + offset.x());
            data[i + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(data[i + 1]) + offset.y());
            data[i + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(data[i + 2]) + offset.z());
        }
        return new BakedQuad(data, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(),
                quad.hasAmbientOcclusion());
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
                                             @NotNull ModelData extraData, @Nullable RenderType renderType) {
        var base = super.getQuads(state, side, rand, extraData, renderType);
        var slots = extraData.get(DriveSlotState.SLOTS);
        if (side != null || slots == null) {
            return base;
        }
        var result = new ArrayList<BakedQuad>(base);
        for (int i = 0; i < Math.min(slots.length, slotQuads.length); i++) {
            result.addAll(slotQuads[i][slots[i].ordinal()]);
        }
        return result;
    }
}