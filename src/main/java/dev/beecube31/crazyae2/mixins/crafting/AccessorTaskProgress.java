package dev.beecube31.crazyae2.mixins.crafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "appeng.crafting.execution.ExecutingCraftingJob$TaskProgress", remap = false)
public interface AccessorTaskProgress {
    @Accessor("value")
    long crazyae$getValue();

    @Accessor("value")
    void crazyae$setValue(long value);
}
