package dev.beecube31.crazyae2.common.network;

import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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

    protected static RegistryFriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), CrazyNetworkHandler.registryAccess());
    }

    protected final void configureWrite(FriendlyByteBuf data) {
        data.capacity(data.readableBytes());
        this.p = data;
    }

    public CrazyPayload toPayload() {
        if (this.p.array().length > 2 * 1024 * 1024) // 2k walking room :)
        {
            throw new IllegalArgumentException(
                    "Sorry CrazyAE made a " + this.p.array().length + " byte packet by accident!");
        }

        byte[] bytes = new byte[p.readableBytes()];
        p.getBytes(p.readerIndex(), bytes);
        return new CrazyPayload(bytes);
    }
}
