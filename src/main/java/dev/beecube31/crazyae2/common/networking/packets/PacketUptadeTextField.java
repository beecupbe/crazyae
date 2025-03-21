package dev.beecube31.crazyae2.common.networking.packets;

import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.containers.ContainerColorizerGui;
import dev.beecube31.crazyae2.common.containers.ContainerColorizerText;
import dev.beecube31.crazyae2.common.containers.ContainerEnergyBusSettings;
import dev.beecube31.crazyae2.common.containers.ContainerPriority;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketUptadeTextField extends CrazyAEPacket {
    private final String Name;
    private final String Value;

    public PacketUptadeTextField(final ByteBuf stream) throws IOException {
        final DataInputStream dis = new DataInputStream(this.getPacketByteArray(stream, stream.readerIndex(), stream.readableBytes()));
        this.Name = dis.readUTF();
        this.Value = dis.readUTF();
    }

    public PacketUptadeTextField(final String name, final String value) throws IOException {
        this.Name = name;
        this.Value = value;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(name);
        dos.writeUTF(value);

        data.writeBytes(bos.toByteArray());

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final Container c = player.openContainer;

        if (this.Name.equals("PriorityHost.Priority") && c instanceof final ContainerPriority pc) {
            pc.setPriority(Integer.parseInt(this.Value), player);
            return;
        }

        if (this.Name.equals("EnergyHost.Config") && c instanceof final ContainerEnergyBusSettings s) {
            s.setSettings(Long.parseLong(this.Value), player);
            return;
        }

        if (this.Name.equals("Colorizer.Gui") && c instanceof final ContainerColorizerGui pc) {
            pc.setText(this.Value);
            return;
        }

        if (this.Name.equals("Colorizer.Text") && c instanceof final ContainerColorizerText pc) {
            pc.setText(this.Value);
            return;
        }
    }

    @Override
    public void clientPacketData(final INetworkInfo network, final CrazyAEPacket packet, final EntityPlayer player) {}
}
