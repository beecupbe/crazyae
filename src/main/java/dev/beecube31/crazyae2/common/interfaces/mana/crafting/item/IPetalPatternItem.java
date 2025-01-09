package dev.beecube31.crazyae2.common.interfaces.mana.crafting.item;

import dev.beecube31.crazyae2.common.interfaces.mana.crafting.IPetalPatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPetalPatternItem {
    IPetalPatternDetails getPatternForItem(ItemStack is, World w);
}
