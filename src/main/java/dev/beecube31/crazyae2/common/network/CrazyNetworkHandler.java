package dev.beecube31.crazyae2.common.network;

import appeng.core.AELog;
import appeng.core.AppEng;
import dev.beecube31.crazyae2.core.ModVersion;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class CrazyNetworkHandler {
    private static final CrazyNetworkHandler INSTANCE = new CrazyNetworkHandler();

    public static CrazyNetworkHandler instance() {
        return INSTANCE;
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(ModVersion.get()).optional();
        registrar.playBidirectional(CrazyPayload.TYPE, CrazyPayload.STREAM_CODEC, CrazyNetworkHandler::handle);
    }

    private static void handle(CrazyPayload payload, IPayloadContext ctx) {
        var player = ctx.player();
        var buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
        final CrazyPacket packet;
        try {
            packet = deserializePacket(buf);
        } catch (IllegalArgumentException e) {
            AELog.warn(e);
            return;
        }

        try {
            if (player instanceof ServerPlayer serverPlayer) {
                packet.serverPacketData(serverPlayer);
            } else {
                CrazyClientPacketHandler.onPacketData(packet);
            }
        } catch (final IllegalArgumentException e) {
            AELog.warn(e);
        }
    }

    private static CrazyPacket deserializePacket(RegistryFriendlyByteBuf payload) {
        var packetId = payload.readInt();
        return CrazyPacketHandler.PacketTypes.getPacket(packetId).parsePacket(payload);
    }


    public static RegistryAccess registryAccess() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.registryAccess();
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return CrazyClientPacketHandler.clientRegistryAccess();
        }
        return RegistryAccess.EMPTY;
    }

    public void sendToAll(CrazyPacket message) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null) {
            PacketDistributor.sendToAllPlayers(message.toPayload());
        }
    }

    public void sendTo(CrazyPacket message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message.toPayload());
    }

    public void sendToAllAround(CrazyPacket message, CrazyTargetPoint point) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null && point.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, point.excluded, point.x, point.y, point.z,
                    Math.sqrt(point.r2), message.toPayload());
        }
    }

    public void sendToServer(CrazyPacket message) {
        PacketDistributor.sendToServer(message.toPayload());
    }
}
