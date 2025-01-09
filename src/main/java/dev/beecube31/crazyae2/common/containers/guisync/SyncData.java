package dev.beecube31.crazyae2.common.containers.guisync;

import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketProgressBar;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketValueConfig;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;

public class SyncData {

    private final CrazyAEBaseContainer source;
    private final Field field;
    private final int channel;
    private Object clientVersion;

    public SyncData(final CrazyAEBaseContainer container, final Field field, final GuiSync annotation) {
        this.clientVersion = null;
        this.source = container;
        this.field = field;
        this.channel = annotation.value();
    }

    public int getChannel() {
        return this.channel;
    }

    public void tick(final IContainerListener c) {
        try {
            final Object val = this.field.get(this.source);
            if (val != null && this.clientVersion == null) {
                this.send(c, val);
            } else if (!val.equals(this.clientVersion)) {
                this.send(c, val);
            }
        } catch (final IllegalArgumentException | IllegalAccessException | IOException e) {
            CrazyAE.logger().debug(e);
        }
    }

    private void send(final IContainerListener o, final Object val) throws IOException {
        if (val instanceof String) {
            if (o instanceof EntityPlayerMP) {
                NetworkHandler.instance().sendTo(new PacketValueConfig("SyncDat." + this.channel, (String) val), (EntityPlayerMP) o);
            }
        } else if (this.field.getType().isEnum()) {
            o.sendWindowProperty(this.source, this.channel, ((Enum<?>) val).ordinal());
        } else if (val instanceof Long) {
            if (o instanceof EntityPlayerMP) {
                NetworkHandler.instance().sendTo(new PacketProgressBar(this.channel, (Long) val), (EntityPlayerMP) o);
            }
        } else if (val instanceof Boolean) {
            o.sendWindowProperty(this.source, this.channel, ((Boolean) val) ? 1 : 0);
        } else {
            o.sendWindowProperty(this.source, this.channel, (Integer) val);
        }

        this.clientVersion = val;
    }

    public void update(final Object val) {
        try {
            final Object oldValue = this.field.get(this.source);
            if (val instanceof String) {
                this.updateString(oldValue, (String) val);
            } else {
                this.updateValue(oldValue, (Long) val);
            }
        } catch (final IllegalArgumentException | IllegalAccessException e) {
            CrazyAE.logger().debug(e);
        }
    }

    private void updateString(final Object oldValue, final String val) {
        try {
            this.field.set(this.source, val);
        } catch (final IllegalArgumentException | IllegalAccessException e) {
            CrazyAE.logger().debug(e);
        }
    }

    private void updateValue(final Object oldValue, final long val) {
        try {
            if (this.field.getType().isEnum()) {
                final EnumSet<? extends Enum> valList = EnumSet.allOf((Class<? extends Enum>) this.field.getType());
                for (final Enum e : valList) {
                    if (e.ordinal() == val) {
                        this.field.set(this.source, e);
                        break;
                    }
                }
            } else {
                if (this.field.getType().equals(int.class)) {
                    this.field.set(this.source, (int) val);
                } else if (this.field.getType().equals(long.class)) {
                    this.field.set(this.source, val);
                } else if (this.field.getType().equals(boolean.class)) {
                    this.field.set(this.source, val == 1);
                } else if (this.field.getType().equals(Integer.class)) {
                    this.field.set(this.source, (int) val);
                } else if (this.field.getType().equals(Long.class)) {
                    this.field.set(this.source, val);
                } else if (this.field.getType().equals(Boolean.class)) {
                    this.field.set(this.source, val == 1);
                }
            }

            this.source.onUpdate(this.field.getName(), oldValue, this.field.get(this.source));
        } catch (final IllegalArgumentException | IllegalAccessException e) {
            CrazyAE.logger().debug(e);
        }
    }
}
