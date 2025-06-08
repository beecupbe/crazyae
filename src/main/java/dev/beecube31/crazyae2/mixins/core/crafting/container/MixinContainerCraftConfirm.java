package dev.beecube31.crazyae2.mixins.core.crafting.container;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.IPart;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.core.AELog;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.crafting.CraftingJob;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.parts.reporting.PartCraftingTerminal;
import appeng.parts.reporting.PartExpandedProcessingPatternTerminal;
import appeng.parts.reporting.PartPatternTerminal;
import appeng.parts.reporting.PartTerminal;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import dev.beecube31.crazyae2.craftsystem.CraftingHostType;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftHostRecord;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingJob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Future;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm extends AEBaseContainer {
    public MixinContainerCraftConfirm(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Shadow protected abstract void setSelectedCpu(int selectedCpu);

    @Shadow public abstract int getSelectedCpu();

    @Shadow protected abstract void setCpuAvailableBytes(long cpuBytesAvail);

    @Shadow protected abstract void setCpuCoProcessors(int cpuCoProcessors);

    @Shadow protected abstract void setName(@NotNull String myName);

    @Shadow public abstract long getUsedBytes();

    @Shadow protected abstract void setNoCPU(boolean noCPU);

    @Shadow protected abstract Future<ICraftingJob> getJob();

    @Shadow private ICraftingJob result;

    @Shadow protected abstract void setSimulation(boolean simulation);

    @Shadow public abstract boolean isAutoStart();

    @Shadow protected abstract void setUsedBytes(long bytesUsed);

    @Shadow public abstract void setJob(Future<ICraftingJob> job);

    @Shadow public abstract boolean isSimulation();

    @Shadow public abstract void setAutoStart(boolean autoStart);

    @Shadow public abstract World getWorld();

    @Shadow protected abstract IActionSource getActionSrc();

    @Shadow public int selectedCpu;

    @Unique private final ArrayList<CrazyCraftHostRecord> crazyae$allAvailableWorkers = new ArrayList<>();

    @Unique private final ArrayList<CrazyCraftHostRecord> crazyae$filteredWorkers = new ArrayList<>();

    @Unique
    public int crazyae$getCurrentCpu() {
        return this.selectedCpu != -1 ? this.selectedCpu : 0;
    }

    /**
     * @author Beecube31
     * @reason Support my own autocrafting system
     * @since v0.6
     */
    @Overwrite
    public void cycleCpu(final boolean next) {
        int listSize = this.crazyae$filteredWorkers.size();
        if (listSize == 0) {
            this.setSelectedCpu(-1);
            crazyae$updateGuiForSelection();
            return;
        }

        if (next) {
            this.setSelectedCpu(this.getSelectedCpu() + 1);
        } else {
            this.setSelectedCpu(this.getSelectedCpu() - 1);
        }

        if (this.getSelectedCpu() < -1) {
            this.setSelectedCpu(listSize - 1);
        } else if (this.getSelectedCpu() >= listSize) {
            this.setSelectedCpu(-1);
        }

        crazyae$updateGuiForSelection();
    }

    /**
     * @author Beecube31
     * @reason Support my own autocrafting system
     * @since v0.6
     */
    @Overwrite
    private void sendCPUs() {
        Collections.sort(this.crazyae$filteredWorkers);

        if (this.getSelectedCpu() >= this.crazyae$filteredWorkers.size()) {
            this.setSelectedCpu(-1);
            this.setCpuAvailableBytes(0);
            this.setCpuCoProcessors(0);
            this.setName("");
        } else if (this.getSelectedCpu() != -1) {
            this.setName(this.crazyae$filteredWorkers.get(this.getSelectedCpu()).getName());
            this.setCpuAvailableBytes(this.crazyae$filteredWorkers.get(this.getSelectedCpu()).getSize());
            this.setCpuCoProcessors(this.crazyae$filteredWorkers.get(this.getSelectedCpu()).getProcessors());
        }
    }

    /**
     * @author Beecube31
     * @reason Support my own autocrafting system
     * @since v0.6
     */
    @Overwrite
    public void detectAndSendChanges() {
        if (Platform.isClient()) {
            return;
        }

        final IActionHost h = ((IActionHost) this.getTarget());
        if (h == null) {
            this.setValidContainer(false);
            return;
        }
        IGrid grid = h.getActionableNode().getGrid();

        final ICraftingGrid cc = grid.getCache(ICraftingGrid.class);
        final ICrazyAutocraftingSystem sys = grid.getCache(ICrazyAutocraftingSystem.class);

        boolean listChanged = false;
        Set<ICraftingCPU> currentCpus = cc.getCpus();
        Set<ICrazyCraftHost> currentWorkers = sys.getWorkers();

        if (crazyae$allAvailableWorkers.size() != currentCpus.size() + currentWorkers.size()) {
            listChanged = true;
        }

        if (listChanged) {
            this.crazyae$allAvailableWorkers.clear();
            for (final ICraftingCPU c : currentCpus) {
                if (c.getAvailableStorage() >= this.getUsedBytes() && !c.isBusy()) {
                    this.crazyae$allAvailableWorkers.add(new CrazyCraftHostRecord(c.getAvailableStorage(), c.getCoProcessors(), c, CraftingHostType.AE_HOST));
                }
            }
            for (final ICrazyCraftHost c : currentWorkers) {
                if (c.getStorageCount() >= this.getUsedBytes() && !c.isBusy()) {
                    this.crazyae$allAvailableWorkers.add(new CrazyCraftHostRecord(c.getStorageCount(), c.getAcceleratorCount(), c, CraftingHostType.CRAZYAE_HOST));
                }
            }
            Collections.sort(this.crazyae$allAvailableWorkers);
        }

        this.crazyae$updateFilteredWorkers();

        this.setNoCPU(this.crazyae$filteredWorkers.isEmpty());

        super.detectAndSendChanges();

        if (this.getJob() != null && this.getJob().isDone()) {
            try {
                this.result = this.getJob().get();

                this.crazyae$updateFilteredWorkers();
                this.crazyae$updateState(grid);
            } catch (final Throwable e) {
                this.getPlayerInv().player.sendMessage(new TextComponentString("Error: " + e));
                AELog.debug(e);
                this.setValidContainer(false);
                this.result = null;
            }
            this.setJob(null);
        }

        this.verifyPermissions(SecurityPermissions.CRAFT, false);
    }

    @Unique
    private void crazyae$updateFilteredWorkers() {
        this.crazyae$filteredWorkers.clear();

        if (this.result == null) {
            this.crazyae$filteredWorkers.addAll(this.crazyae$allAvailableWorkers);
        } else {
            if (this.result instanceof CrazyCraftingJob) {
                for (CrazyCraftHostRecord record : this.crazyae$allAvailableWorkers) {
                    if (record.getType() == CraftingHostType.CRAZYAE_HOST) {
                        this.crazyae$filteredWorkers.add(record);
                    }
                }
            } else {
                for (CrazyCraftHostRecord record : this.crazyae$allAvailableWorkers) {
                    if (record.getType() == CraftingHostType.AE_HOST) {
                        this.crazyae$filteredWorkers.add(record);
                    }
                }
            }
        }

        if (this.getSelectedCpu() >= this.crazyae$filteredWorkers.size()) {
            this.setSelectedCpu(-1);
            crazyae$updateGuiForSelection();
        }
    }

    @Unique
    private void crazyae$updateState(IGrid grid) {
        if (!this.result.isSimulation()) {
            this.setSimulation(false);
            if (this.isAutoStart()) {
                this.startJob();
                return;
            }
        } else {
            this.setSimulation(true);
        }

        try {
            final PacketMEInventoryUpdate a = new PacketMEInventoryUpdate((byte) 0);
            final PacketMEInventoryUpdate b = new PacketMEInventoryUpdate((byte) 1);
            final PacketMEInventoryUpdate c = this.result.isSimulation() ? new PacketMEInventoryUpdate((byte) 2) : null;

            final IItemList<IAEItemStack> plan = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
            this.result.populatePlan(plan);

            this.setUsedBytes(this.result.getByteTotal());

            for (final IAEItemStack out : plan) {

                IAEItemStack o = out.copy();
                o.reset();
                o.setStackSize(out.getStackSize());

                final IAEItemStack p = out.copy();
                p.reset();
                p.setStackSize(out.getCountRequestable());

                final IStorageGrid sg = grid.getCache(IStorageGrid.class);
                final IMEMonitor<IAEItemStack> items = sg.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                IAEItemStack m = null;
                if (c != null && this.result.isSimulation()) {
                    m = o.copy();
                    o = items.extractItems(o, Actionable.SIMULATE, this.getActionSrc());

                    if (o == null) {
                        o = m.copy();
                        o.setStackSize(0);
                    }

                    m.setStackSize(m.getStackSize() - o.getStackSize());
                }

                if (o.getStackSize() > 0) {
                    a.appendItem(o);
                }

                if (p.getStackSize() > 0) {
                    b.appendItem(p);
                }

                if (c != null && m != null && m.getStackSize() > 0) {
                    c.appendItem(m);
                }
            }

            for (final Object g : this.listeners) {
                if (g instanceof EntityPlayer) {
                    NetworkHandler.instance().sendTo(a, (EntityPlayerMP) g);
                    NetworkHandler.instance().sendTo(b, (EntityPlayerMP) g);
                    if (c != null) {
                        NetworkHandler.instance().sendTo(c, (EntityPlayerMP) g);
                    }
                }
            }
        } catch (final IOException e) {
            // :P
        }
    }

    @Unique
    private void crazyae$updateGuiForSelection() {
        if (this.getSelectedCpu() == -1) {
            this.setCpuAvailableBytes(0);
            this.setCpuCoProcessors(0);
            this.setName("");
        } else {
            if (this.getSelectedCpu() < this.crazyae$filteredWorkers.size()) {
                CrazyCraftHostRecord selected = this.crazyae$filteredWorkers.get(this.getSelectedCpu());
                this.setName(selected.getName());
                this.setCpuAvailableBytes(selected.getSize());
                this.setCpuCoProcessors(selected.getProcessors());
            } else {
                this.setSelectedCpu(-1);
                crazyae$updateGuiForSelection();
            }
        }
    }

    @Unique
    private boolean crazyae$validateState() {
        if (this.crazyae$filteredWorkers.isEmpty() || this.result == null) {
            return false;
        }

        int currentIndex = this.crazyae$getCurrentCpu();
        if (currentIndex < 0 || currentIndex >= this.crazyae$filteredWorkers.size()) {
            return false;
        }

        CrazyCraftHostRecord host = this.crazyae$filteredWorkers.get(currentIndex);

        if (host.getType() == CraftingHostType.AE_HOST) {
            return this.result instanceof CraftingJob;
        } else if (host.getType() == CraftingHostType.CRAZYAE_HOST) {
            return this.result instanceof CrazyCraftingJob;
        }

        return false;
    }

    /**
     * @author Beecube31
     * @reason Support my own autocrafting system
     * @since v0.6
     */
    @Overwrite
    public void startJob() {
        if (!this.crazyae$validateState()) return;

        GuiBridge originalGui = null;

        final IActionHost ah = this.getActionHost();
        if (ah instanceof WirelessTerminalGuiObject) {
            ItemStack myIcon = ((WirelessTerminalGuiObject) ah).getItemStack();
            originalGui = (GuiBridge) AEApi.instance().registries().wireless().getWirelessTerminalHandler(myIcon).getGuiHandler(myIcon);
        }

        if (ah instanceof PartTerminal) {
            originalGui = GuiBridge.GUI_ME;
        }

        if (ah instanceof PartCraftingTerminal) {
            originalGui = GuiBridge.GUI_CRAFTING_TERMINAL;
        }

        if (ah instanceof PartPatternTerminal) {
            originalGui = GuiBridge.GUI_PATTERN_TERMINAL;
        }

        if (ah instanceof PartExpandedProcessingPatternTerminal) {
            originalGui = GuiBridge.GUI_EXPANDED_PROCESSING_PATTERN_TERMINAL;
        }

        final IActionHost h = ((IActionHost) this.getTarget());
        if (h == null) {
            return;
        }
        IGridNode node = h.getActionableNode();
        IGrid grid = node.getGrid();

        if (this.result != null && !this.isSimulation()) {
            final ICraftingGrid cc = grid.getCache(ICraftingGrid.class);
            final ICrazyAutocraftingSystem sys = grid.getCache(ICrazyAutocraftingSystem.class);
            ICraftingLink g = null;
            CrazyCraftHostRecord host = this.crazyae$filteredWorkers.get(this.crazyae$getCurrentCpu());

            switch (host.getType()) {
                case CRAZYAE_HOST -> g = sys.submitCraftingJob(this.result, null, host.getCrazyWorker(), this.getActionSrc());
                case AE_HOST -> g = cc.submitJob(this.result, null, host.getCpu(), true, this.getActionSrc());
            }

            this.setAutoStart(false);
            if (g == null) {

                switch (host.getType()) {
                    case CRAZYAE_HOST ->
                        this.setJob(sys.beginCraftingJob(this.getWorld(), grid, this.getActionSrc(), this.result.getOutput(), null));
                    case AE_HOST ->
                        this.setJob(cc.beginCraftingJob(this.getWorld(), grid, this.getActionSrc(), this.result.getOutput(), null));
                }
            } else if (originalGui != null && this.getOpenContext() != null) {
                final TileEntity te = this.getOpenContext().getTile();
                if (te != null) {
                    Platform.openGUI(this.getInventoryPlayer().player, te, this.getOpenContext().getSide(), originalGui);
                } else {
                    if (ah instanceof IInventorySlotAware i) {
                        Platform.openGUI(this.getInventoryPlayer().player, i.getInventorySlot(), originalGui, i.isBaubleSlot());
                    }
                }
            }
        }
    }
}
