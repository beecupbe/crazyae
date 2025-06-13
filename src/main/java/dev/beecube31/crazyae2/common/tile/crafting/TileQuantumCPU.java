package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.crafting.CraftBranchFailure;
import appeng.helpers.IPriorityHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.me.helpers.PlayerSource;
import appeng.tile.crafting.TileCraftingMonitorTile;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.interfaces.IGridHostMonitorable;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.*;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinCraftingCPUStatus;
import dev.beecube31.crazyae2.common.networking.events.MECraftHostPatternsChangedEv;
import dev.beecube31.crazyae2.common.networking.events.MECraftHostStateUpdateEv;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.config.CrazyAEAutoCraftingSystemConfig;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftContainer;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingInventory;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingJob;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingLink;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TileQuantumCPU extends CrazyCraftContainer implements IConfigManagerHost, ICraftingCPU, ICrazyCraftHost, ICrazyCraftingMethod, ICrazyCraftCallback, IGridHostMonitorable, IMixinCraftingCPUStatus, IGridTickable {


    private final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, 1024, 1).setItemFilter(RestrictedSlot.PlaceableItemType.ENCODED_CRAFTING_PATTERN.associatedFilter);

    private final CrazyAEInternalInv accelsInv = new CrazyAEInternalInv(this, 18, 64).setItemFilter(RestrictedSlot.PlaceableItemType.CRAFTING_ACCELERATORS.associatedFilter);
    private final CrazyAEInternalInv storagesInv = new CrazyAEInternalInv(this, 18, 64).setItemFilter(RestrictedSlot.PlaceableItemType.CRAFTING_STORAGES.associatedFilter);

    private final Map<ICraftingPatternDetails, PendingInterfaceTask> pendingInterfaceTasks = new ConcurrentHashMap<>();

    private final IConfigManager settings;
    private boolean isPowered = false;
    private boolean cached = false;
    private final IActionSource actionSource = new MachineSource(this);

    private int priority = 1;
    private Set<ICraftingPatternDetails> craftingList = null;
    private final List<IAEItemStack> itemsToSend = new ArrayList<>();

    private long accelsCount = -1;
    private double storageCount = -1;

    private long initialTotalItems = 0;

    private long initialTotalExpectedItems = 0;

    private String myOwnName = "";

    private long millisWhenJobStarted = 0;
    private String jobInitiator = "";

    public TileQuantumCPU() {
        this.settings = new ConfigManager(this);
        this.settings.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getProxy().setIdlePowerUsage(4096.0);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isBusy() {
        boolean tasksActive = false;
        if (!this.tasks.isEmpty()) {
            for (TaskProgress p : this.tasks.values()) {
                if (p.value > 0) {
                    tasksActive = true;
                    break;
                }
            }
        }

        return (this.finalOutput != null && this.finalOutput.getStackSize() > 0) ||
                tasksActive ||
                !this.pendingInterfaceTasks.isEmpty() ||
                !this.waitingFor.isEmpty() ||
                !this.itemsToSend.isEmpty();
    }

    @Override
    public IActionSource getActionSource() {
        return this.actionSource;
    }

    @Override
    public long getAvailableStorage() {
        return (long) this.getStorageCount();
    }

    @Override
    public int getCoProcessors() {
        return (int) Math.min(this.getAcceleratorCount(), Integer.MAX_VALUE);
    }

    @Override
    public long getRemainingItemCount() {
        if (!isBusy() && (this.finalOutput == null || this.finalOutput.getStackSize() <= 0) && this.tasks.isEmpty() && this.pendingInterfaceTasks.isEmpty() && this.waitingFor.isEmpty()) {
            return 0;
        }

        long remainingItems = 0;

        for (Map.Entry<ICraftingPatternDetails, TaskProgress> entry : this.tasks.entrySet()) {
            ICraftingPatternDetails details = entry.getKey();
            TaskProgress progress = entry.getValue();
            if (progress.value > 0) {
                for (IAEItemStack output : details.getCondensedOutputs()) {
                    remainingItems += Utils.multiplySafely(output.getStackSize(), progress.value);
                }
            }
        }

        for (Map.Entry<ICraftingPatternDetails, PendingInterfaceTask> entry : this.pendingInterfaceTasks.entrySet()) {
            PendingInterfaceTask pendingTask = entry.getValue();
            if (pendingTask.details != null && pendingTask.pendingBatches > 0) {
                for (IAEItemStack output : pendingTask.details.getCondensedOutputs()) {
                    remainingItems += Utils.multiplySafely(output.getStackSize(), pendingTask.pendingBatches);
                }
            }
        }

        return Math.max(0, remainingItems);
    }

    @Override
    public long getStartItemCount() {
        return this.initialTotalExpectedItems;
    }


    @Override
    public IAEItemStack getFinalOutput() {
        if (this.finalOutput != null) {
            return this.finalOutput.copy();
        }

        return null;
    }

    @Override
    public boolean pushDetails(ICraftingPatternDetails details, long crafts) {
        TaskProgress i = this.tasks.computeIfAbsent(details, k -> new TaskProgress());
        i.value += crafts;

        return true;
    }

    @Override
    public ICraftingLink pushJob(ICraftingJob job, ICraftingRequester requester, IActionSource src) {
        if (!this.waitingFor.isEmpty()) {
            return null;
        }

        if (this.isBusy() || this.getStorageCount() < job.getByteTotal()) {
            return null;
        }

        try {
            final IStorageGrid sg = this.getProxy().getGrid().getCache(IStorageGrid.class);
            final IMEInventory<IAEItemStack> storage = sg.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            final CrazyCraftingInventory ci = new CrazyCraftingInventory(storage, true, false, false);

            try {
                this.waitingFor.resetStatus();
                ((CrazyCraftingJob) job).getTree().setJob(ci, this, src);
                if (ci.commit(src)) {
                    this.finalOutput = job.getOutput();
                    this.waiting = false;
                    this.isComplete = false;

                    if (src instanceof PlayerSource playerSource && playerSource.player().isPresent()) {
                        this.requestingPlayerUUID = playerSource.player().get().getUniqueID();
                    } else {
                        this.requestingPlayerUUID = null;
                    }

                    this.markDirty();

                    this.updateCrafting();
                    final String craftID = Utils.generateCraftingID(null);

                    this.myLastLink = new CrazyCraftingLink(Utils.generateLinkData(craftID, requester == null, false), this);

                    this.prepareElapsedTime();

                    this.initialTotalItems = 0;
                    for (TaskProgress p : this.tasks.values()) {
                        this.initialTotalItems += p.value;
                    }

                    this.initialTotalExpectedItems = 0;
                    for (Map.Entry<ICraftingPatternDetails, TaskProgress> entry : this.tasks.entrySet()) {
                        ICraftingPatternDetails details = entry.getKey();
                        TaskProgress progress = entry.getValue();
                        for (IAEItemStack output : details.getCondensedOutputs()) {
                            this.initialTotalExpectedItems += Utils.multiplySafely(output.getStackSize(), progress.value);
                        }
                    }

                    if (requester == null) {
                        this.setCraftInfo(src);
                        return this.myLastLink;
                    }

                    final ICraftingLink whatLink = new CrazyCraftingLink(Utils.generateLinkData(craftID, false, true), this);

                    this.submitLink(this.myLastLink);
                    this.submitLink(whatLink);

                    final IItemList<IAEItemStack> list = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
                    this.getListOfItem(list, CraftingItemList.ALL);
                    for (final IAEItemStack ge : list) {
                        Utils.postChange(ge, this.actionSource, this.getListeners());
                    }

                    this.setCraftInfo(src);

                    return whatLink;
                } else {
                    this.tasks.clear();
                    this.inventory.getItemList().resetStatus();
                    this.initialTotalItems = 0;
                }
            } catch (final CraftBranchFailure e) {
                this.tasks.clear();
                this.inventory.getItemList().resetStatus();
                this.initialTotalItems = 0;
            }

            final ICraftingLink link = new CrazyCraftingLink(Utils.generateLinkData(Utils.generateCraftingID(job.getOutput()), false, true), this);

            this.submitLink(link);

            final IItemList<IAEItemStack> list = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
            this.getListOfItem(list, CraftingItemList.ALL);

            for (final IAEItemStack ge : list) {
                Utils.postChange(ge, this.actionSource, this.getListeners());
            }

            this.setCraftInfo(src);

            return link;
        } catch (GridAccessException ignored) {}

        this.initialTotalItems = 0;
        return null;
    }

    private void setCraftInfo(IActionSource src) {
        String initiator;

        if (src.player().isPresent()) {
            initiator = src.player().get().getName();
        } else if (src.machine().isPresent()) {
            initiator = src.machine().get().getActionableNode().getGridBlock().getMachineRepresentation().getDisplayName();
        } else {
            initiator = "N/A";
        }

        this.jobInitiator = initiator;
        this.millisWhenJobStarted = System.currentTimeMillis();
    }

    @Override
    protected void completeJob() {
        this.updateCrafting();
        super.completeJob();
        this.initialTotalItems = 0;
        this.tasks.clear();
        this.initialTotalExpectedItems = 0;
        this.pendingInterfaceTasks.clear();
        this.jobInitiator = "";
        this.millisWhenJobStarted = 0;
        this.markDirty();
    }

    @Override
    public void cancel(IActionSource src) {
        Map<ICraftingPatternDetails, PendingInterfaceTask> tasksToCancelWithInterfaces = new HashMap<>(this.pendingInterfaceTasks);

        if (!tasksToCancelWithInterfaces.isEmpty()) {
            try {
                IGrid grid = this.getProxy().getGrid();
                if (grid != null) {
                    CrazyAutocraftingSystem cc = grid.getCache(ICrazyAutocraftingSystem.class);
                    for (Map.Entry<ICraftingPatternDetails, PendingInterfaceTask> entry : tasksToCancelWithInterfaces.entrySet()) {
                        PendingInterfaceTask taskInfo = entry.getValue();
                        if (taskInfo.details != null && taskInfo.pendingBatches > 0) {
                            Set<ICrazyInterfaceHost> relevantInterfaces = cc.findInterfaceByDetails(taskInfo.details);
                            if (relevantInterfaces != null && !relevantInterfaces.isEmpty()) {
                                for (ICrazyInterfaceHost interfaceHost : relevantInterfaces) {
                                    if (interfaceHost.getNode() != null && interfaceHost.getNode().isActive()) {
                                        interfaceHost.cancelCraftingForPattern(taskInfo.details, this);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (GridAccessException ignored) {}
        }

        this.pendingInterfaceTasks.clear();

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
                for (IAEItemStack negStack : toPost) {
                    this.postCraftingStatusChange(negStack);
                    Utils.postChange(negStack, src, this.getListeners());
                }
            }
        }

        super.cancel(src);

        this.initialTotalItems = 0;
        this.initialTotalExpectedItems = 0;
        this.jobInitiator = "";
        this.millisWhenJobStarted = 0;

        if (!this.itemsToSend.isEmpty()) {
            pushItemsOut();
        }

        this.isComplete = true;
        updateCrafting();
        markDirty();
    }

    private void updateElapsedTime() {
        final long nextStartTime = System.nanoTime();
        this.elapsedTime = this.getElapsedTime() + nextStartTime - this.lastTime;
        this.lastTime = nextStartTime;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    private void checkAndCompleteJob() {
        if (this.isComplete) {
            return;
        }

        boolean allInternalTasksDone = true;

        if (!this.tasks.isEmpty()) {
            allInternalTasksDone = this.tasks.values().stream().allMatch(p -> p.value <= 0);
        }

        boolean allInterfaceTasksDone = this.pendingInterfaceTasks.isEmpty();
        boolean allItemsReceived = this.waitingFor.isEmpty();
        boolean allItemsSent = this.itemsToSend.isEmpty();

        boolean conditionsMet = allInternalTasksDone &&
                allInterfaceTasksDone &&
                allItemsReceived &&
                allItemsSent;

        if (conditionsMet) {
            completeJob();
        }
    }

    @Override
    public void tickCraftHost(IGrid grid, CrazyAutocraftingSystem cache) {
        if (!this.getProxy().isActive()) {
            return;
        }

        if (!this.itemsToSend.isEmpty() && this.getProxy().isActive()) {
            this.pushItemsOut();
        }

        if (this.myLastLink != null) {
            if (this.myLastLink.isCanceled()) {
                this.myLastLink = null;
                this.cancel(this.actionSource);
                return;
            }
        }

        if (this.isComplete) {
            if (!this.inventory.getItemList().isEmpty()) {
                this.storeItems(this.actionSource);
            }

            if (!this.itemsToSend.isEmpty()){
                this.pushItemsOut();
            }

            return;
        }

        this.waiting = false;

        boolean hasInternalTasks = !this.tasks.isEmpty() && this.tasks.values().stream().anyMatch(p -> p.value > 0);
        boolean hasPendingInterfaceTasks = !this.pendingInterfaceTasks.isEmpty();

        if (!hasInternalTasks && !hasPendingInterfaceTasks && this.waitingFor.isEmpty()) {
            this.checkAndCompleteJob();
            if (this.isComplete) return;
        }

        this.remainingOperations = this.getAcceleratorCount() + 1 - (this.usedOps[0] + this.usedOps[1] + this.usedOps[2]);
        final long startedOps = this.remainingOperations;

        if (this.remainingOperations > 0 && (hasInternalTasks)) {
            this.updateElapsedTime();
            do {
                this.somethingChanged = false;
                this.executeCrafting(cache);

                hasInternalTasks = !this.tasks.isEmpty() && this.tasks.values().stream().anyMatch(p -> p.value > 0);
            } while (this.somethingChanged && this.remainingOperations > 0 && hasInternalTasks);
        }

        if (!hasInternalTasks && !hasPendingInterfaceTasks && this.waitingFor.isEmpty()) {
            this.checkAndCompleteJob();
            if (this.isComplete) return;
            if (this.remainingOperations <= 0) this.waiting = true;
        }

        this.usedOps[2] = this.usedOps[1];
        this.usedOps[1] = this.usedOps[0];
        this.usedOps[0] = startedOps - this.remainingOperations;

        if (this.remainingOperations <= 0 && (hasInternalTasks || hasPendingInterfaceTasks)) {
            this.waiting = true;
        } else if (!this.somethingChanged && !hasInternalTasks && !hasPendingInterfaceTasks && this.waitingFor.isEmpty()) {
            this.waiting = true;
        }

        this.checkAndCompleteJob();
    }

    private void executeCrafting(final CrazyAutocraftingSystem cc) {
        Map<ICraftingPatternDetails, Long> interfaceTasks = new HashMap<>();
        Iterator<Map.Entry<ICraftingPatternDetails, TaskProgress>> taskIterator = this.tasks.entrySet().iterator();

        while (taskIterator.hasNext()) {
            Map.Entry<ICraftingPatternDetails, TaskProgress> entry = taskIterator.next();
            ICraftingPatternDetails details = entry.getKey();
            TaskProgress progress = entry.getValue();

            if (progress.value <= 0) {
                taskIterator.remove();
                continue;
            }

            if (!details.isCraftable()) {
                interfaceTasks.merge(details, progress.value, Long::sum);
            }
        }

        if (!interfaceTasks.isEmpty()) {
            for (Map.Entry<ICraftingPatternDetails, Long> interfaceTaskEntry : interfaceTasks.entrySet()) {
                if (this.remainingOperations <= 0 && this.getCoProcessors() > 0 && interfaceTasks.values().stream().anyMatch(v -> v > 0)) break;

                ICraftingPatternDetails details = interfaceTaskEntry.getKey();
                long totalExecutionsStillNeeded = interfaceTaskEntry.getValue();

                TaskProgress originalProgress = this.tasks.get(details);
                if (originalProgress == null || originalProgress.value <= 0) {
                    continue;
                }
                totalExecutionsStillNeeded = Math.min(totalExecutionsStillNeeded, originalProgress.value);
                if (totalExecutionsStillNeeded <= 0) continue;

                Set<ICrazyInterfaceHost> candidateInterfaces = cc.findInterfaceByDetails(details);

                if (candidateInterfaces.isEmpty()) {
                    continue; // :(
                }

                List<ICrazyInterfaceHost> availableInterfaces = new ArrayList<>(candidateInterfaces);
                availableInterfaces.sort(Comparator.comparingInt(host -> ((IPriorityHost) host.getTile()).getPriority()));

                long executionsDelegated = 0;

                for (ICrazyInterfaceHost currentInterface : availableInterfaces) {
                    long maxPossibleDelegationsConfig = CrazyAEAutoCraftingSystemConfig.maxDelegationSizePerInterface;
                    long maxPossibleDelegations = totalExecutionsStillNeeded - executionsDelegated;
                    long batchSize = Math.min(maxPossibleDelegationsConfig, maxPossibleDelegations);

                    if (this.getCoProcessors() > 0) {
                        batchSize = Math.min(batchSize, this.remainingOperations);
                    } else if (maxPossibleDelegations > 0) {
                        batchSize = Math.max(1, batchSize);
                    }

                    if (batchSize <= 0) {
                        continue;
                    }

                    long estimatedByInterface = currentInterface.estimatePushableBatchSize(details, batchSize, this, this.world);

                    batchSize = Math.min(batchSize, estimatedByInterface);

                    if (batchSize <= 0) {
                        continue;
                    }

                    List<IAEItemStack> extracted = extractIngredientsForBatch(details, batchSize);
                    if (extracted == null) {
                        continue;
                    }

                    if (currentInterface.pushDetails(details, batchSize, this)) {
                        this.somethingChanged = true;
                        executionsDelegated += batchSize;


                        PendingInterfaceTask taskState = this.pendingInterfaceTasks.computeIfAbsent(
                                details, k -> new PendingInterfaceTask(details, 0));
                        taskState.pendingBatches += batchSize;

                        for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
                            if (outputTemplate != null && outputTemplate.getStackSize() > 0) {
                                IAEItemStack expectedOutput = outputTemplate.copy();
                                long amountToExpect = Utils.multiplySafely(outputTemplate.getStackSize(), batchSize);
                                if (amountToExpect > 0) {
                                    expectedOutput.setStackSize(amountToExpect);
                                    this.waitingFor.add(expectedOutput.copy());
                                    this.postCraftingStatusChange(expectedOutput.copy());
                                }
                            }
                        }

                        List<IAEItemStack> containerItemsFromInterface = handleContainerItems(details, batchSize);
                        for (IAEItemStack containerItem : containerItemsFromInterface) {
                            if (containerItem != null && containerItem.getStackSize() > 0) {
                                this.waitingFor.add(containerItem.copy());
                                this.postCraftingStatusChange(containerItem.copy());
                            }
                        }

                        if (this.getCoProcessors() > 0) {
                            this.remainingOperations -= batchSize;
                        }
                        this.markDirty();
                    } else {
                        rollbackIngredients(extracted);
                    }
                }

                if (executionsDelegated > 0) {
                    originalProgress.value -= executionsDelegated;
                    if (originalProgress.value <= 0) {
                        this.tasks.remove(details);
                    }
                }
            }
        }

        Iterator<Map.Entry<ICraftingPatternDetails, TaskProgress>> craftableTaskIterator = this.tasks.entrySet().iterator();
        while (craftableTaskIterator.hasNext()) {
            if (this.remainingOperations <= 0 && this.getCoProcessors() > 0) break;

            Map.Entry<ICraftingPatternDetails, TaskProgress> entry = craftableTaskIterator.next();
            ICraftingPatternDetails details = entry.getKey();
            TaskProgress progress = entry.getValue();

            if (progress.value <= 0) {
                craftableTaskIterator.remove();
                continue;
            }

            if (!details.isCraftable()) {
                continue;
            }

            long maxBatchThisTickForPattern = progress.value;
            if (this.getCoProcessors() > 0) {
                maxBatchThisTickForPattern = Math.min(maxBatchThisTickForPattern, this.remainingOperations);
            } else if (progress.value > 0) {
                maxBatchThisTickForPattern = Math.min(maxBatchThisTickForPattern, 1);
            }

            if (maxBatchThisTickForPattern <= 0) continue;


            long actualCraftableNow = determineActualBatchSize(details, maxBatchThisTickForPattern);
            if (actualCraftableNow <= 0) {
                continue;
            }

            List<IAEItemStack> extractedItems = extractIngredientsForBatch(details, actualCraftableNow);
            if (extractedItems == null) {
                continue;
            }

            List<IAEItemStack> craftedOutputs = dispatchBatchJobInternal(details, actualCraftableNow);
            List<IAEItemStack> containerItems = handleContainerItems(details, actualCraftableNow);
            this.somethingChanged = true;

            for (IAEItemStack outputItem : craftedOutputs) {
                if (outputItem == null || outputItem.getStackSize() <= 0) continue;
                IAEItemStack remainder = injectCraftedInternally(outputItem.copy(), Actionable.MODULATE, this.getActionSource());
                if (remainder != null && remainder.getStackSize() > 0) {
                    handleRemainder(remainder);
                }
            }

            for (IAEItemStack containerItem : containerItems) {
                if (containerItem == null || containerItem.getStackSize() <= 0) continue;
                IAEItemStack remainder = injectCraftedInternally(containerItem.copy(), Actionable.MODULATE, this.getActionSource());
                if (remainder != null && remainder.getStackSize() > 0) {
                    handleRemainder(remainder);
                }
            }

            this.markDirty();
            progress.value -= actualCraftableNow;
            if (this.getCoProcessors() > 0) {
                this.remainingOperations -= actualCraftableNow;
            }

            if (progress.value <= 0) {
                craftableTaskIterator.remove();
            }
        }

        this.checkAndCompleteJob();
    }

    private void handleRemainder(IAEItemStack remainder) {
        if (this.finalOutput != null && this.finalOutput.isSameType(remainder)) {
            this.itemsToSend.add(remainder.copy());
        } else {
            boolean merged = false;
            for (IAEItemStack stackInSend : this.itemsToSend) {
                if (stackInSend.isSameType(remainder)) {
                    stackInSend.add(remainder);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                this.itemsToSend.add(remainder.copy());
            }
        }
    }

    private IAEItemStack injectToMe(IAEItemStack item, Actionable mode, IActionSource source) {
        if (!this.getProxy().isActive() || item == null || item.getStackSize() <= 0) {
            return item;
        }

        try {
            IGrid grid = this.getProxy().getGrid();

            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            IMEInventory<IAEItemStack> storage = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            return storage.injectItems(item, mode, source);
        } catch (GridAccessException ignored) {}

        return item;
    }

    private IAEItemStack injectCraftedInternally(final IAEItemStack input, final Actionable type, final IActionSource src) {
        if (input == null || input.getStackSize() <= 0) {
            return null;
        }

        if (this.finalOutput != null && this.finalOutput.isSameType(input)) {
            return processFinalOutput(input, type);
        }

        IAEItemStack remainder = this.inventory.injectItems(input.copy(), type, src);
        if (remainder == null || remainder.getStackSize() <= 0) {
            Utils.postChange(input, src, this.getListeners());
        } else {
            long injectedAmount = input.getStackSize() - remainder.getStackSize();
            if (injectedAmount > 0) {
                IAEItemStack injectedPart = input.copy();
                injectedPart.setStackSize(injectedAmount);
                Utils.postChange(injectedPart, src, this.getListeners());
            }
        }
        return remainder;
    }

    @Override
    public void onCraftSentCallback(ICraftingPatternDetails details, long batchSize) {
//        final Iterator<Map.Entry<ICraftingPatternDetails, TaskProgress>> i = this.tasks.entrySet().iterator();
//        while (i.hasNext() && this.remainingOperations > 0) {
//            final Map.Entry<ICraftingPatternDetails, TaskProgress> entry = i.next();
//            if (details == entry.getKey()) {
//                final TaskProgress progress = entry.getValue();
//                progress.value -= batchSize;
//            }
//        }
        //NO-OP HERE
    }

    @Override
    public void onCraftBatchCompletedCallback(ICraftingPatternDetails details, long batchSizeCompletedByInterface) {
        if (batchSizeCompletedByInterface <= 0) {
            return;
        }

        if (this.isComplete) {
            List<IAEItemStack> allItems = new ArrayList<>();
            for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
                IAEItemStack itemToProcess = outputTemplate.copy();
                long amountReturned = Utils.multiplySafely(outputTemplate.getStackSize(), batchSizeCompletedByInterface);
                if (amountReturned > 0) {
                    itemToProcess.setStackSize(amountReturned);
                    allItems.add(itemToProcess);
                }
            }
            allItems.addAll(handleContainerItems(details, batchSizeCompletedByInterface));

            for (IAEItemStack item : allItems) {
                try {
                    IGrid grid = this.getProxy().getGrid();
                    if (grid != null) {
                        IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                        IMEInventory<IAEItemStack> networkInv = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                        IAEItemStack remainder = networkInv.injectItems(item.copy(), Actionable.MODULATE, this.getActionSource());
                        if (remainder != null && remainder.getStackSize() > 0) {
                            boolean merged = false;
                            for (IAEItemStack stackInSend : this.itemsToSend) {
                                if (stackInSend.isSameType(remainder)) {
                                    stackInSend.add(remainder);
                                    merged = true;
                                    break;
                                }
                            }
                            if (!merged) {
                                this.itemsToSend.add(remainder.copy());
                            }
                        }
                    }
                } catch (GridAccessException ignored) {}
            }
            this.markDirty();
            return;
        }

        PendingInterfaceTask pendingTaskStatus = this.pendingInterfaceTasks.get(details);

        if (pendingTaskStatus != null) {
            pendingTaskStatus.pendingBatches -= batchSizeCompletedByInterface;
            if (pendingTaskStatus.pendingBatches <= 0) {
                this.pendingInterfaceTasks.remove(details);
            }
        }

        List<IAEItemStack> allReturnedItems = new ArrayList<>();
        for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
            IAEItemStack itemExpected = outputTemplate.copy();
            long amountExpected = Utils.multiplySafely(outputTemplate.getStackSize(), batchSizeCompletedByInterface);
            if (amountExpected > 0) {
                itemExpected.setStackSize(amountExpected);
                allReturnedItems.add(itemExpected);
            }
        }
        allReturnedItems.addAll(handleContainerItems(details, batchSizeCompletedByInterface));

        for (IAEItemStack itemReceived : allReturnedItems) {
            if (itemReceived == null || itemReceived.getStackSize() <= 0) continue;

            long amountToConsume = itemReceived.getStackSize();

            IItemList<IAEItemStack> nextWaitingFor = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
            boolean waitingForChanged = false;

            for (IAEItemStack stackInWaitingFor : this.waitingFor) {
                IAEItemStack stackToKeep = stackInWaitingFor.copy();

                if (amountToConsume > 0 && stackInWaitingFor.isSameType(itemReceived)) {
                    long canTakeFromThisWaitingStack = Math.min(stackInWaitingFor.getStackSize(), amountToConsume);

                    IAEItemStack consumedPortion = stackInWaitingFor.copy();
                    consumedPortion.setStackSize(canTakeFromThisWaitingStack);

                    stackToKeep.decStackSize(canTakeFromThisWaitingStack);
                    amountToConsume -= canTakeFromThisWaitingStack;
                    waitingForChanged = true;

                    this.postCraftingStatusChange(consumedPortion.copy().setStackSize(-consumedPortion.getStackSize()));
                }

                if (stackToKeep.getStackSize() > 0) {
                    nextWaitingFor.add(stackToKeep);
                } else {
                    if (stackInWaitingFor.isSameType(itemReceived))
                        waitingForChanged = true;
                }
            }

            if (waitingForChanged) {
                this.waitingFor = nextWaitingFor;
            }

            IAEItemStack remainderAfterProcessing;
            IAEItemStack itemToProcess = itemReceived.copy();

            if (this.finalOutput != null && this.finalOutput.isSameType(itemToProcess)) {
                remainderAfterProcessing = processFinalOutput(itemToProcess.copy(), Actionable.MODULATE);
            } else {
                remainderAfterProcessing = this.inventory.injectItems(itemToProcess.copy(), Actionable.MODULATE, this.actionSource);
                if (remainderAfterProcessing == null || remainderAfterProcessing.getStackSize() <= 0) {
                    Utils.postChange(itemToProcess, this.actionSource, this.getListeners());
                } else {
                    long injectedAmount = itemToProcess.getStackSize() - remainderAfterProcessing.getStackSize();
                    if (injectedAmount > 0) {
                        IAEItemStack injectedPart = itemToProcess.copy();
                        injectedPart.setStackSize(injectedAmount);
                        Utils.postChange(injectedPart, this.actionSource, this.getListeners());
                    }
                }
            }

            if (remainderAfterProcessing != null && remainderAfterProcessing.getStackSize() > 0) {
                handleRemainder(remainderAfterProcessing);
            }
        }

        this.markDirty();
        this.checkAndCompleteJob();
    }


    private void rollbackIngredients(List<IAEItemStack> itemsToRollback) {
        if (itemsToRollback == null || itemsToRollback.isEmpty()) {
            return;
        }

        for (IAEItemStack extractedItem : itemsToRollback) {
            if (extractedItem == null || extractedItem.getStackSize() <= 0) {
                continue;
            }

            IAEItemStack itemToReturn = extractedItem.copy();

            IAEItemStack remainder = this.inventory.injectItems(itemToReturn, Actionable.MODULATE, this.actionSource);

            if (remainder == null || remainder.getStackSize() <= 0) {
                Utils.postChange(itemToReturn, this.actionSource, this.getListeners());
            } else {
                long returnedAmount = itemToReturn.getStackSize() - remainder.getStackSize();

                if (returnedAmount > 0) {
                    IAEItemStack partialReturn = itemToReturn.copy();
                    partialReturn.setStackSize(returnedAmount);
                    Utils.postChange(partialReturn, this.actionSource, this.getListeners());
                }
            }
        }
        this.markDirty();
    }


    private long determineActualBatchSize(ICraftingPatternDetails details, long requestedBatchSize) {
        final IAEItemStack[] condensedInputs = details.getCondensedInputs();
        long maxBatch = requestedBatchSize;

        Map<IAEItemStackMatcher, Long> requiredTotals = new HashMap<>();
        boolean canCraftAtLeastOne = true;

        for (final IAEItemStack templateInput : condensedInputs) {
            if (templateInput == null || templateInput.getStackSize() == 0) continue;
            long neededPerCraft = templateInput.getStackSize();
            long totalNeeded = Utils.multiplySafely(neededPerCraft, requestedBatchSize);

            if (totalNeeded <= 0 && requestedBatchSize > 0) {
                return 0;
            }

            IAEItemStackMatcher matcher = IAEItemStackMatcher.create(templateInput, details);
            requiredTotals.put(matcher, requiredTotals.getOrDefault(matcher, 0L) + totalNeeded);
        }

        for (Map.Entry<IAEItemStackMatcher, Long> requiredEntry : requiredTotals.entrySet()) {
            IAEItemStackMatcher matcher = requiredEntry.getKey();
            long neededPerCraft = matcher.template.getStackSize();

            if (neededPerCraft <= 0) continue;


            long totalAvailable = countAvailableItems(matcher);

            if (totalAvailable < neededPerCraft) {
                maxBatch = 0;
                canCraftAtLeastOne = false;
                break;
            }

            long possibleCraftsWithThisItem = totalAvailable / neededPerCraft;
            maxBatch = Math.min(maxBatch, possibleCraftsWithThisItem);
        }

        if (!canCraftAtLeastOne) {
            return 0;
        }

        return maxBatch;
    }

    private List<IAEItemStack> extractIngredientsForBatch(ICraftingPatternDetails details, long actualBatchSize) {
        List<IAEItemStack> extractedItems = new ArrayList<>();
        final IAEItemStack[] inputs = details.getInputs();

        for (int slotIndex = 0; slotIndex < inputs.length; slotIndex++) {
            final IAEItemStack templateInput = inputs[slotIndex];
            if (templateInput == null || templateInput.getStackSize() == 0) continue;

            long totalToExtract = Utils.multiplySafely(templateInput.getStackSize(), actualBatchSize);
            if (totalToExtract <= 0 && actualBatchSize > 0) {
                rollbackExtraction(extractedItems);
                return null;
            }
            if (totalToExtract == 0) continue;


            IAEItemStack request = templateInput.copy();
            request.setStackSize(totalToExtract);


            boolean itemExtractedForSlot;
            long remainingToExtract = totalToExtract;

            if (details.isCraftable() && details.canSubstitute()) {
                final List<IAEItemStack> substitutes = details.getSubstituteInputs(slotIndex);
                List<IAEItemStack> potentialSources = new ArrayList<>();
                potentialSources.add(templateInput);
                potentialSources.addAll(substitutes);

                for (IAEItemStack potentialSource : potentialSources) {
                    if (remainingToExtract <= 0) break;

                    IAEItemStack requestPart = potentialSource.copy();
                    requestPart.setStackSize(remainingToExtract);

                    IAEItemStack currentExtracted = this.inventory.extractItems(requestPart, Actionable.MODULATE, this.actionSource);

                    if (currentExtracted != null && currentExtracted.getStackSize() > 0) {
                        extractedItems.add(currentExtracted.copy());
                        remainingToExtract -= currentExtracted.getStackSize();
                        Utils.postChange(currentExtracted, this.actionSource, this.getListeners());
                    }
                }

            } else {
                IAEItemStack extractedBatch = this.inventory.extractItems(request, Actionable.MODULATE, this.actionSource);
                if (extractedBatch != null && extractedBatch.getStackSize() > 0) {
                    extractedItems.add(extractedBatch.copy());
                    remainingToExtract -= extractedBatch.getStackSize();
                    Utils.postChange(extractedBatch, this.actionSource, this.getListeners());
                }
            }
            itemExtractedForSlot = (remainingToExtract <= 0);


            if (!itemExtractedForSlot) {
                rollbackExtraction(extractedItems);
                return null;
            }
        }

        return extractedItems;
    }

    private void rollbackExtraction(List<IAEItemStack> extractedItems) {
        for (IAEItemStack toReturn : extractedItems) {
            IAEItemStack returned = this.inventory.injectItems(toReturn.copy(), Actionable.MODULATE, this.actionSource);
            if (!(returned != null && returned.getStackSize() > 0)) {
                Utils.postChange(toReturn, this.actionSource, this.getListeners());
            }
        }
    }

    private List<IAEItemStack> handleContainerItems(ICraftingPatternDetails details, long actualBatchSize) {
        List<IAEItemStack> containerItems = new ArrayList<>();
        final IAEItemStack[] inputs = details.getInputs();

        for (final IAEItemStack templateInput : inputs) {
            if (templateInput == null || templateInput.getStackSize() <= 0) continue;

            ItemStack inputStack = templateInput.createItemStack();
            if (inputStack.isEmpty()) continue;

            ItemStack containerStack = Platform.getContainerItem(inputStack.copy().splitStack(1));

            if (!containerStack.isEmpty()) {
                IAEItemStack aeContainerStack = AEItemStack.fromItemStack(containerStack.copy());
                if (aeContainerStack == null || aeContainerStack.getStackSize() <= 0) continue;

                long totalUnitsUsed = Utils.multiplySafely(templateInput.getStackSize(), actualBatchSize);
                if (totalUnitsUsed <=0 && actualBatchSize > 0) continue;
                if (totalUnitsUsed == 0) continue;

                long totalContainers = Utils.multiplySafely(aeContainerStack.getStackSize(), totalUnitsUsed);

                if (totalContainers > 0) {
                    IAEItemStack finalBatchContainer = aeContainerStack.copy();
                    finalBatchContainer.setStackSize(totalContainers);

                    boolean merged = false;
                    for (IAEItemStack existingContainer : containerItems) {
                        if (existingContainer.isSameType(finalBatchContainer)) {
                            existingContainer.setStackSize(existingContainer.getStackSize() + finalBatchContainer.getStackSize());
                            merged = true;
                            break;
                        }
                    }
                    if (!merged) {
                        containerItems.add(finalBatchContainer);
                    }
                }
            }
        }
        return containerItems;
    }

    public IAEItemStack getItemStack(final IAEItemStack what, final CraftingItemList storage2) {
        IAEItemStack is;

        switch (storage2) {
            case STORAGE:
                is = this.inventory.getItemList().findPrecise(what);
                break;
            case ACTIVE:
                is = this.waitingFor.findPrecise(what);
                break;
            case PENDING:

                is = what.copy();
                is.setStackSize(0);

                for (final Map.Entry<ICraftingPatternDetails, TaskProgress> t : this.tasks.entrySet()) {
                    for (final IAEItemStack ais : t.getKey().getCondensedOutputs()) {
                        if (ais.isSameType(is)) {
                            is.setStackSize(is.getStackSize() + ais.getStackSize() * t.getValue().value);
                        }
                    }
                }

                break;
            default:
            case ALL:
                throw new IllegalStateException("Invalid Operation!!1");
        }

        if (is != null) {
            return is.copy();
        }

        is = what.copy();
        is.setStackSize(0);
        return is;
    }

    @Override
    public long crazyae$whenJobStarted() {
        return this.millisWhenJobStarted;
    }

    @Override
    public void crazyae$setWhenJobStarted(long when) {
        this.millisWhenJobStarted = when;
    }

    @Override
    public String crazyae$jobInitiator() {
        return this.jobInitiator;
    }

    @Override
    public void crazyae$setJobInitiator(String player) {
        this.jobInitiator = player;
    }

    @Override
    public CrazyCraftingLink getLastLink() {
        return (CrazyCraftingLink) this.myLastLink;
    }

    @Override
    public IGridNode getNode() {
        return this.getProxy().getNode();
    }

    @Override
    public void jobStateChange(ICraftingLink link) {

    }

    @Override
    public IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack aeStack, Actionable mode) {
        if (aeStack == null || aeStack.getStackSize() <= 0) {
            return null;
        }

        if (mode == Actionable.SIMULATE) {
            try {
                IGrid grid = this.getProxy().getGrid();
                if (grid == null) return aeStack.copy();

                IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                IMEInventory<IAEItemStack> networkInv = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                return networkInv.injectItems(aeStack.copy(), Actionable.SIMULATE, this.getActionSource());
            } catch (GridAccessException e) {
                return aeStack.copy();
            }
        }

        IAEItemStack remainder = aeStack.copy();

        if (this.getProxy().isActive()) {
            try {
                IGrid grid = this.getProxy().getGrid();
                if (grid != null) {
                    IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
                    IMEInventory<IAEItemStack> networkInv = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                    remainder = networkInv.injectItems(aeStack.copy(), Actionable.MODULATE, this.getActionSource());

                    if (remainder == null || remainder.getStackSize() <= 0) {
                        this.markDirty();
                        return null;
                    } else {
                        long injectedAmount = aeStack.getStackSize() - remainder.getStackSize();
                        if (injectedAmount > 0) {
                            this.markDirty();
                        }
                    }
                }
            } catch (GridAccessException ignored) {}
        }

        return remainder;
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final IActionSource src) {
        if (input == null || input.getStackSize() <= 0) return input;

        if (this.isComplete) {
            if (this.finalOutput == null || !this.finalOutput.isSameType(input)) {
                if (type == Actionable.MODULATE) {
                    IAEItemStack remainder = injectToMe(input.copy(), Actionable.MODULATE, src != null ? src : this.actionSource);
                    if (remainder != null && remainder.getStackSize() > 0) {
                        handleRemainder(remainder.copy());
                    }
                    return null;
                } else {
                    return injectToMe(input.copy(), Actionable.SIMULATE, src != null ? src : this.actionSource);
                }
            }
        }


        if (type == Actionable.SIMULATE) {
            IAEItemStack remaining = input.copy();
            for (IAEItemStack wfStack : this.waitingFor) {
                if (wfStack != null && wfStack.getStackSize() > 0 && wfStack.isSameType(remaining)) {
                    long canTake = Math.min(remaining.getStackSize(), wfStack.getStackSize());
                    remaining.decStackSize(canTake);
                    if (remaining.getStackSize() <= 0) return null;
                }
            }
            return remaining;
        }

        IAEItemStack currentInput = input.copy();
        IAEItemStack unfulfilledByWaiting;
        IAEItemStack remainderAfterProcessing = null;

        boolean waitingForChanged = false;
        IItemList<IAEItemStack> nextWaitingForList = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();

        for (IAEItemStack waitingStack : this.waitingFor) {
            IAEItemStack stackToKeepInNext = waitingStack.copy();

            if (currentInput.getStackSize() > 0 && waitingStack.getStackSize() > 0 && waitingStack.isSameType(currentInput)) {
                this.waiting = false;
                long canTakeFromWaiting = Math.min(currentInput.getStackSize(), waitingStack.getStackSize());

                IAEItemStack takenFromWaiting = waitingStack.copy();
                takenFromWaiting.setStackSize(canTakeFromWaiting);

                stackToKeepInNext.decStackSize(canTakeFromWaiting);
                currentInput.decStackSize(canTakeFromWaiting);
                waitingForChanged = true;

                this.postCraftingStatusChange(takenFromWaiting.copy().setStackSize(-takenFromWaiting.getStackSize()));

                IAEItemStack remainderFromProcessing;
                if (this.finalOutput != null && this.finalOutput.isSameType(takenFromWaiting)) {
                    remainderFromProcessing = processFinalOutput(takenFromWaiting.copy(), Actionable.MODULATE);
                } else {
                    remainderFromProcessing = this.inventory.injectItems(takenFromWaiting.copy(), Actionable.MODULATE, src);
                    if (remainderFromProcessing == null || remainderFromProcessing.getStackSize() <= 0) {
                        Utils.postChange(takenFromWaiting, src, this.getListeners());
                    } else {
                        long injectedAmt = takenFromWaiting.getStackSize() - remainderFromProcessing.getStackSize();
                        if (injectedAmt > 0)
                            Utils.postChange(takenFromWaiting.copy().setStackSize(injectedAmt), src, this.getListeners());
                    }
                }

                if (remainderFromProcessing != null && remainderFromProcessing.getStackSize() > 0) {
                    if (remainderAfterProcessing == null)
                        remainderAfterProcessing = remainderFromProcessing.copy();
                    else
                        remainderAfterProcessing.add(remainderFromProcessing);
                }
            }

            if (stackToKeepInNext.getStackSize() > 0) {
                nextWaitingForList.add(stackToKeepInNext);
            } else {
                if (waitingStack.isSameType(currentInput) || (input.isSameType(waitingStack) && waitingStack.getStackSize() > 0))
                    waitingForChanged = true;
            }
        }

        if (waitingForChanged) {
            this.waitingFor = nextWaitingForList;
            this.markDirty();
        }

        if (currentInput.getStackSize() > 0) {
            unfulfilledByWaiting = currentInput.copy();

            IAEItemStack remainderFromProcessingUnfulfilled;
            if (this.finalOutput != null && this.finalOutput.isSameType(unfulfilledByWaiting)) {
                remainderFromProcessingUnfulfilled = processFinalOutput(unfulfilledByWaiting.copy(), Actionable.MODULATE);
            } else {
                remainderFromProcessingUnfulfilled = this.inventory.injectItems(unfulfilledByWaiting.copy(), Actionable.MODULATE, src);
                if (remainderFromProcessingUnfulfilled == null || remainderFromProcessingUnfulfilled.getStackSize() <= 0) {
                    Utils.postChange(unfulfilledByWaiting, src, this.getListeners());
                } else {
                    long injectedAmt = unfulfilledByWaiting.getStackSize() - remainderFromProcessingUnfulfilled.getStackSize();
                    if (injectedAmt > 0)
                        Utils.postChange(unfulfilledByWaiting.copy().setStackSize(injectedAmt), src, this.getListeners());
                }
            }

            if (remainderFromProcessingUnfulfilled != null && remainderFromProcessingUnfulfilled.getStackSize() > 0) {
                if (remainderAfterProcessing == null)
                    remainderAfterProcessing = remainderFromProcessingUnfulfilled.copy();
                else
                    remainderAfterProcessing.add(remainderFromProcessingUnfulfilled);
            }
        }

        if (remainderAfterProcessing == null || remainderAfterProcessing.getStackSize() <= 0) {
            this.checkAndCompleteJob();
        }

        if (remainderAfterProcessing != null && remainderAfterProcessing.getStackSize() > 0) {
            boolean alreadyInItemsToSend = false;
            for (IAEItemStack its : this.itemsToSend) {
                if (its.isSameType(remainderAfterProcessing)) {
                    alreadyInItemsToSend = true;
                    break;
                }
            }
            if (alreadyInItemsToSend) {
                this.checkAndCompleteJob();
                return null;
            }
        }


        return remainderAfterProcessing;
    }

    @Override
    protected void updateCrafting() {
        IAEItemStack sendToGui = null;
        if (this.finalOutput != null && this.finalOutput.getStackSize() > 0) {
            sendToGui = this.finalOutput.copy();
        }

        for (final TileCraftingMonitorTile t : this.status) {
            t.setJob(sendToGui);
        }
    }

    private IAEItemStack processFinalOutput(IAEItemStack item, Actionable type) {
        IAEItemStack remainingAfterLink = item.copy();

        if (this.myLastLink != null) {
            remainingAfterLink = ((CrazyCraftingLink) this.myLastLink).injectItems(item.copy(), type);
        }

        if (remainingAfterLink != null && remainingAfterLink.getStackSize() > 0) {
            boolean merged = false;
            for (IAEItemStack stackInSend : this.itemsToSend) {
                if(stackInSend.isSameType(remainingAfterLink)){
                    stackInSend.add(remainingAfterLink);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                this.itemsToSend.add(remainingAfterLink.copy());
            }

            this.markDirty();
        }

        this.updateCrafting();
        return null;
    }

    private void submitLink(final ICraftingLink myLastLink2) {
        try {
            if (this.getProxy().getGrid() != null) {
                final CrazyAutocraftingSystem cc = this.getProxy().getGrid().getCache(ICrazyAutocraftingSystem.class);
                cc.addLink((CrazyCraftingLink) myLastLink2);
            }
        } catch (GridAccessException ignored) {}
    }

    @Override
    public IItemHandler getAcceleratorsInv() {
        return this.getInventoryByName("accelerators");
    }

    @Override
    public IItemHandler getStoragesInv() {
        return this.getInventoryByName("storages");
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        final boolean oldPower = this.isPowered;
        this.isPowered = data.readBoolean();
        return this.isPowered != oldPower || c;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeBoolean(this.isPowered);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);

        this.patternsInv.writeToNBT(data, "patterns");
        this.accelsInv.writeToNBT(data, "accels");
        this.storagesInv.writeToNBT(data, "storages");
        this.settings.writeToNBT(data);
        data.setInteger("priority", this.priority);
        data.setLong("initialTotalExpectedItems", this.initialTotalExpectedItems);

        NBTTagList tasksList = new NBTTagList();
        for (Map.Entry<ICraftingPatternDetails, TaskProgress> entry : this.tasks.entrySet()) {
            ICraftingPatternDetails details = entry.getKey();
            TaskProgress task = entry.getValue();
            if (task.value > 0) {
                NBTTagCompound tag = new NBTTagCompound();
                ItemStack patternStack = details.getPattern();
                if (!patternStack.isEmpty()) {
                    tag.setTag("pattern", patternStack.writeToNBT(new NBTTagCompound()));
                    tag.setLong("value", task.value);
                    tasksList.appendTag(tag);
                }
            }
        }
        data.setTag("craftingTasks", tasksList);

        NBTTagList pendingInterfacesNBT = new NBTTagList();
        for (Map.Entry<ICraftingPatternDetails, PendingInterfaceTask> entry : this.pendingInterfaceTasks.entrySet()) {
            ICraftingPatternDetails patternDetailsAsKey = entry.getKey();
            PendingInterfaceTask task = entry.getValue();

            if (task.pendingBatches > 0 && patternDetailsAsKey != null) {
                NBTTagCompound tag = new NBTTagCompound();
                ItemStack patternStackFromDetails = patternDetailsAsKey.getPattern();
                if (!patternStackFromDetails.isEmpty()) {
                    tag.setTag("patternStack", patternStackFromDetails.writeToNBT(new NBTTagCompound()));
                    tag.setLong("pending", task.pendingBatches);
                    pendingInterfacesNBT.appendTag(tag);
                }
            }
        }
        data.setTag("pendingCPUInterfaces", pendingInterfacesNBT);

        NBTTagList inventoryList = new NBTTagList();
        for (IAEItemStack stack : this.inventory.getItemList()) {
            if (stack != null && stack.getStackSize() > 0) {
                NBTTagCompound c = new NBTTagCompound();
                stack.writeToNBT(c);
                inventoryList.appendTag(c);
            }
        }
        data.setTag("craftingInventory", inventoryList);

        NBTTagList waitingForList = new NBTTagList();
        for (IAEItemStack stack : this.waitingFor) {
            if (stack != null && stack.getStackSize() > 0) {
                NBTTagCompound c = new NBTTagCompound();
                stack.writeToNBT(c);
                waitingForList.appendTag(c);
            }
        }
        data.setTag("waitingFor", waitingForList);

        NBTTagList itemsToSendList = new NBTTagList();
        for (IAEItemStack i : this.itemsToSend) {
            if (i != null && i.getStackSize() > 0) {
                NBTTagCompound c = new NBTTagCompound();
                i.writeToNBT(c);
                itemsToSendList.appendTag(c);
            }
        }
        data.setTag("itemsToSend", itemsToSendList);


        if (this.finalOutput != null && this.finalOutput.getStackSize() > 0) {
            NBTTagCompound c = new NBTTagCompound();
            this.finalOutput.writeToNBT(c);
            data.setTag("finalOutput", c);
        }

        if (this.myLastLink != null) {
            final NBTTagCompound linkNBT = new NBTTagCompound();
            this.myLastLink.writeToNBT(linkNBT);
            data.setTag("myLastLinkData", linkNBT);
        }

        data.setBoolean("isComplete", this.isComplete);
        data.setLong("initialTotalItems", this.initialTotalItems);

        if (this.requestingPlayerUUID != null) {
            data.setTag("requestingPlayerUUID", NBTUtil.createUUIDTag(this.requestingPlayerUUID));
        }

        data.setString("cpuName", this.myOwnName);
        data.setLong("elapsedTime", this.elapsedTime);

        data.setString("jobInitiator", this.jobInitiator);
        data.setLong("millisJobStarted", this.millisWhenJobStarted);


        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        this.patternsInv.readFromNBT(data, "patterns");
        this.accelsInv.readFromNBT(data, "accels");
        this.storagesInv.readFromNBT(data, "storages");
        this.settings.readFromNBT(data);
        this.priority = data.getInteger("priority");
        this.initialTotalExpectedItems = data.getLong("initialTotalExpectedItems");

        this.tasks.clear();
        if (data.hasKey("craftingTasks", Constants.NBT.TAG_LIST)) {
            NBTTagList tasksList = data.getTagList("craftingTasks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < tasksList.tagCount(); i++) {
                NBTTagCompound tag = tasksList.getCompoundTagAt(i);
                ItemStack patternStack = new ItemStack(tag.getCompoundTag("pattern"));
                long taskValue = tag.getLong("value");

                if (!patternStack.isEmpty() && patternStack.getItem() instanceof ICraftingPatternItem cpi && taskValue > 0) {
                    ICraftingPatternDetails details = cpi.getPatternForItem(patternStack, this.getWorld());
                    if (details != null) {
                        TaskProgress progress = new TaskProgress();
                        progress.value = taskValue;
                        this.tasks.put(details, progress);
                    }
                }
            }
        }

        this.pendingInterfaceTasks.clear();
        if (data.hasKey("pendingCPUInterfaces", Constants.NBT.TAG_LIST)) {
            NBTTagList pendingList = data.getTagList("pendingCPUInterfaces", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < pendingList.tagCount(); i++) {
                NBTTagCompound tag = pendingList.getCompoundTagAt(i);
                ItemStack patternStack = new ItemStack(tag.getCompoundTag("patternStack"));
                long pendingBatches = tag.getLong("pending");

                if (!patternStack.isEmpty() && patternStack.getItem() instanceof ICraftingPatternItem cpi && pendingBatches > 0) {
                    ICraftingPatternDetails details = cpi.getPatternForItem(patternStack, this.getWorld());
                    if (details != null) {
                        this.pendingInterfaceTasks.put(details, new PendingInterfaceTask(details, pendingBatches));
                    }
                }
            }
        }


        this.inventory = new CrazyCraftingInventory();
        if (data.hasKey("craftingInventory", Constants.NBT.TAG_LIST)) {
            NBTTagList inventoryList = data.getTagList("craftingInventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inventoryList.tagCount(); ++i) {
                NBTTagCompound itemTags = inventoryList.getCompoundTagAt(i);
                IAEItemStack stack = AEItemStack.fromNBT(itemTags);
                if (stack != null && stack.getStackSize() > 0) {
                    this.inventory.injectItems(stack, Actionable.MODULATE, null);
                }
            }
        }

        this.waitingFor = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        if (data.hasKey("waitingFor", Constants.NBT.TAG_LIST)) {
            NBTTagList waitingForList = data.getTagList("waitingFor", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < waitingForList.tagCount(); ++i) {
                NBTTagCompound itemTags = waitingForList.getCompoundTagAt(i);
                IAEItemStack stack = AEItemStack.fromNBT(itemTags);
                if (stack != null && stack.getStackSize() > 0) {
                    this.waitingFor.add(stack);
                }
            }
        }

        this.itemsToSend.clear();
        if (data.hasKey("itemsToSend", Constants.NBT.TAG_LIST)) {
            NBTTagList tagList = data.getTagList("itemsToSend", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                IAEItemStack stack = AEItemStack.fromNBT(itemTags);
                if (stack != null && stack.getStackSize() > 0) {
                    this.itemsToSend.add(stack);
                }
            }
        }

        if (data.hasKey("finalOutput", Constants.NBT.TAG_COMPOUND)) {
            this.finalOutput = AEItemStack.fromNBT(data.getCompoundTag("finalOutput"));
            if (this.finalOutput != null && this.finalOutput.getStackSize() <= 0) {
                this.finalOutput = null;
            }
        } else {
            this.finalOutput = null;
        }

        if (data.hasKey("myLastLinkData", Constants.NBT.TAG_STRING)) {
            this.myLastLink = new CrazyCraftingLink(data, this);
        } else {
            this.myLastLink = null;
        }


        this.isComplete = data.getBoolean("isComplete");
        this.initialTotalItems = data.getLong("initialTotalItems");

        if (data.hasKey("requestingPlayerUUID")) {
            this.requestingPlayerUUID = NBTUtil.getUUIDFromTag(data.getCompoundTag("requestingPlayerUUID"));
        } else {
            this.requestingPlayerUUID = null;
        }

        if (data.hasKey("cpuName", Constants.NBT.TAG_STRING)) {
            this.myOwnName = data.getString("cpuName");
        } else {
            this.myOwnName = "";
        }

        this.elapsedTime = data.getLong("elapsedTime");

        this.jobInitiator = data.getString("jobInitiator");
        this.millisWhenJobStarted = data.getLong("millisJobStarted");
    }

    @Override
    public @NotNull AECableType getCableConnectionType(final @NotNull AEPartLocation dir) {
        return AECableType.COVERED;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "patterns" -> this.patternsInv;
            case "accelerators" -> this.accelsInv;
            case "storages" -> this.storagesInv;
            default -> null;
        };
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {}

    @Override
    public @NotNull IItemHandler getInternalInventory() {
        return this.patternsInv;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (inv == this.patternsInv && (!removed.isEmpty() || !added.isEmpty())) {
            if (this.getProxy().isActive()) {
                this.cached = false;
            }
        }

        if ((inv == this.accelsInv || inv == this.storagesInv) && (!removed.isEmpty() || !added.isEmpty())) {
            this.cached = false;
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (Platform.isServer()) {
            this.updateCraftingList();
            this.notifyPatternsChanged();
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        if (Platform.isServer()) {
            this.notifyReady();
            this.updateCraftingList();
            this.notifyPatternsChanged();

            if (this.isBusy() && !this.isComplete) {
                this.lastTime = System.nanoTime();
            }
        }
    }

    private void notifyReady() {
        try {
            this.getProxy().getGrid().postEvent(new MECraftHostStateUpdateEv(this.getNode()));
        } catch (GridAccessException ignored) {}
    }

    public boolean acceptPatternFromTerm(ItemStack pattern) {
        for (int i = 0; i < this.patternsInv.getSlots(); i++) {
            ItemStack is = this.patternsInv.getStackInSlot(i);
            if (is.isEmpty()) {
                this.patternsInv.setStackInSlot(i, pattern.copy());
                return true;
            }
        }

        return false;
    }

    private void pushItemsOut() {
        if (!this.getProxy().isActive() || this.itemsToSend.isEmpty()) {
            return;
        }

        try {
            IGrid grid = this.getProxy().getGrid();
            if (grid == null) return;

            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            IMEInventory<IAEItemStack> storage = storageGrid.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            List<IAEItemStack> toProcess = new ArrayList<>(this.itemsToSend);
            List<IAEItemStack> successfullySent = new ArrayList<>();
            Map<IAEItemStack, Long> toUpdateInOriginal = new HashMap<>();

            for (IAEItemStack itemToSend : toProcess) {
                if (itemToSend == null || itemToSend.getStackSize() <= 0) {
                    successfullySent.add(itemToSend);
                    continue;
                }

                IAEItemStack stackToInject = itemToSend.copy();
                IAEItemStack overflow = storage.injectItems(stackToInject, Actionable.MODULATE, this.actionSource);

                long injectedAmount = stackToInject.getStackSize() - (overflow != null ? overflow.getStackSize() : 0);

                if (injectedAmount > 0) {
                    if (overflow == null || overflow.getStackSize() <= 0) {
                        successfullySent.add(itemToSend);
                    } else {
                        toUpdateInOriginal.put(itemToSend, overflow.getStackSize());
                    }
                }
            }

            boolean changed = false;
            if (!successfullySent.isEmpty()) {
                this.itemsToSend.removeAll(successfullySent);
                changed = true;
            }
            for (Map.Entry<IAEItemStack, Long> entry : toUpdateInOriginal.entrySet()) {
                IAEItemStack originalItem = entry.getKey();
                long newSize = entry.getValue();
                int index = this.itemsToSend.indexOf(originalItem);
                if (index != -1) {
                    this.itemsToSend.get(index).setStackSize(newSize);
                    changed = true;
                }
            }

            if (!successfullySent.isEmpty() || !toUpdateInOriginal.isEmpty()) {
                List<IAEItemStack> nextItemsToSend = new ArrayList<>();
                for (IAEItemStack currentInList : this.itemsToSend) {
                    if (successfullySent.contains(currentInList)) {
                        continue;
                    }
                    Long updatedSize = toUpdateInOriginal.get(currentInList);
                    if (updatedSize != null) {
                        currentInList.setStackSize(updatedSize);
                        if (currentInList.getStackSize() > 0) {
                            nextItemsToSend.add(currentInList);
                        }
                        changed = true;
                    } else {
                        nextItemsToSend.add(currentInList);
                    }
                }
                this.itemsToSend.clear();
                this.itemsToSend.addAll(nextItemsToSend);
            }


            if (changed) {
                this.markDirty();
                this.checkAndCompleteJob();
            }

        } catch (GridAccessException ignored) {}
    }

    private void updateCraftingList() {
        final Boolean[] accountedFor = new Boolean[this.patternsInv.getSlots()];
        Arrays.fill(accountedFor, false);

        if (this.craftingList != null) {
            final Iterator<ICraftingPatternDetails> i = this.craftingList.iterator();
            while (i.hasNext()) {
                final ICraftingPatternDetails details = i.next();
                boolean found = false;

                for (int x = 0; x < accountedFor.length; x++) {
                    final ItemStack is = this.patternsInv.getStackInSlot(x);
                    if (details.getPattern() == is) {
                        accountedFor[x] = found = true;
                    }
                }

                if (!found) {
                    i.remove();
                }
            }
        }

        for (int x = 0; x < accountedFor.length; x++) {
            if (!accountedFor[x]) {
                this.addToCraftingList(this.patternsInv.getStackInSlot(x));
            }
        }
    }

    private void addToCraftingList(final ItemStack is) {
        if (is.isEmpty()) {
            return;
        }

        if (is.getItem() instanceof final ICraftingPatternItem cpi) {
            final ICraftingPatternDetails details = cpi.getPatternForItem(is, this.getWorld());

            if (details != null) {
                if (this.craftingList == null) {
                    this.craftingList = new HashSet<>();
                }

                this.craftingList.add(details);
            }
        }
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        for (final ItemStack is : this.patternsInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.accelsInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.storagesInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final IAEItemStack is : this.itemsToSend) {
            drops.add(is.createItemStack());
        }
    }

    private void notifyPatternsChanged() {
        try {
            if (this.getProxy().isActive()) {
                IGrid grid = this.getProxy().getGrid();
                if (grid != null) {
                    grid.postEvent(new MECraftHostPatternsChangedEv(this, this.getProxy().getNode()));
                    this.getProxy().getTick().alertDevice(this.getProxy().getNode());
                    this.cached = true;
                } else {
                    this.cached = false;
                }
            } else {
                this.cached = false;
            }
        } catch (GridAccessException e) {
            this.cached = false;
        }
    }

    private List<IAEItemStack> dispatchBatchJobInternal(ICraftingPatternDetails details, long batchSize) {
        List<IAEItemStack> producedOutputs = new ArrayList<>();
        if (details != null && batchSize > 0) {
            for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
                IAEItemStack totalOutput = outputTemplate.copy();
                long newAmount = Utils.multiplySafely(outputTemplate.getStackSize(), batchSize);

                if (newAmount > 0) {
                    totalOutput.setStackSize(newAmount);
                    producedOutputs.add(totalOutput);

                }
            }
        }
        return producedOutputs;
    }

    private long countAvailableItems(IAEItemStackMatcher matcher) {
        long totalAvailable = 0;
        IItemList<IAEItemStack> availableItems = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        this.inventory.getAvailableItems(availableItems);

        ICraftingPatternDetails details = matcher.details;
        IAEItemStack template = matcher.template;

        if (details.isCraftable() && details.canSubstitute() && matcher.useSubstitutes()) {
            int slotIndex = Utils.findSlotIndex(details, template);
            if (slotIndex != -1) {
                List<IAEItemStack> substitutes = details.getSubstituteInputs(slotIndex);
                List<IAEItemStack> potentialSources = new ArrayList<>();
                potentialSources.add(template);
                potentialSources.addAll(substitutes);

                for (IAEItemStack source : potentialSources) {
                    for (IAEItemStack stackInInv : availableItems.findFuzzy(source, FuzzyMode.IGNORE_ALL)) {
                        totalAvailable += stackInInv.getStackSize();
                    }
                }

            } else {
                IAEItemStack found = availableItems.findPrecise(template);
                if (found != null) {
                    totalAvailable = found.getStackSize();
                } else if (template.getDefinition().isItemStackDamageable() || Platform.isGTDamageableItem(template.getItem())) {
                    for (IAEItemStack fuzzyMatch : availableItems.findFuzzy(template, FuzzyMode.IGNORE_ALL)){
                        totalAvailable += fuzzyMatch.getStackSize();
                    }
                }
            }

        } else {
            IAEItemStack found = availableItems.findPrecise(template);
            if (found != null) {
                totalAvailable = found.getStackSize();
            } else if (template.getDefinition().isItemStackDamageable() || Platform.isGTDamageableItem(template.getItem())) {
                for (IAEItemStack fuzzyMatch : availableItems.findFuzzy(template, FuzzyMode.IGNORE_ALL)){
                    totalAvailable += fuzzyMatch.getStackSize();
                }
            }
        }
        return totalAvailable;
    }

    @MENetworkEventSubscribe
    public void onPowerEvent(final MENetworkPowerStatusChange p) {
        this.updatePowerState();
        if (Platform.isServer()) {
            this.updateCraftingList();
            this.notifyPatternsChanged();
            if (!this.itemsToSend.isEmpty()) {
                this.pushItemsOut();
            }
        }
    }

    private void updatePowerState() {
        boolean newState = false;

        try {
            newState = this.getProxy().isActive() && this.getProxy().getEnergy().extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.0001;
        } catch (final GridAccessException ignored) {

        }

        if (newState != this.isPowered) {
            this.isPowered = newState;
            this.markForUpdate();
        }
    }

    @Override
    public void provideCrafting(final ICrazyCraftingProviderHelper craftingTracker) {
        if (this.getProxy().getNode().isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                details.setPriority(this.priority);
                craftingTracker.addCraftingOption(this, details);
            }
        }
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        if (input instanceof IAEItemStack) {
            final IAEItemStack is = this.waitingFor.findPrecise(input);
            return is != null && is.getStackSize() > 0;
        }
        return false;
    }

    @Override
    public long getSortValue() {
        return (long)this.getPos().getZ() << 24 ^ (long)this.getPos().getX() << 8 ^ this.getPos().getY();
    }

    @Override
    public BlockPos getTEPos() {
        return this.getPos();
    }

    @Override
    public int getDim() {
        return this.getWorld().provider.getDimension();
    }

    @Override
    public IItemHandler getPatternsInv() {
        return this.getInternalInventory();
    }

    @Override
    public String getName() {
        return (this.hasCustomInventoryName() ? this.getCustomInventoryName() : CrazyAEGuiText.QUANTUM_CPU.getLocal() + this.myOwnName);
    }

    @Override
    public void setCpuName(String name) {
        this.myOwnName = name;
    }

    @Override
    public long getAcceleratorCount() {
        if (this.accelsCount != -1) return this.accelsCount;

        long ret = 0;

        for (ItemStack is : this.accelsInv) {
            if (is != null) {
                for (int i = 0; i < is.getCount(); i++) {
                    ret += Utils.getAcceleratorsCountOf(is);
                }
            }
        }

        this.accelsCount = ret;
        return this.accelsCount;
    }

    @Override
    public double getStorageCount() {
        if (this.storageCount != -1) return this.storageCount;
        double ret = 0;

        for (ItemStack is : this.storagesInv) {
            if (is != null) {
                for (int i = 0; i < is.getCount(); i++) {
                    ret += Utils.getStorageCountOf(is);
                }
            }
        }

        this.storageCount = ret;
        return this.storageCount;
    }

    @Override
    public void addListener(IMEMonitorHandlerReceiver<IAEItemStack> imeMonitorHandlerReceiver, Object o) {
        this.listeners.put(imeMonitorHandlerReceiver, o);
    }

    @Override
    public void removeListener(IMEMonitorHandlerReceiver<IAEItemStack> imeMonitorHandlerReceiver) {
        this.listeners.remove(imeMonitorHandlerReceiver);
    }

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode iGridNode) {
        return new TickingRequest(1,1,false,false);
    }

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode iGridNode, int i) {
        if (!this.cached) {
            this.accelsCount = -1;
            this.storageCount = -1;
            if (this.getProxy().isActive()) {
                this.updateCraftingList();
                this.notifyPatternsChanged();
                this.notifyReady();
            }
        }

        return TickRateModulation.URGENT;
    }

    private static class PendingInterfaceTask {
        final ICraftingPatternDetails details;
        long pendingBatches;

        PendingInterfaceTask(ICraftingPatternDetails details, long initialBatches) {
            this.details = details;
            this.pendingBatches = initialBatches;
        }
    }

    private static class IAEItemStackMatcher {
        private final IAEItemStack template;
        private final ICraftingPatternDetails details;
        private final int slotIndex;
        private final boolean useSubstitutes;

        private IAEItemStackMatcher(IAEItemStack template, ICraftingPatternDetails details, int slotIndex, boolean useSubstitutes) {
            this.template = template;
            this.details = details;
            this.slotIndex = slotIndex;
            this.useSubstitutes = useSubstitutes;
        }

        public static IAEItemStackMatcher create(IAEItemStack template, ICraftingPatternDetails details) {
            int slot = -1;
            IAEItemStack[] inputs = details.getInputs();
            for (int i=0; i<inputs.length; ++i) {
                if (inputs[i] != null && inputs[i].isSameType(template)) {
                    slot = i;
                    break;
                }
            }

            boolean useEffectiveSubstitutes = details.isCraftable() && details.canSubstitute();
            return new IAEItemStackMatcher(template, details, slot, useEffectiveSubstitutes);
        }

        public boolean useSubstitutes() {
            return useSubstitutes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IAEItemStackMatcher that = (IAEItemStackMatcher) o;

            if (this.useSubstitutes && that.useSubstitutes && this.details == that.details && this.slotIndex != -1 && this.slotIndex == that.slotIndex) {
                return true;
            }

            return template.isSameType(that.template);
        }

        @Override
        public int hashCode() {
            if (useSubstitutes && slotIndex != -1) {
                return Objects.hash(details, slotIndex);
            }
            return Objects.hash(template.getItem(), template.getItemDamage());
        }
    }
}