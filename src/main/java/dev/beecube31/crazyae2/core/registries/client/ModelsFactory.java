package dev.beecube31.crazyae2.core.registries.client;

import appeng.block.crafting.CraftingUnitBlock;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.tesr.CrankRenderer;
import appeng.hooks.BuiltInModelHooks;
import dev.beecube31.crazyae2.client.render.CrazyCraftingBlocksModelProvider;
import dev.beecube31.crazyae2.client.render.ImprovedDriveModel;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyTileEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ModelsFactory {
    private ModelsFactory() {}

    public static void registerBuiltInModels() {
        BuiltInModelHooks.addBuiltInModel(ImprovedDriveModel.ID, new ImprovedDriveModel());
        for (CrazyAECraftingUnitType type : CrazyAECraftingUnitType.values()) {
            if (type == CrazyAECraftingUnitType.COMBINER) {
                continue;
            }
            BuiltInModelHooks.addBuiltInModel(
                    CrazyAE.makeId("block/crafting/" + type.getName() + "_formed"),
                    new CraftingCubeModel(new CrazyCraftingBlocksModelProvider(type)));
        }
    }

    @SuppressWarnings("deprecation")
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var entry : CrazyBlocks.craftingUnits().entrySet()) {
                if (entry.getKey() != CrazyAECraftingUnitType.COMBINER) {
                    CraftingUnitBlock block = entry.getValue().get();
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
                }
            }
            ItemBlockRenderTypes.setRenderLayer(CrazyBlocks.IMPROVED_MAC.get(), RenderType.cutout());
            BlockEntityRenderers.register(CrazyTileEntities.IMPROVED_CRANK.get(), CrankRenderer::new);
        });
    }
}
