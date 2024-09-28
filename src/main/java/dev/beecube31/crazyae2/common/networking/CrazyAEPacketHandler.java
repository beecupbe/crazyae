package dev.beecube31.crazyae2.common.networking;


import appeng.core.AELog;
import dev.beecube31.crazyae2.common.networking.packets.PacketChangePriority;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class CrazyAEPacketHandler {
    private static final Map<Class<? extends CrazyAEPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();

    public enum PacketTypes {
        PACKET_CHANGE_PRIORITY(PacketChangePriority.class),
        PACKET_SWITCH_GUIS(PacketSwitchGuis.class);

        private final Class<? extends CrazyAEPacket> packetClass;
        private final Constructor<? extends CrazyAEPacket> packetConstructor;

        PacketTypes(final Class<? extends CrazyAEPacket> c) {
            this.packetClass = c;

            Constructor<? extends CrazyAEPacket> x = null;
            try {
                x = this.packetClass.getConstructor(ByteBuf.class);
            } catch (final NoSuchMethodException | SecurityException ignored) {}

            this.packetConstructor = x;
            REVERSE_LOOKUP.put(this.packetClass, this);

            if (this.packetConstructor == null) {
                throw new IllegalStateException("Invalid Packet Class " + c + ", must be constructable on DataInputStream");
            }
        }

        public static PacketTypes getPacket(final int id) {
            return (values())[id];
        }

        static PacketTypes getID(final Class<? extends CrazyAEPacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public CrazyAEPacket parsePacket(final ByteBuf in) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return this.packetConstructor.newInstance(in);
        }
    }
}
