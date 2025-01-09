package dev.beecube31.crazyae2.common.interfaces.mana.crafting.item;

import dev.beecube31.crazyae2.common.interfaces.mana.crafting.IElventradePatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IElventradePatternItem {
    IElventradePatternDetails getPatternForItem(ItemStack is, World w);
}
