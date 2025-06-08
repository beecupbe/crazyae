package dev.beecube31.crazyae2.mixins.core.crafting.container;

import appeng.api.networking.IGrid;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerCraftingCPU.class, remap = false)
public interface AccessorContainerCraftingCPU {
    @Accessor IGrid getNetwork();

    @Accessor CraftingCPUCluster getMonitor();
}
