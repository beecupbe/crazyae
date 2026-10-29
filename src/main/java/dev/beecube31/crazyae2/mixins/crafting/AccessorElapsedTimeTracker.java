package dev.beecube31.crazyae2.mixins.crafting;

import appeng.api.stacks.AEKeyType;
import appeng.crafting.execution.ElapsedTimeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ElapsedTimeTracker.class, remap = false)
public interface AccessorElapsedTimeTracker {
    @Invoker("addMaxItems")
    void crazyae$addMaxItems(long itemDiff, AEKeyType keyType);
}
