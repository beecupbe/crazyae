package dev.beecube31.crazyae2.core;

import net.neoforged.fml.ModList;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
