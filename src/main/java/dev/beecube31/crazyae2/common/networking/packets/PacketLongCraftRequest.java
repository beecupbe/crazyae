package dev.beecube31.crazyae2.common.networking.packets;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.IActionHost;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.core.AELog;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.INetworkInfo;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.concurrent.Future;

public class PacketLongCraftRequest extends CrazyAEPacket {

    private final long amount;
    private final boolean heldShift;

    public PacketLongCraftRequest(final ByteBuf stream) {
        this.heldShift = stream.readBoolean();
        this.amount = stream.readLong();
    }

    public PacketLongCraftRequest(final long craftAmt, final boolean shift) {
        this.amount = craftAmt;
        this.heldShift = shift;

        final ByteBuf data = Unpooled.buffer();

        data.writeInt(this.getPacketID());
        data.writeBoolean(shift);
        data.writeLong(this.amount);

        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        if (player.openContainer instanceof ContainerCraftAmount cca) {
            final Object target = cca.getTarget();
            if (target instanceof IActionHost ah) {
                final IGridNode gn = ah.getActionableNode();

                final IGrid g = gn.getGrid();
                if (cca.getItemToCraft() == null) {
                    return;
                }

                cca.getItemToCraft().setStackSize(this.amount);

                Future<ICraftingJob> futureJob = null;
                try {
                    final ICraftingGrid cg = g.getCache(ICraftingGrid.class);
                    final ICrazyAutocraftingSystem sys = g.getCache(ICrazyAutocraftingSystem.class);

                    futureJob = sys.containsCraftingItem(cca.getItemToCraft())
                            ? sys.beginCraftingJob(cca.getWorld(), cca.getGrid(), cca.getActionSrc(), cca.getItemToCraft(), null)
                            : cg.beginCraftingJob(cca.getWorld(), cca.getGrid(), cca.getActionSrc(), cca.getItemToCraft(), null);

                    final ContainerOpenContext context = cca.getOpenContext();
                    if (context != null) {
                        final TileEntity te = context.getTile();
                        if (te != null) {
                            Platform.openGUI(player, te, cca.getOpenContext().getSide(), GuiBridge.GUI_CRAFTING_CONFIRM);
                        } else {
                            if (ah instanceof IInventorySlotAware i) {
                                Platform.openGUI(player, i.getInventorySlot(), GuiBridge.GUI_CRAFTING_CONFIRM, i.isBaubleSlot());
                            }
                        }

                        if (player.openContainer instanceof ContainerCraftConfirm ccc) {
                            ccc.setAutoStart(this.heldShift);
                            ccc.setJob(futureJob);
                            cca.detectAndSendChanges();
                        }
                    }
                } catch (final Throwable e) {
                    if (futureJob != null) {
                        futureJob.cancel(true);
                    }
                    AELog.debug(e);
                }
            }
        }
    }
}
