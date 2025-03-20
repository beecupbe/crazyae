package dev.beecube31.crazyae2.common.util;

import net.minecraftforge.fml.common.Loader;

public class ModsChecker {
    public static final boolean IC2_LOADED = Loader.isModLoaded("ic2");
    public static final boolean RFLUX_LOADED = Loader.isModLoaded("redstoneflux");
    public static final boolean IU_LOADED = Loader.isModLoaded("industrialupgrade");
    public static final boolean COFHCORE_LOADED = Loader.isModLoaded("cofhcore");
    public static final boolean TD_LOADED = Loader.isModLoaded("thermaldynamics");

}
