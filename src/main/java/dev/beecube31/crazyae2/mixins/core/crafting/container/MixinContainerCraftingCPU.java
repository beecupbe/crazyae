package dev.beecube31.crazyae2.mixins.core.crafting.container;

import appeng.api.networking.crafting.CraftingItemList;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.helpers.ICustomNameObject;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinContainerCraftingCPU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = ContainerCraftingCPU.class, remap = false)
public abstract class MixinContainerCraftingCPU extends AEBaseContainer implements IMEMonitorHandlerReceiver<IAEItemStack>, ICustomNameObject, IMixinContainerCraftingCPU {
    public MixinContainerCraftingCPU(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Shadow abstract CraftingCPUCluster getMonitor();

    @Shadow private String cpuName;

    @Shadow protected abstract void setMonitor(CraftingCPUCluster monitor);

    @Shadow @Final private IItemList<IAEItemStack> list;

    @Shadow protected abstract void setEstimatedTime(long eta);

    @Shadow public abstract long getEstimatedTime();

    @Unique private ICrazyCraftHost crazyae$worker = null;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void crazyae$checkForOwnWorker(InventoryPlayer ip, Object te, CallbackInfo ci) {
        if (te instanceof ICrazyCraftHost c) {
            this.setCPU(c);
        }
    }

    @Override
    public ICrazyCraftHost crazyae$getCurrentWorker() {
        return this.crazyae$worker;
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    protected void setCPU(final ICraftingCPU c) {
        if (this.getMonitor() != null) {
            this.getMonitor().removeListener(this);
        }
        if (this.crazyae$worker != null) {
            this.crazyae$worker.removeListener(this);
        }

        this.setMonitor(null);
        this.crazyae$worker = null;
        this.crazyae$sendClearPacket();
        this.list.resetStatus();

        if (c == null) {
            this.cpuName = "";
            this.setEstimatedTime(-1);
        } else {
            this.cpuName = c.getName();
            this.setEstimatedTime(0);

            if (c instanceof ICrazyCraftHost h) {
                this.crazyae$worker = h;
                this.crazyae$worker.getListOfItem(this.list, CraftingItemList.ALL);
                this.crazyae$worker.addListener(this, null);
            } else if (c instanceof CraftingCPUCluster cluster) {
                this.setMonitor(cluster);
                this.getMonitor().getListOfItem(this.list, CraftingItemList.ALL);
                this.getMonitor().addListener(this, null);
            }
        }
    }

    @Unique
    private void crazyae$sendClearPacket() {
        if (Platform.isClient()) {
            return;
        }

        try {
            final PacketValueConfig packet = new PacketValueConfig("CraftingStatus", "Clear");
            for (final Object g : this.listeners) {
                if (g instanceof EntityPlayerMP player) {
                    NetworkHandler.instance().sendTo(packet, player);
                }
            }
        } catch (final IOException e) {
            AELog.debug(e);
        }
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    public void cancelCrafting() {
        if (this.getMonitor() != null) {
            this.getMonitor().cancel();
        } else if (this.crazyae$worker != null) {
            this.crazyae$worker.cancel(this.getActionSource());
        }
        this.setEstimatedTime(-1);
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    @Override
    public void removeListener(final @NotNull IContainerListener c) {
        super.removeListener(c);
        if (this.listeners.isEmpty()) {
            if (this.getMonitor() != null) {
                this.getMonitor().removeListener(this);
            }
            if (this.crazyae$worker != null) {
                this.crazyae$worker.removeListener(this);
            }
        }
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    @Override
    public void onContainerClosed(final @NotNull EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.getMonitor() != null) {
            this.getMonitor().removeListener(this);
        }
        if (this.crazyae$worker != null) {
            this.crazyae$worker.removeListener(this);
        }
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    @Override
    public void detectAndSendChanges() {
        if (Platform.isServer()) {
            ICraftingCPU currentCpu = this.getMonitor() != null ? this.getMonitor() : this.crazyae$worker;
            final long elapsedTime =  this.getMonitor() != null ? this.getMonitor().getElapsedTime() : this.crazyae$worker != null ? this.crazyae$worker.getElapsedTime() : -1L;

            if (currentCpu != null) {
                if (this.getEstimatedTime() >= 0) {
                    final double remainingItems = currentCpu.getRemainingItemCount();
                    final double startItems = currentCpu.getStartItemCount();
                    final long eta = (long) (elapsedTime / Math.max(1.0, startItems - remainingItems) * remainingItems);
                    this.setEstimatedTime(eta);
                }

                if (!this.list.isEmpty()) {
                    try {
                        final PacketMEInventoryUpdate storagePacket = new PacketMEInventoryUpdate((byte) 0);
                        final PacketMEInventoryUpdate activePacket = new PacketMEInventoryUpdate((byte) 1);
                        final PacketMEInventoryUpdate pendingPacket = new PacketMEInventoryUpdate((byte) 2);

                        for (final IAEItemStack out : this.list) {
                            storagePacket.appendItem(this.getMonitor() != null ? this.getMonitor().getItemStack(out, CraftingItemList.STORAGE)
                                    : this.crazyae$worker.getItemStack(out, CraftingItemList.STORAGE));
                            activePacket.appendItem(this.getMonitor() != null ? this.getMonitor().getItemStack(out, CraftingItemList.ACTIVE)
                                    : this.crazyae$worker.getItemStack(out, CraftingItemList.ACTIVE));
                            pendingPacket.appendItem(this.getMonitor() != null ? this.getMonitor().getItemStack(out, CraftingItemList.PENDING)
                                    : this.crazyae$worker.getItemStack(out, CraftingItemList.PENDING));
                        }

                        this.list.resetStatus();

                        for (final Object g : this.listeners) {
                            if (g instanceof EntityPlayerMP player) {
                                if (!storagePacket.isEmpty()) NetworkHandler.instance().sendTo(storagePacket, player);
                                if (!activePacket.isEmpty()) NetworkHandler.instance().sendTo(activePacket, player);
                                if (!pendingPacket.isEmpty()) NetworkHandler.instance().sendTo(pendingPacket, player);
                            }
                        }
                    } catch (final IOException e) {
                        AELog.debug(e);
                    }
                }
            }
        }
        super.detectAndSendChanges();
    }
}
