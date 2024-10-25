package dev.beecube31.crazyae2.common.networking;

import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public abstract class CrazyAEPacket implements Packet {
    private PacketBuffer p;
    private CrazyAEPacketCallState caller;

    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        throw new UnsupportedOperationException("Packet ( " + this.getPacketID() + " does not implement a server side handler.");
    }

    public final int getPacketID() {
        return CrazyAEPacketHandler.PacketTypes.getID(this.getClass()).ordinal();
    }

    public void clientPacketData(final INetworkInfo network, final CrazyAEPacket packet, final EntityPlayer player) {
        throw new UnsupportedOperationException("Packet ( " + this.getPacketID() + " does not implement a client side handler.");
    }

    protected void configureWrite(final ByteBuf data) {
        data.capacity(data.readableBytes());
        this.p = new PacketBuffer(data);
    }

    public FMLProxyPacket getProxy() {
        if (this.p.array().length > 2 * 1024 * 1024) {
            throw new IllegalArgumentException();
        }
        return new FMLProxyPacket(this.p, NetworkHandler.instance().getChannel());
    }

    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        throw new RuntimeException("Not Implemented");
    }

    public ByteArrayInputStream getPacketByteArray(ByteBuf stream, int readerIndex, int readableBytes) {
        final ByteArrayInputStream bytes;
        if (stream.hasArray()) {
            bytes = new ByteArrayInputStream(stream.array(), readerIndex, readableBytes);
        } else {
            byte[] data = new byte[stream.capacity()];
            stream.getBytes(readerIndex, data, 0, readableBytes);
            bytes = new ByteArrayInputStream(data);
        }
        return bytes;
    }

    public ByteArrayInputStream getPacketByteArray(ByteBuf stream) {
        return this.getPacketByteArray(stream, 0, stream.readableBytes());
    }

    public void setCallParam(final CrazyAEPacketCallState call) {
        this.caller = call;
    }

    @Override
    public void processPacket(final INetHandler handler) {
        this.caller.call(this);
    }

}
