package dev.beecube31.crazyae2.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
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

    static RegistryAccess clientRegistryAccess() {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            return connection.registryAccess();
        }
        var level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }
}
