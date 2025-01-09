package dev.beecube31.crazyae2.common.networking.packets.orig;

import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class PacketProgressBar extends CrazyAEPacket {

    private final short id;
    private final long value;

    public PacketProgressBar(final ByteBuf stream) {
        this.id = stream.readShort();
        this.value = stream.readLong();
    }

    public PacketProgressBar(final int shortID, final long value) {
        this.id = (short) shortID;
        this.value = value;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeShort(shortID);
        data.writeLong(value);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final Container c = player.openContainer;
        if (c instanceof CrazyAEBaseContainer) {
            ((CrazyAEBaseContainer) c).updateFullProgressBar(this.id, this.value);
        }
    }

    @Override
    public void clientPacketData(final INetworkInfo network, final CrazyAEPacket packet, final EntityPlayer player) {
        final Container c = player.openContainer;
        if (c instanceof CrazyAEBaseContainer) {
            ((CrazyAEBaseContainer) c).updateFullProgressBar(this.id, this.value);
        }
    }
}
