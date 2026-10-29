package dev.beecube31.crazyae2.mixins.crafting;

import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.inv.ListCraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingCpuLogic.class, remap = false)
public interface AccessorCraftingCpuLogic {
    @Accessor("inventory")
    ListCraftingInventory crazyae$getInventory();
}
