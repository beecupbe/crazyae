/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package dev.beecube31.crazyae2.common.network;

import dev.beecube31.crazyae2.common.network.packets.PacketConfigButton;
import dev.beecube31.crazyae2.common.network.packets.PacketGuiDataSync;
import dev.beecube31.crazyae2.common.network.packets.PacketInventoryAction;
import dev.beecube31.crazyae2.common.network.packets.PacketSwitchGuis;
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

        SWITCH_GUIS(PacketSwitchGuis.class, PacketSwitchGuis::new);

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
