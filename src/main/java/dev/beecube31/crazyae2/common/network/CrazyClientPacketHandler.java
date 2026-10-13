package dev.beecube31.crazyae2.common.network;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrazyClientPacketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CrazyClientPacketHandler.class);

    private CrazyClientPacketHandler() {}

    public static void onPacketData(CrazyPacket packet) {
        try {
            packet.clientPacketData(Minecraft.getInstance().player);
        } catch (final IllegalArgumentException e) {
            LOG.error("Failed handling packet", e);
        }
    }
}
