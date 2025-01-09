package dev.beecube31.crazyae2.common.util;

import java.lang.reflect.Field;

public class AEUtils {
    public static final Field fValue;

    static {
        try {
            fValue = Class.forName("appeng.me.cluster.implementations.CraftingCPUCluster$TaskProgress")
                    .getDeclaredField("value");
            fValue.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
