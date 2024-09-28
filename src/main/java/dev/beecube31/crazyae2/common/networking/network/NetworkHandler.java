package dev.beecube31.crazyae2.common.networking.network;


import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


public class NetworkHandler {
    public static NetworkHandler instance;

    private final FMLEventChannel ec;
    private final String myChannelName;

    private final IPacketHandler clientHandler;
    private final IPacketHandler serveHandler;

    public NetworkHandler(final String channelName) {
        MinecraftForge.EVENT_BUS.register(this);
        this.ec = NetworkRegistry.INSTANCE.newEventDrivenChannel(this.myChannelName = channelName);
        this.ec.register(this);

        this.clientHandler = this.createClientSide();
        this.serveHandler = this.createServerSide();
    }

    public static void init(final String channelName) {
        instance = new NetworkHandler(channelName);
    }

    public static NetworkHandler instance() {
        return instance;
    }

    private IPacketHandler createClientSide() {
        try {
            return new CrazyAEClientPacketHandler();
        } catch (final Throwable t) {
            return null;
        }
    }

    private IPacketHandler createServerSide() {
        try {
            return new CrazyAEServerPacketHandler();
        } catch (final Throwable t) {
            return null;
        }
    }

    @SubscribeEvent
    public void serverPacket(final ServerCustomPacketEvent ev) {
        final NetHandlerPlayServer srv = (NetHandlerPlayServer) ev.getPacket().handler();
        if (this.serveHandler != null) {
            try {
                this.serveHandler.onPacketData(null, ev.getHandler(), ev.getPacket(), srv.player);
            } catch (final ThreadQuickExitException ignored) {

            }
        }
    }

    @SubscribeEvent
    public void clientPacket(final ClientCustomPacketEvent ev) {
        if (this.clientHandler != null) {
            try {
                this.clientHandler.onPacketData(null, ev.getHandler(), ev.getPacket(), null);
            } catch (final ThreadQuickExitException ignored) {

            }
        }
    }

    public String getChannel() {
        return this.myChannelName;
    }

    public void sendToAll(final CrazyAEPacket message) {
        this.ec.sendToAll(message.getProxy());
    }

    public void sendTo(final CrazyAEPacket message, final EntityPlayerMP player) {
        this.ec.sendTo(message.getProxy(), player);
    }

    public void sendToAllAround(final CrazyAEPacket message, final NetworkRegistry.TargetPoint point) {
        this.ec.sendToAllAround(message.getProxy(), point);
    }

    public void sendToDimension(final CrazyAEPacket message, final int dimensionId) {
        this.ec.sendToDimension(message.getProxy(), dimensionId);
    }

    public void sendToServer(final CrazyAEPacket message) {
        this.ec.sendToServer(message.getProxy());
    }
}
