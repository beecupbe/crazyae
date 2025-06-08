package dev.beecube31.crazyae2.mixins.aefixes.crafting;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.cache.CraftingGridCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinGridCraftingCache implements ICraftingGrid, ICraftingProviderHelper, ICellProvider, IMEInventoryHandler<IAEItemStack> {
    @Shadow protected abstract void updateCPUClusters();

    @Shadow protected abstract void recalculateCraftingPatterns();

    @Shadow private boolean updateList;
    @Shadow private boolean updatePatterns;
    @Unique private boolean crazyae$updateList;
    @Unique private boolean crazyae$updatePatterns;

    @Unique private int crazyae$ticker = 0;

    @Inject(
            method = "onUpdateTick",
            at = @At("TAIL"),
            remap = false
    )
    private void crazyae$recreateCraftingLists(CallbackInfo ci) {
        if (this.crazyae$ticker > 0 && this.crazyae$ticker % 10 == 0) {
            if (this.crazyae$updateList) {
                this.crazyae$updateList = false;
                this.updateCPUClusters();
            }

            if (crazyae$updatePatterns) {
                this.recalculateCraftingPatterns();
                this.crazyae$updatePatterns = false;
            }
        }

        this.crazyae$ticker++;
    }

    @Inject(
            method = "addNode",
            at = @At("TAIL"),
            remap = false
    )
    private void crazyae$removeAEListsCheckOnAddNode(IGridNode gridNode, IGridHost machine, CallbackInfo ci) {
        if (this.updateList) {
            this.crazyae$updateList = true;
            this.updateList = false;
        }

        if (this.updatePatterns) {
            this.crazyae$updatePatterns = true;
            this.updatePatterns = false;
        }
    }

    @Inject(
            method = "removeNode",
            at = @At("TAIL"),
            remap = false
    )
    private void crazyae$removeAEListsCheckOnRemoveNode(IGridNode gridNode, IGridHost machine, CallbackInfo ci) {
        if (this.updateList) {
            this.crazyae$updateList = true;
            this.updateList = false;
        }

        if (this.updatePatterns) {
            this.crazyae$updatePatterns = true;
            this.updatePatterns = false;
        }
    }


    /**
     * @author beecube31
     * @reason Optimization
     * @since v0.6
     */
    @Overwrite
    @MENetworkEventSubscribe
    public void updateCPUClusters(final MENetworkCraftingCpuChange c) {
        this.crazyae$updateList = true;
    }

    /**
     * @author beecube31
     * @reason Optimization
     * @since v0.6
     */
    @Overwrite
    @MENetworkEventSubscribe
    public void updateCPUClusters(final MENetworkCraftingPatternChange c) {
        this.crazyae$updatePatterns = true;
    }
}
