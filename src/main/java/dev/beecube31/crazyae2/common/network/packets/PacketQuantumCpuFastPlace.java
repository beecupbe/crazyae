package dev.beecube31.crazyae2.common.network.packets;

import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.beecube31.crazyae2.common.network.CrazyPacket;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class PacketQuantumCpuFastPlace extends CrazyPacket {

    public PacketQuantumCpuFastPlace(FriendlyByteBuf stream) {}

    public PacketQuantumCpuFastPlace() {
        final FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        data.writeInt(this.getPacketID());
        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (!(player.containerMenu instanceof PatternEncodingTermMenu menu)) {
            return;
        }

        var slots = menu.getSlots(SlotSemantics.ENCODED_PATTERN);
        if (slots.isEmpty()) {
            return;
        }

        var slot = slots.get(0);
        ItemStack pattern = slot.getItem();
        if (pattern.isEmpty()) {
            return;
        }

        if (!TileQuantumCPU.isSupportedPattern(pattern, player.level())) {
            player.displayClientMessage(Component.translatable("gui.crazyae.quantum_cpu.wrong_pattern"), true);
            return;
        }

        var node = menu.getNetworkNode();
        var grid = node != null ? node.getGrid() : null;
        if (grid == null) {
            return;
        }

        for (var qcpu : grid.getMachines(TileQuantumCPU.class)) {
            var rest = qcpu.acceptPattern(pattern.copy());
            if (rest.isEmpty()) {
                slot.set(ItemStack.EMPTY);
                menu.broadcastChanges();
                return;
            }
        }

        player.displayClientMessage(Component.translatable("gui.crazyae.quantum_cpu.no_free_cpu"), true);
    }
}
