package dev.beecube31.crazyae2.common.network.packets;

import appeng.helpers.InventoryAction;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.network.CrazyPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PacketInventoryAction extends CrazyPacket {

    private final InventoryAction action;
    private final int slot;
    private final long id;
    private final ItemStack slotItem;

    public PacketInventoryAction(FriendlyByteBuf stream) {
        this.action = InventoryAction.values()[stream.readInt()];
        this.slot = stream.readInt();
        this.id = stream.readLong();
        this.slotItem = stream.readItem();
    }

    // api
    public PacketInventoryAction(InventoryAction action, int slot, ItemStack slotItem) {

        if (Platform.isClient() && action != InventoryAction.SET_FILTER) {
            throw new IllegalStateException("invalid packet, client cannot post inv actions with stacks.");
        }

        this.action = action;
        this.slot = slot;
        this.id = 0;
        this.slotItem = slotItem.copy();

        final FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());

        data.writeInt(this.getPacketID());
        data.writeInt(action.ordinal());
        data.writeInt(slot);
        data.writeLong(this.id);
        data.writeItem(this.slotItem);

        this.configureWrite(data);
    }

    // api
    public PacketInventoryAction(InventoryAction action, int slot, long id) {
        this.action = action;
        this.slot = slot;
        this.id = id;
        this.slotItem = null;

        var data = new FriendlyByteBuf(Unpooled.buffer());

        data.writeInt(this.getPacketID());
        data.writeInt(action.ordinal());
        data.writeInt(slot);
        data.writeLong(id);
        data.writeItem(ItemStack.EMPTY);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (player.containerMenu instanceof CrazyAEBaseContainer baseContainer) {
            if (action == InventoryAction.SET_FILTER) {
                baseContainer.setFilter(this.slot, this.slotItem);
            } else {
                baseContainer.doAction(player, this.action, this.slot, this.id);
            }
        }
    }

}
