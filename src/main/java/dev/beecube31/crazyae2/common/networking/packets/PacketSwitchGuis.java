package dev.beecube31.crazyae2.common.networking.packets;

import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.INetworkInfo;
import dev.beecube31.crazyae2.common.containers.base.ContainerOpenContext;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;


public class PacketSwitchGuis extends CrazyAEPacket {
    private final Object newGui;

    public PacketSwitchGuis(final ByteBuf stream) {
        this.newGui = CrazyAEGuiBridge.values()[stream.readInt()];
    }

    public PacketSwitchGuis(final Object newGui) {
        this.newGui = newGui;
        final ByteBuf data = Unpooled.buffer();
        data.writeInt(this.getPacketID());
        if (newGui instanceof GuiBridge aeGui) {
            data.writeInt(aeGui.ordinal());
        } else if (newGui instanceof CrazyAEGuiBridge crazyAeGui) {
            data.writeInt(crazyAeGui.ordinal());
        } else {
            throw new IllegalArgumentException("Gui must be located in GuiBridge or CrazyAEGuiBridge : " + newGui);
        }

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final Container c = player.openContainer;
        if (c instanceof final CrazyAEBaseContainer bc) {
            final ContainerOpenContext context = bc.getOpenContext();
            if (context != null) {
                final TileEntity te = context.getTile();
                CrazyAEGuiHandler.openGUI(player, te, bc.getOpenContext().getSide(), this.newGui);
            }
        }
    }
}
