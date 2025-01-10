package dev.beecube31.crazyae2.core.helpers;

import net.minecraftforge.common.MinecraftForge;

public class ServerHelper implements Helper {

    public void preinit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init() {

    }

    public void postinit() {

    }
}
