package dev.beecube31.crazyae2.client.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import java.util.Arrays;

public final class CrazyCraftingCubeModel {
    public static final Identifier ID = CrazyAE.makeId("crafting_cube");

    private static final Codec<CrazyAECraftingUnitType> UNIT_TYPE_CODEC = Codec.STRING.xmap(
            name -> Arrays.stream(CrazyAECraftingUnitType.values())
                    .filter(t -> t.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown CrazyAE crafting unit type: " + name)),
            CrazyAECraftingUnitType::getName);

    private CrazyCraftingCubeModel() {}

    public record Unbaked(CrazyAECraftingUnitType type) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                UNIT_TYPE_CODEC.fieldOf("unit_type").forGetter(Unbaked::type))
                .apply(instance, Unbaked::new));

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new CrazyCraftingBlocksModelProvider(type).bake(baker.materials());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
        }

        @Override
        public MapCodec<Unbaked> codec() {
            return MAP_CODEC;
        }
    }
}
