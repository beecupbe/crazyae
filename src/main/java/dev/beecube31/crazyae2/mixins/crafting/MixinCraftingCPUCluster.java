package dev.beecube31.crazyae2.mixins.crafting;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster {

    @Unique
    private static final int CRAZYAE_MAX_THREADS = 1 << 30;

    @Shadow
    private int accelerator;

    @ModifyConstant(method = "addBlockEntity", constant = @Constant(intValue = 16))
    private int crazyae$unlimitedAcceleratorThreads(int original) {
        return Integer.MAX_VALUE;
    }

    @Redirect(method = "addBlockEntity",
            at = @At(value = "INVOKE", target = "Lappeng/blockentity/crafting/CraftingBlockEntity;getAcceleratorThreads()I"))
    private int crazyae$saturatingThreads(CraftingBlockEntity te) {
        long threads = Math.max(0, te.getAcceleratorThreads());
        return (int) Math.max(0, Math.min(threads, (long) CRAZYAE_MAX_THREADS - Math.max(0, this.accelerator)));
    }
}
