package dev.beecube31.crazyae2.craftsystem;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.core.AppEng;
import appeng.core.features.AEFeature;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketCraftingToast;
import appeng.crafting.CraftingWatcher;
import appeng.me.GridAccessException;
import appeng.me.cache.CraftingGridCache;
import appeng.tile.crafting.TileCraftingMonitorTile;
import appeng.util.Platform;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvOCTile;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.util.*;

public abstract class CrazyCraftContainer extends CrazyAENetworkInvOCTile implements ICrazyCraftHost {

    private static final String LOG_MARK_AS_COMPLETE = "CrazyCraftContainer :: Completed job for %s.";

    protected final long[] usedOps = new long[3];
    protected ICraftingLink myLastLink;
    protected final Map<ICraftingPatternDetails, TaskProgress> tasks = new HashMap<>();
    protected CrazyCraftingInventory inventory = new CrazyCraftingInventory();
    protected IItemList<IAEItemStack> waitingFor = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
    protected final List<TileCraftingMonitorTile> status = new ArrayList<>();
    protected final HashMap<IMEMonitorHandlerReceiver<IAEItemStack>, Object> listeners = new HashMap<>();

    protected long remainingOperations;
    protected long lastTime;
    protected long elapsedTime;
    protected double remainingItemCount;
    protected double startItemCount;
    protected UUID requestingPlayerUUID;

    protected IAEItemStack finalOutput;
    protected boolean waiting = false;
    protected boolean isComplete = true;
    protected boolean somethingChanged;

    public void addStorage(final IAEItemStack extractItems) {
        this.inventory.injectItems(extractItems, Actionable.MODULATE, null);
    }

    public void addEmitable(final IAEItemStack i) {
        this.waitingFor.add(i);
        this.postCraftingStatusChange(i);
    }

    public void getListOfItem(final IItemList<IAEItemStack> list, final CraftingItemList whichList) {
        switch (whichList) {
            case ACTIVE:
                for (final IAEItemStack ais : this.waitingFor) {
                    list.add(ais);
                }
                break;
            case PENDING:
                for (final Map.Entry<ICraftingPatternDetails, TaskProgress> t : this.tasks.entrySet()) {
                    for (IAEItemStack ais : t.getKey().getCondensedOutputs()) {
                        ais = ais.copy();
                        ais.setStackSize(ais.getStackSize() * t.getValue().value());
                        list.add(ais);
                    }
                }
                break;
            case STORAGE:
                this.inventory.getAvailableItems(list);
                break;
            default:
            case ALL:
                this.inventory.getAvailableItems(list);

                for (final IAEItemStack ais : this.waitingFor) {
                    list.add(ais);
                }

                for (final Map.Entry<ICraftingPatternDetails, TaskProgress> t : this.tasks.entrySet()) {
                    for (IAEItemStack ais : t.getKey().getCondensedOutputs()) {
                        ais = ais.copy();
                        ais.setStackSize(ais.getStackSize() * t.getValue().value());
                        list.add(ais);
                    }
                }
                break;
        }
    }

    protected void prepareElapsedTime() {
        this.lastTime = System.nanoTime();
        this.elapsedTime = 0;

        final IItemList<IAEItemStack> list = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();

        this.getListOfItem(list, CraftingItemList.ACTIVE);
        this.getListOfItem(list, CraftingItemList.PENDING);

        double itemCount = 0;
        for (final IAEItemStack ge : list) {
            itemCount += ge.getStackSize();
        }

        this.startItemCount = itemCount;
        this.remainingItemCount = itemCount;
    }

    protected void completeJob() {
        if (this.isComplete && this.tasks.isEmpty() && this.waitingFor.isEmpty()) {
            return;
        }


        this.isComplete = true;

        if (this.myLastLink != null) {
            ((CrazyCraftingLink) this.myLastLink).markDone();
            this.myLastLink = null;
        }

        if (AELog.isCraftingLogEnabled() && this.finalOutput != null) {
            final IAEItemStack logStack = this.finalOutput.copy();
            AELog.crafting(LOG_MARK_AS_COMPLETE, logStack.getStackSize() > 0 ? logStack : "Unknown job output");
        }

        if (!this.waitingFor.isEmpty()) {
            IItemList<IAEItemStack> toPost = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
            for (IAEItemStack stack : this.waitingFor) {
                if (stack != null && stack.getStackSize() > 0) {
                    IAEItemStack negStack = stack.copy();
                    negStack.setStackSize(-stack.getStackSize());
                    toPost.add(negStack);
                }
            }
            this.waitingFor.resetStatus();
            if (!toPost.isEmpty()) {
                for(IAEItemStack negStack : toPost){
                    this.postCraftingStatusChange(negStack);
                    Utils.postChange(negStack, this.getActionSource(), this.getListeners());
                }
            }
        }

        this.remainingItemCount = 0;
        this.startItemCount = 0;
        this.lastTime = 0;
        this.elapsedTime = 0;
        this.tasks.clear();

        this.notifyRequester(false);
        this.requestingPlayerUUID = null;

        try {
            if (this.getProxy().getNode() != null) {
                this.getProxy().getTick().alertDevice(this.getProxy().getNode());
            }
        } catch (GridAccessException ignored) {}

        this.storeItems(this.getActionSource()); // marks dirty

        this.finalOutput = null;
        this.updateCrafting();
    }

