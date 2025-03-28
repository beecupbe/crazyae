package dev.beecube31.crazyae2.common.util;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraftforge.fml.common.Loader;

public class ModsChecker {
    public static final boolean IC2_LOADED = Loader.isModLoaded("ic2");
    public static final boolean RFLUX_LOADED = Loader.isModLoaded("redstoneflux");
    public static final boolean IU_LOADED;
    public static final boolean COFHCORE_LOADED = Loader.isModLoaded("cofhcore");
    public static final boolean TD_LOADED = Loader.isModLoaded("thermaldynamics");

    static {
        boolean loaded = false;
        try {
            if (Loader.isModLoaded("industrialupgrade")) {
                loaded = ((String) Class.forName("com.denfop.Constants").getField("MOD_VERSION").get(null)).startsWith("3");
            }
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException ignored) {}

        CrazyAE.logger().info("IU 3.X LOADED ? : {}", loaded);
        IU_LOADED = loaded;
    }
}
