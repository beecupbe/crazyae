package dev.beecube31.crazyae2.common.compat;

import com.aeternal.botaniverse.common.item.materials.ItemMoreRune;
import net.minecraft.item.Item;

public class BotaniverseCompat {
    public static boolean isItemMoreRune(Item item) {
        return item instanceof ItemMoreRune;
    }
}
