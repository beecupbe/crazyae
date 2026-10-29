package dev.beecube31.crazyae2.core.registries.client;

import appeng.client.renderer.blockentity.CrankRenderer;
import dev.beecube31.crazyae2.client.render.CrazyCraftingCubeModel;
import dev.beecube31.crazyae2.client.render.ImprovedDriveModel;
import dev.beecube31.crazyae2.client.render.ThroughputMonitorRenderer;
import dev.beecube31.crazyae2.core.registries.server.CrazyTileEntities;
import dev.beecube31.crazyae2.common.parts.PartThroughputConversionMonitor;
import dev.beecube31.crazyae2.common.parts.PartThroughputStorageMonitor;
import appeng.client.api.renderer.parts.RegisterPartRendererEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

public final class ModelsFactory {
    private ModelsFactory() {}

    public static void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(ImprovedDriveModel.ID, ImprovedDriveModel.Unbaked.MAP_CODEC);
        event.registerModel(CrazyCraftingCubeModel.ID, CrazyCraftingCubeModel.Unbaked.MAP_CODEC);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType) CrazyTileEntities.IMPROVED_CRANK.get(),
                (BlockEntityRendererProvider) CrankRenderer::new);
    }

    public static void registerPartRenderers(RegisterPartRendererEvent event) {
        event.register(PartThroughputStorageMonitor.class, new ThroughputMonitorRenderer<>());
        event.register(PartThroughputConversionMonitor.class, new ThroughputMonitorRenderer<>());
    }
}
