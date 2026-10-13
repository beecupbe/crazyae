package dev.beecube31.crazyae2.core;

import net.minecraftforge.fml.ModList;

public final class ModsChecker {

    public static boolean JEI = false;
    public static boolean REI = false;

    public static void init() {
        var list = ModList.get();
        if (list.isLoaded("jei")) {
            JEI = true;
        }
        if (list.isLoaded("roughlyenoughitems")) {
            REI = true;
        }
    }
}
