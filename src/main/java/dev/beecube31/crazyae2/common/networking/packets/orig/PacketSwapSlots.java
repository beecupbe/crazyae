package dev.beecube31.crazyae2.common.networking.packets.orig;

import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSwapSlots extends CrazyAEPacket {

    private final int slotA;
    private final int slotB;

    // automatic.
    public PacketSwapSlots(final ByteBuf stream) {
        this.slotA = stream.readInt();
        this.slotB = stream.readInt();
    }

    // api
    public PacketSwapSlots(final int slotA, final int slotB) {
        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeInt(this.slotA = slotA);
        data.writeInt(this.slotB = slotB);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        if (player != null && player.openContainer instanceof CrazyAEBaseContainer) {
            ((CrazyAEBaseContainer) player.openContainer).swapSlotContents(this.slotA, this.slotB);
        }
    }
}
