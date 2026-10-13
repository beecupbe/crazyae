package dev.beecube31.crazyae2.common.network;

import appeng.core.AELog;
import appeng.core.AppEng;
import dev.beecube31.crazyae2.core.ModVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.function.Consumer;

public class CrazyNetworkHandler {
    private static CrazyNetworkHandler instance;

    private final ResourceLocation myChannelName;

    private final Consumer<CrazyPacket> clientHandler;

    public CrazyNetworkHandler(final ResourceLocation channelName) {
        EventNetworkChannel ec = NetworkRegistry.ChannelBuilder.named(myChannelName = channelName)
                .networkProtocolVersion(ModVersion::get).clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true)
                .eventNetworkChannel();
        ec.registerObject(this);

        this.clientHandler = DistExecutor.unsafeRunForDist(() -> () -> CrazyClientPacketHandler::onPacketData, () -> () -> pkt -> {});
    }

    public static void init(final ResourceLocation channelName) {
        instance = new CrazyNetworkHandler(channelName);
    }

    public static CrazyNetworkHandler instance() {
        return instance;
    }

    @SubscribeEvent
    public void serverPacket(final NetworkEvent.ClientCustomPayloadEvent ev) {
        try {
            NetworkEvent.Context ctx = ev.getSource().get();
            ctx.setPacketHandled(true);
            var packet = deserializePacket(ev.getPayload());
            var player = ctx.getSender();
            ctx.enqueueWork(
                    () -> {
                        try {
                            packet.serverPacketData(player);
                        } catch (final IllegalArgumentException e) {
                            AELog.warn(e);
                        }
                    });
        } catch (final RunningOnDifferentThreadException ignored) {}
    }

    @SubscribeEvent
    public void clientPacket(NetworkEvent.ServerCustomPayloadEvent ev) {
        if (ev instanceof NetworkEvent.ServerCustomPayloadLoginEvent) {
            return;
        }
        if (this.clientHandler != null) {
            try {
                NetworkEvent.Context ctx = ev.getSource().get();
                ctx.setPacketHandled(true);

                var packet = deserializePacket(ev.getPayload());

                ctx.enqueueWork(() -> this.clientHandler.accept(packet));
            } catch (RunningOnDifferentThreadException ignored) {}
        }
    }

    private static CrazyPacket deserializePacket(FriendlyByteBuf payload) {
        var packetId = payload.readInt();
        return CrazyPacketHandler.PacketTypes.getPacket(packetId).parsePacket(payload);
    }

    public ResourceLocation getChannel() {
        return this.myChannelName;
    }

    public void sendToAll(CrazyPacket message) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null) {
            server.getPlayerList().broadcastAll(message.toPacket(NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    public void sendTo(CrazyPacket message, ServerPlayer player) {
        player.connection.send(message.toPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    public void sendToAllAround(CrazyPacket message, CrazyTargetPoint point) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null) {
            Packet<?> pkt = message.toPacket(NetworkDirection.PLAY_TO_CLIENT);
            server.getPlayerList().broadcast(point.excluded(), point.x(), point.y(), point.z(), point.r2(),
                    point.level().dimension(), pkt);
        }
    }

    public void sendToServer(CrazyPacket message) {
        Minecraft.getInstance().getConnection().send(message.toPacket(NetworkDirection.PLAY_TO_SERVER));
    }
}
