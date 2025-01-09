package dev.beecube31.crazyae2.common.networking.packets.orig;

import appeng.core.sync.network.INetworkInfo;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;


public class PacketTargetItemStack extends CrazyAEPacket {
    private AEItemStack stack;

    // automatic.
    public PacketTargetItemStack(final ByteBuf stream) {
        try {
            if (stream.readableBytes() > 0) {
                this.stack = AEItemStack.fromPacket(stream);
            } else {
                this.stack = null;
            }
        } catch (Exception ex) {
            CrazyAE.logger().debug(ex);
            this.stack = null;
        }
    }

    // api
    public PacketTargetItemStack(AEItemStack stack) {

        this.stack = stack;

        final ByteBuf data = Unpooled.buffer();
        data.writeInt(this.getPacketID());
        if (stack != null) {
            try {
                stack.writeToPacket(data);
            } catch (Exception ex) {
                CrazyAE.logger().debug(ex);
            }
        }
        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        if (player.openContainer instanceof CrazyAEBaseContainer) {
            ((CrazyAEBaseContainer) player.openContainer).setTargetStack(this.stack);
        }
    }

}
