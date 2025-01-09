package dev.beecube31.crazyae2.common.networking.packets.orig;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.sync.network.INetworkInfo;
import appeng.helpers.Reflected;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PacketConfigButton extends CrazyAEPacket {
    private final Settings option;
    private final boolean rotationDirection;

    // automatic.
    @Reflected
    public PacketConfigButton(final ByteBuf stream) {
        this.option = Settings.values()[stream.readInt()];
        this.rotationDirection = stream.readBoolean();
    }

    // api
    public PacketConfigButton(final Settings option, final boolean rotationDirection) {
        this.option = option;
        this.rotationDirection = rotationDirection;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeInt(option.ordinal());
        data.writeBoolean(rotationDirection);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final EntityPlayerMP sender = (EntityPlayerMP) player;
        if (sender.openContainer instanceof final CrazyAEBaseContainer baseContainer) {
            if (baseContainer.getTarget() instanceof IConfigurableObject) {
                final IConfigManager cm = ((IConfigurableObject) baseContainer.getTarget()).getConfigManager();
                final Enum<?> newState = Platform.rotateEnum(cm.getSetting(this.option), this.rotationDirection, this.option.getPossibleValues());
                cm.putSetting(this.option, newState);
            }
        }
    }
}
