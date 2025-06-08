package dev.beecube31.crazyae2.mixins.cucombiner;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.crafting.CraftingLink;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;
import dev.beecube31.crazyae2.common.tile.crafting.TileDenseCraftingUnit;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.stream.StreamSupport;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinCraftingGridCache {

    @Shadow @Final private IGrid grid;

    @Shadow @Final private Set<CraftingCPUCluster> craftingCPUClusters;

    @Shadow public abstract void addLink(CraftingLink link);

    @Inject(method = "updateCPUClusters()V", at = @At("RETURN"), remap = false)
    private void crazyae$updateCPUClusters(CallbackInfo ci) {
        for (Object cls: StreamSupport.stream(grid.getMachinesClasses().spliterator(), false).filter(te -> te.isAssignableFrom(TileDenseCraftingUnit.class) || te.isAssignableFrom(TileCraftingUnitsCombiner.class)).toArray()) {
            for (final IGridNode cst : this.grid.getMachines((Class<? extends IGridHost>) cls)) {
                final TileCraftingTile tile = (TileCraftingTile) cst.getMachine();
                final CraftingCPUCluster cluster = (CraftingCPUCluster) tile.getCluster();
                if (cluster != null) {
                    this.craftingCPUClusters.add(cluster);

                    if (cluster.getLastCraftingLink() != null) {
                        this.addLink((CraftingLink) cluster.getLastCraftingLink());
                    }
                }
            }
        }
    }
}
