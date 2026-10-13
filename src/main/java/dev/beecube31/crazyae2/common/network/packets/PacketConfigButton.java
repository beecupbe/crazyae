package dev.beecube31.crazyae2.common.network.packets;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.util.EnumCycler;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.network.CrazyPacket;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public final class PacketConfigButton extends CrazyPacket {
    private final Setting<?> option;
    private final boolean rotationDirection;

    public PacketConfigButton(FriendlyByteBuf stream) {
        this.option = Settings.getOrThrow(stream.readUtf());
        this.rotationDirection = stream.readBoolean();
    }

    // api
    public PacketConfigButton(Setting<?> option, boolean rotationDirection) {
        this.option = option;
        this.rotationDirection = rotationDirection;

        final FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());

        data.writeInt(this.getPacketID());
        data.writeUtf(option.getName());
        data.writeBoolean(rotationDirection);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (player.containerMenu instanceof CrazyAEBaseContainer baseMenu) {
            if (baseMenu.getTarget() instanceof IConfigurableObject configurableObject) {
                var cm = configurableObject.getConfigManager();
                if (cm.hasSetting(option)) {
                    cycleSetting(cm, option);
                } else {
                    CrazyAE.LOGGER.info("Ignoring unsupported setting {} sent by client on {}", option,
                            baseMenu.getTarget());
                }
            }
        }
    }

    private <T extends Enum<T>> void cycleSetting(IConfigManager cm, Setting<T> setting) {
        var currentValue = cm.getSetting(setting);
        var nextValue = EnumCycler.rotateEnum(currentValue, rotationDirection, setting.getValues());
        cm.putSetting(setting, nextValue);
    }

}
