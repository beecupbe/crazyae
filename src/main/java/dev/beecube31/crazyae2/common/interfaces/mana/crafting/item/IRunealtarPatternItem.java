package dev.beecube31.crazyae2.common.interfaces.mana.crafting.item;

import dev.beecube31.crazyae2.common.interfaces.mana.crafting.IRunealtarPatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRunealtarPatternItem {
    IRunealtarPatternDetails getPatternForItem(ItemStack is, World w);
}
