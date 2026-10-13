package dev.beecube31.crazyae2.common.network;

import appeng.core.AEConfig;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

public abstract class CrazyPacket {

    private FriendlyByteBuf p;

    public void serverPacketData(ServerPlayer player) {
        throw new UnsupportedOperationException(
                "This packet ( " + this.getPacketID() + " does not implement a server side handler.");
    }

    public final int getPacketID() {
        return CrazyPacketHandler.PacketTypes.getID(this.getClass()).ordinal();
    }

    public void clientPacketData(Player player) {
        throw new UnsupportedOperationException(
                "This packet ( " + this.getPacketID() + " does not implement a client side handler.");
    }

    protected final void configureWrite(FriendlyByteBuf data) {
        data.capacity(data.readableBytes());
        this.p = data;
    }

    public Packet<?> toPacket(NetworkDirection direction) {
        if (this.p.array().length > 2 * 1024 * 1024) // 2k walking room :)
        {
            throw new IllegalArgumentException(
                    "Sorry CrazyAE made a " + this.p.array().length + " byte packet by accident!");
        }

        if (AEConfig.instance().isPacketLogEnabled()) {
            CrazyAE.LOGGER.info("{} : {}", this.getClass().getName(), p.readableBytes());
        }

        return direction.buildPacket(Pair.of(p, 0), CrazyNetworkHandler.instance().getChannel()).getThis();
    }
}
