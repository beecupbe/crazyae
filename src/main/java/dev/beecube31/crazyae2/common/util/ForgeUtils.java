package dev.beecube31.crazyae2.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ForgeUtils {
    public static boolean isOreDict(ItemStack stack, String entry) {
        if (stack.isEmpty()) return false;

        for (ItemStack is : OreDictionary.getOres(entry, false)) {
            if (OreDictionary.itemMatches(is, stack, false)) {
                return true;
            }
        }

        return false;
    }
}
