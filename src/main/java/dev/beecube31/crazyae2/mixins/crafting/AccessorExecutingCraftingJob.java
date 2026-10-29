package dev.beecube31.crazyae2.mixins.crafting;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.execution.ElapsedTimeTracker;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ExecutingCraftingJob.class, remap = false)
public interface AccessorExecutingCraftingJob {
    @Accessor("tasks")
    Map<IPatternDetails, Object> crazyae$getTasks();

    @Accessor("waitingFor")
    ListCraftingInventory crazyae$getWaitingFor();

    @Accessor("timeTracker")
    ElapsedTimeTracker crazyae$getTimeTracker();
}
