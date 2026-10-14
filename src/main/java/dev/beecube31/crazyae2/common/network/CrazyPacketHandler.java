package dev.beecube31.crazyae2.common.network;

import dev.beecube31.crazyae2.common.network.packets.*;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CrazyPacketHandler {
    private static final Map<Class<? extends CrazyPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();

    public enum PacketTypes {
        CONFIG_BUTTON(PacketConfigButton.class, PacketConfigButton::new),

        GUI_CONTAINER_SYNC(PacketGuiDataSync.class, PacketGuiDataSync::new),
        INV_ACTION(PacketInventoryAction.class, PacketInventoryAction::new),

        SWITCH_GUIS(PacketSwitchGuis.class, PacketSwitchGuis::new),

        QUANTUM_CPU_FAST_PLACE(PacketQuantumCpuFastPlace.class, PacketQuantumCpuFastPlace::new);

        private final Function<FriendlyByteBuf, CrazyPacket> factory;

        PacketTypes(Class<? extends CrazyPacket> packetClass, Function<FriendlyByteBuf, CrazyPacket> factory) {
            this.factory = factory;

            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static PacketTypes getPacket(int id) {
            return values()[id];
        }

        public int getPacketId() {
            return ordinal();
        }

        static PacketTypes getID(Class<? extends CrazyPacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public CrazyPacket parsePacket(FriendlyByteBuf in) throws IllegalArgumentException {
            return this.factory.apply(in);
        }
    }
}
