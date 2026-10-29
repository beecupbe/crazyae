package dev.beecube31.crazyae2.common.interfaces;

import appeng.api.inventories.InternalInventory;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IValidationSupplier<T> {
    T isValid(InternalInventory inv, int slot, ItemStack stack);
}