    public void cancel(IActionSource src) {
        if (this.myLastLink != null) {
            this.myLastLink.cancel();
        }

        final IItemList<IAEItemStack> list;
        this.getListOfItem(list = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList(), CraftingItemList.ALL);
        for (final IAEItemStack is : list) {
            Utils.postChange(is, src, this.getListeners());
        }

        this.isComplete = true;
        this.myLastLink = null;
        this.tasks.clear();

        final List<IAEItemStack> items = new ArrayList<>(this.waitingFor.size());
        this.waitingFor.forEach(stack -> items.add(stack.copy().setStackSize(-stack.getStackSize())));

        this.waitingFor.resetStatus();

        for (final IAEItemStack is : items) {
            this.postCraftingStatusChange(is);
        }

        notifyRequester(true);
        this.requestingPlayerUUID = null;
        this.finalOutput = null;

        this.storeItems(src); // marks dirty
    }

    protected void storeItems(IActionSource src) {
        Preconditions.checkState(isComplete, "CPU should be complete to prevent re-insertion when dumping items");

        try {
            final IGrid g = this.getProxy().getGrid();
            if (g == null) {
                return;
            }

            final IStorageGrid sg = g.getCache(IStorageGrid.class);
            final IMEInventory<IAEItemStack> networkInv = sg.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            IItemList<IAEItemStack> currentCpuContents = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
            this.inventory.getAvailableItems(currentCpuContents);

            if (currentCpuContents.isEmpty()) {
                return;
            }


            List<IAEItemStack> whatsLeftInCpu = new ArrayList<>();
            boolean changed = false;

            for (IAEItemStack stackInCpu : currentCpuContents) {
                if (stackInCpu == null || stackInCpu.getStackSize() <= 0) continue;

                IAEItemStack toInjectToNetwork = stackInCpu.copy();

                Utils.postChange(toInjectToNetwork, src, this.getListeners());
                IAEItemStack remainderFromNetwork = networkInv.injectItems(toInjectToNetwork, Actionable.MODULATE, src);

                if (remainderFromNetwork != null && remainderFromNetwork.getStackSize() > 0) {
                    whatsLeftInCpu.add(remainderFromNetwork.copy());
                }
                changed = true;
            }

            this.inventory = new CrazyCraftingInventory();
            for (IAEItemStack remainingStack : whatsLeftInCpu) {
                if (remainingStack.getStackSize() > 0) {
                    this.inventory.getItemList().addStorage(remainingStack.copy());
                }
            }

            if (changed) {
                this.markDirty();
            }
        } catch (GridAccessException ignored) {}
    }

    private void notifyRequester(boolean cancelled) {
        if (!Platform.isServer()) return;
        if (this.requestingPlayerUUID == null) return;
        if (this.finalOutput == null) return;
        if (!AEConfig.instance().isFeatureEnabled(AEFeature.CRAFTING_TOASTS)) return;

        var player = AppEng.proxy.getPlayerByUUID(this.requestingPlayerUUID);
        if (player instanceof EntityPlayerMP playerMP) {
            try {
                NetworkHandler.instance().sendTo(new PacketCraftingToast(this.finalOutput, cancelled), playerMP);
            } catch (IOException ignored) {}
        }

        this.finalOutput = null;
    }

    protected void postCraftingStatusChange(final IAEItemStack diff) {
        try {
            if (this.getProxy().getGrid() == null) {
                return;
            }

            final CraftingGridCache sg = this.getProxy().getGrid().getCache(ICraftingGrid.class);
            final CrazyAutocraftingSystem sys = this.getProxy().getGrid().getCache(ICrazyAutocraftingSystem.class);

            if (sys.getInterestManager().containsKey(diff)) {
                final Collection<CrazyCraftingWatcher> list = sys.getInterestManager().get(diff);

                if (!list.isEmpty()) {
                    for (final CrazyCraftingWatcher iw : list) {
                        iw.getHost().onRequestChange(sg, diff);
                    }
                }
            }

            if (sg.getInterestManager().containsKey(diff)) {
                final Collection<CraftingWatcher> list = sg.getInterestManager().get(diff);

                if (!list.isEmpty()) {
                    for (final CraftingWatcher iw : list) {
                        iw.getHost().onRequestChange(sg, diff);
                    }
                }
            }
        } catch (GridAccessException ignored) {}
    }

    protected Iterator<Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> getListeners() {
        return this.listeners.entrySet().iterator();
    }

    protected void updateCrafting() {
        IAEItemStack send = this.finalOutput;

        if (this.finalOutput != null && this.finalOutput.getStackSize() <= 0) {
            send = null;
        }

        for (final TileCraftingMonitorTile t : this.status) {
            t.setJob(send);
        }
    }

    public static class TaskProgress {
        public long value;

        public long value() {
            return value;
        }
    }
}
