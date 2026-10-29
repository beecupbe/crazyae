package dev.beecube31.crazyae2.common.network.packets;

import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.network.CrazyPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Consumer;

public class PacketGuiDataSync extends CrazyPacket {
    private final int containerId;

    private final RegistryFriendlyByteBuf data;

    public PacketGuiDataSync(int containerId, Consumer<RegistryFriendlyByteBuf> writer) {
        this.containerId = 0;
        this.data = null;

        RegistryFriendlyByteBuf data = newBuffer();
        data.writeInt(getPacketID());
        data.writeVarInt(containerId);
        writer.accept(data);
        configureWrite(data);
    }

    public PacketGuiDataSync(FriendlyByteBuf data) {
        this.containerId = data.readVarInt();
        this.data = new RegistryFriendlyByteBuf(data.copy(), ((RegistryFriendlyByteBuf) data).registryAccess());
    }

    public RegistryFriendlyByteBuf getData() {
        return data;
    }

    @Override
    public void clientPacketData(Player player) {
        AbstractContainerMenu c = player.containerMenu;
        if (c instanceof CrazyAEBaseContainer baseMenu && c.containerId == this.containerId) {
            baseMenu.receiveServerSyncData(this);
        }
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        AbstractContainerMenu c = player.containerMenu;
        if (c instanceof CrazyAEBaseContainer baseMenu && c.containerId == this.containerId) {
            baseMenu.receiveClientAction(this);
        }
    }

}
