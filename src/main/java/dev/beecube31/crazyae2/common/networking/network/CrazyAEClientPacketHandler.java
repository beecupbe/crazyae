package dev.beecube31.crazyae2.common.networking.network;

import appeng.core.AELog;
import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacketCallState;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketThreadUtil;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.InvocationTargetException;

public class CrazyAEClientPacketHandler extends CrazyAEPacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(final INetworkInfo manager, final INetHandler handler, final FMLProxyPacket packet, final EntityPlayer player) {
        final ByteBuf stream = packet.payload();

        try {
            final int packetType = stream.readInt();
            final CrazyAEPacket pack = PacketTypes.getPacket(packetType).parsePacket(stream);

            final CrazyAEPacketCallState callState = new CrazyAEPacketCallState() {

                @Override
                public void call(final CrazyAEPacket packet) {
                    packet.clientPacketData(manager, packet, Minecraft.getMinecraft().player);
                }
            };

            pack.setCallParam(callState);
            PacketThreadUtil.checkThreadAndEnqueue(pack, handler, Minecraft.getMinecraft());
            callState.call(pack);
        } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            AELog.debug(e);
        }
    }
}
