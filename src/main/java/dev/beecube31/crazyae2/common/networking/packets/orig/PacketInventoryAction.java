package dev.beecube31.crazyae2.common.networking.packets.orig;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.IJEITargetSlot;
import appeng.core.AELog;
import appeng.core.AppEng;
import appeng.core.sync.network.INetworkInfo;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.InventoryAction;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotDisconnected;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

public class PacketInventoryAction extends CrazyAEPacket {

    private final InventoryAction action;
    private final int slot;
    private final long id;
    private final IAEItemStack slotItem;

    public PacketInventoryAction(final ByteBuf stream) throws IOException {
        this.action = InventoryAction.values()[stream.readInt()];
        this.slot = stream.readInt();
        this.id = stream.readLong();
        final boolean hasItem = stream.readBoolean();

        if (hasItem) {
            this.slotItem = AEItemStack.fromPacket(stream);
        } else {
            this.slotItem = null;
        }
    }

    public PacketInventoryAction(final InventoryAction action, final int slot, final IAEItemStack slotItem) throws IOException {
        if (Platform.isClient()) {
            throw new IllegalStateException("invalid packet, client cannot post inv actions with stacks.");
        }

        this.action = action;
        this.slot = slot;
        this.id = 0;
        this.slotItem = slotItem;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeInt(action.ordinal());
        data.writeInt(slot);
        data.writeLong(this.id);

        if (slotItem == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            slotItem.writeToPacket(data);
        }

        this.configureWrite(data);
    }

    public PacketInventoryAction(final InventoryAction action, final IJEITargetSlot slot, final IAEItemStack slotItem) throws IOException {

        this.action = action;
        if (slot instanceof SlotFake) {
            this.slot = ((SlotFake) slot).slotNumber;
            this.id = 0;
        } else if (slot instanceof SlotDisconnected) {
            this.slot = ((SlotDisconnected) slot).getSlotIndex();
            this.id = ((SlotDisconnected) slot).getSlot().getId();
        } else {
            this.slot = ((GuiFluidSlot) slot).getId();
            this.id = 0;
        }
        this.slotItem = slotItem;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeInt(action.ordinal());
        data.writeInt(this.slot);
        data.writeLong(this.id);

        if (slotItem == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            slotItem.writeToPacket(data);
        }

        this.configureWrite(data);
    }

    // api
    public PacketInventoryAction(final InventoryAction action, final int slot, final long id) {
        this.action = action;
        this.slot = slot;
        this.id = id;
        this.slotItem = null;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeInt(action.ordinal());
        data.writeInt(slot);
        data.writeLong(id);
        data.writeBoolean(false);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final EntityPlayerMP sender = (EntityPlayerMP) player;
        if (sender.openContainer instanceof final CrazyAEBaseContainer baseContainer) {
            if (this.action == InventoryAction.PLACE_JEI_GHOST_ITEM) {
                if (this.slot < sender.openContainer.inventorySlots.size()) {
                    Slot senderSlot = sender.openContainer.inventorySlots.get(this.slot);
                    if (senderSlot instanceof SlotFake) {
                        if (this.slotItem != null) {
                            senderSlot.putStack(this.slotItem.createItemStack());
                            if (senderSlot.getStack().isEmpty()) {
                                IAEFluidStack aefs = AEFluidStack.fromNBT(this.slotItem.getDefinition().getTagCompound());
                                if (aefs != null) {
                                    FluidStack fluid = aefs.getFluidStack();
                                    senderSlot.putStack(AEFluidStack.fromFluidStack(fluid).asItemStackRepresentation());
                                }
                            }
                        } else {
                            senderSlot.putStack(ItemStack.EMPTY);
                        }
                        try {
                            NetworkHandler.instance().sendTo(new PacketInventoryAction(InventoryAction.UPDATE_HAND, 0, AEItemStack.fromItemStack(ItemStack.EMPTY)), sender);
                        } catch (final IOException e) {
                            AELog.debug(e);
                        }
                    }
                }
            } else {
                baseContainer.doAction(sender, this.action, this.slot, this.id);
            }
        }
    }

    @Override
    public void clientPacketData(final INetworkInfo network, final CrazyAEPacket packet, final EntityPlayer player) {
        if (this.action == InventoryAction.UPDATE_HAND) {
            if (this.slotItem == null) {
                AppEng.proxy.getPlayers().get(0).inventory.setItemStack(ItemStack.EMPTY);
            } else {
                AppEng.proxy.getPlayers().get(0).inventory.setItemStack(this.slotItem.createItemStack());
            }
        }
    }
}
