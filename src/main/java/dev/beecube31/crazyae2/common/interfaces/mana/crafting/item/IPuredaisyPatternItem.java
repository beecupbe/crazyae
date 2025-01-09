package dev.beecube31.crazyae2.common.interfaces.mana.crafting.item;

import dev.beecube31.crazyae2.common.interfaces.mana.crafting.IPuredaisyPatternDetails;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPuredaisyPatternItem {
    IPuredaisyPatternDetails getPatternForItem(ItemStack is, World w);
}
