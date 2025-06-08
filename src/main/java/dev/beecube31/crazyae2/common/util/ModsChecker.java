package dev.beecube31.crazyae2.common.util;

import dev.beecube31.crazyae2.common.compat.BotaniverseCompat;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

public class ModsChecker {
    public static final boolean IC2_LOADED = Loader.isModLoaded("ic2");
    public static final boolean RFLUX_LOADED = Loader.isModLoaded("redstoneflux");
    public static final boolean IU_LOADED;
    public static final boolean COFHCORE_LOADED = Loader.isModLoaded("cofhcore");
    public static final boolean AE2FC_LOADED = Loader.isModLoaded("ae2fc");
    public static final boolean TD_LOADED = Loader.isModLoaded("thermaldynamics");
    public static final boolean AA_LOADED = Loader.isModLoaded("actuallyadditions");
    public static final boolean BOTANIVERSE_LOADED = Loader.isModLoaded("botaniverse");

    public static boolean isItemMoreRune(Item item) {
        return BOTANIVERSE_LOADED && BotaniverseCompat.isItemMoreRune(item);
    }


    static {
        boolean loaded = false;
        try {
            if (Loader.isModLoaded("industrialupgrade")) {
                loaded = ((String) Class.forName("com.denfop.Constants").getField("MOD_VERSION").get(null)).startsWith("3");
            }
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException ignored) {}

        IU_LOADED = loaded;
    }
}
