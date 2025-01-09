package dev.beecube31.crazyae2.common.interfaces.mana.crafting.item;

import dev.beecube31.crazyae2.common.interfaces.mana.crafting.IManapoolPatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IManapoolPatternItem {
    IManapoolPatternDetails getPatternForItem(ItemStack is, World w);
}
