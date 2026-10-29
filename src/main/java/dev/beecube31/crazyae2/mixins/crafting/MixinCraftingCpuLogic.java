package dev.beecube31.crazyae2.mixins.crafting;

import appeng.api.networking.energy.IEnergyService;
import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.service.CraftingService;
import dev.beecube31.crazyae2.common.crafting.QuantumBatchExecutor;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingCpuLogic.class, remap = false)
public abstract class MixinCraftingCpuLogic {
    @Shadow
    @Final
    CraftingCPUCluster cluster;

    @Shadow
    private ExecutingCraftingJob job;

    @Shadow
    @Final
    private ListCraftingInventory inventory;

    @Shadow
    public abstract boolean isJobSuspended();

    @Inject(method = "executeCrafting", at = @At("HEAD"), cancellable = true)
    private void crazyae$batchExecute(int maxPatterns, CraftingService craftingService, IEnergyService energyService,
                                      Level level, CallbackInfoReturnable<Integer> cir) {
        if (this.job == null || this.isJobSuspended() || maxPatterns <= 0) {
            cir.setReturnValue(0);
            return;
        }

        int batched = QuantumBatchExecutor.execute(this.cluster, this.job, this.inventory, maxPatterns,
                craftingService, energyService, level);

        if (batched > 0) {
            cir.setReturnValue(batched);
        }
    }
}
