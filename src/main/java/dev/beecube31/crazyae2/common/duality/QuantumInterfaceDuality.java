package dev.beecube31.crazyae2.common.duality;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.IStorageMonitorableAccessor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;
import appeng.capabilities.Capabilities;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.NonBlockingItems;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MachineSource;
import appeng.parts.misc.PartInterface;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.networking.TileCableBus;
import appeng.util.ConfigManager;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.BlockingInventoryAdaptor;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import de.ellpeck.actuallyadditions.api.tile.IPhantomTile;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftCallback;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftingProviderHelper;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyInterfaceHost;
import dev.beecube31.crazyae2.common.interfaces.mixin.inv.IMixinAdaptorItemHandler;
import dev.beecube31.crazyae2.common.networking.events.MEInterfaceHostStateUpdateEv;
import dev.beecube31.crazyae2.common.util.Longium;
import dev.beecube31.crazyae2.common.util.ModsChecker;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.config.CrazyAEAutoCraftingSystemConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class QuantumInterfaceDuality extends DualityInterface implements ICrazyInterfaceHost {
    public static final int NUMBER_OF_PATTERN_SLOTS = 72;
    public static final int MAX_PATTERN_EXECUTIONS_TO_PUSH_PER_AC_TICK = CrazyAEAutoCraftingSystemConfig.maxPatternPushExecutionsPerActiveCraftTick;

    private final IAEItemStack[] requireWork;

    private final List<QuantumInterfaceDuality.PendingCraft> pendingCrafts = new ArrayList<>();
    private final Map<String, QuantumInterfaceDuality.ActiveInterfaceCraft> activeCrafts = new HashMap<>();

    private final AENetworkProxy proxy;
    protected final IInterfaceHost iHost;
    private List<ICraftingPatternDetails> craftingList = null;
    private final ConfigManager cm;
    private EnumMap<EnumFacing, List<IAEItemStack>> waitingToSendFacing = new EnumMap<>(EnumFacing.class);
    private final Map<String, List<String>> pendingToActiveCraftKeysMap = new HashMap<>();
    private final IActionSource mySource;

    private static Method cachedSameGridMethod;
    private static Class<?> cachedSameGridMethodClass;


    public QuantumInterfaceDuality(AENetworkProxy networkProxy, IInterfaceHost ih) {
        super(networkProxy, ih);
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalInventory(this, 72, 1),
                "patterns"
        );

        this.proxy = networkProxy;
        this.iHost = ih;

        this.craftingList = ObfuscationReflectionHelper.getPrivateValue(
                DualityInterface.class,
                this,
                "craftingList"
        );

        this.cm = ObfuscationReflectionHelper.getPrivateValue(
                DualityInterface.class,
                this,
                "cm"
        );

        this.requireWork = ObfuscationReflectionHelper.getPrivateValue(
                DualityInterface.class,
                this,
                "requireWork"
        );

        this.mySource = new MachineSource(this.iHost);
    }

    @Override
    public boolean isBusy() {
        if (this.activeCrafts.size() >= CrazyAEAutoCraftingSystemConfig.maxCraftingTasksSizePerQuantumInterface) {
            return true;
        }

        return this.waitingToSendFacing != null && !this.waitingToSendFacing.isEmpty();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (inv == this.getInventoryByName("patterns")) {
            if (!removed.isEmpty() || !added.isEmpty()) {
                this.updateCraftingList();
                this.iHost.saveChanges();
            }
            return;
        }

        if (inv == this.getInventoryByName("storage")) {
            IItemHandler storageInv = this.getInventoryByName("storage");
            ItemStack stackInSlot = storageInv.getStackInSlot(slot);

            if (stackInSlot.isEmpty()) {
                if (this.requireWork[slot] != null) {
                    this.requireWork[slot] = null;
                }
                return;
            }

            IAEItemStack aeStack = AEItemStack.fromItemStack(stackInSlot);
            if (aeStack == null || aeStack.getStackSize() <= 0) return;

            long totalConsumedFromSlot = 0;
            boolean changed = false;

            List<String> activeCraftKeys = new ArrayList<>();
            List<QuantumInterfaceDuality.PendingCraft> pendingCraftList = new ArrayList<>();

            for (Map.Entry<String, QuantumInterfaceDuality.ActiveInterfaceCraft> entry : this.activeCrafts.entrySet()) {
                if (aeStack.getStackSize() <= 0) break;

                String activeCraftKey = entry.getKey();
                QuantumInterfaceDuality.ActiveInterfaceCraft activeCraft = entry.getValue();

                if (activeCraft == null) continue;

                if (activeCraft.pendingCraft == null) {
                    if (!activeCraftKeys.contains(activeCraftKey)) activeCraftKeys.add(activeCraftKey);
                    changed = true;
                    continue;
                }
                QuantumInterfaceDuality.PendingCraft currentPendingCraft = activeCraft.pendingCraft;

                if (activeCraft.currentMicroBatchSize == 0) {
                    continue;
                }

                if (activeCraft.currentMicroBatchTotalOutputsExpected.isEmpty() &&
                        currentPendingCraft.patternDetails != null &&
                        currentPendingCraft.patternDetails.getCondensedOutputs().length == 0) {
                    continue;
                }

                if (!currentPendingCraft.resolveEnvironment(iHost.getTileEntity().getWorld())) {
                    continue;
                }
                ICraftingPatternDetails details = currentPendingCraft.patternDetails;
                if (details == null) {
                    continue;
                }

                Longium consumedNowFromLogicStack = new Longium();
                boolean microBatchCompletedNow = activeCraft.recordReceivedAndComplete(aeStack, consumedNowFromLogicStack);

                if (consumedNowFromLogicStack.get() > 0) {
                    long actualTakenFromSharedLogicStack = Math.min(aeStack.getStackSize(), consumedNowFromLogicStack.get());
                    if (actualTakenFromSharedLogicStack > 0) {
                        aeStack.decStackSize(actualTakenFromSharedLogicStack);
                        totalConsumedFromSlot += actualTakenFromSharedLogicStack;
                    }
                }

                if (microBatchCompletedNow) {
                    changed = true;
                    long completedPatternExecs = activeCraft.finalizeMicrobatch();

                    if (completedPatternExecs <= 0 && activeCraft.currentMicroBatchSize > 0) {
                        continue;
                    }
                    if (completedPatternExecs <= 0) continue;

                    activeCraft.reportedToCpuTotalPatterns += completedPatternExecs;
                    currentPendingCraft.updateReportedByAC(completedPatternExecs);

                    if (currentPendingCraft.requesterHost instanceof ICrazyCraftCallback callbackHost) {
                        callbackHost.onCraftBatchCompletedCallback(details, completedPatternExecs);
                    }

                    if (currentPendingCraft.reportedToCpu >= currentPendingCraft.originalBatchSize) {
                        List<String> keysForThisPC = pendingToActiveCraftKeysMap.get(currentPendingCraft.uuid);
                        if (keysForThisPC != null && !keysForThisPC.isEmpty()) {
                            for (String keyOfAcInPc : keysForThisPC) {
                                if (!activeCraftKeys.contains(keyOfAcInPc)) {
                                    activeCraftKeys.add(keyOfAcInPc);
                                }
                            }
                        }
                        if (!pendingCraftList.contains(currentPendingCraft)) {
                            pendingCraftList.add(currentPendingCraft);
                        }
                    }
                }
            }

            if (!activeCraftKeys.isEmpty()) {
                for (String key : activeCraftKeys) {
                    removeActiveCraftInternal(key);
                }
            }

            if (!pendingCraftList.isEmpty()) {
                for (QuantumInterfaceDuality.PendingCraft pc : pendingCraftList) {
                    this.pendingCrafts.removeIf(p -> p.uuid.equals(pc.uuid));
                    this.pendingToActiveCraftKeysMap.remove(pc.uuid);
                }
            }

            if (totalConsumedFromSlot > 0) {
                storageInv.extractItem(slot, (int)totalConsumedFromSlot, false);
                changed = true;
            }

            ItemStack finalStackInSlot = storageInv.getStackInSlot(slot);
            IAEItemStack requireWork = this.requireWork[slot];

            if (!finalStackInSlot.isEmpty()) {
                boolean pushedSomething = pushContainedItemsToMe(slot);
                if (pushedSomething) changed = true;
            } else {
                if (requireWork != null) {
                    this.requireWork[slot] = null;
                    changed = true;
                }
            }

            if (requireWork != this.requireWork[slot] &&
                    (requireWork == null || this.requireWork[slot] == null || !requireWork.equals(this.requireWork[slot]))) {
                changed = true;
            }

            if (changed) {
                this.iHost.saveChanges();
            }
        }
    }


    private void addActiveCraftInternal(QuantumInterfaceDuality.ActiveInterfaceCraft active) {
        String pcId = active.pendingCraft.uuid;
        String acKey = pcId + ":" + active.targetSide.getName();

        this.activeCrafts.put(acKey, active);
        this.pendingToActiveCraftKeysMap.computeIfAbsent(pcId, k -> new ArrayList<>()).add(acKey);

        active.pendingCraft.pushedToMachines += active.pushedToMachineTotalPatterns;
        active.pendingCraft.reportedToCpu += active.reportedToCpuTotalPatterns;
    }

    private void removeActiveCraftInternal(String acKey) {
        QuantumInterfaceDuality.ActiveInterfaceCraft acToRemove = this.activeCrafts.remove(acKey);
        if (acToRemove != null && acToRemove.pendingCraft != null) {
            String pcId = acToRemove.pendingCraft.uuid;

            List<String> keysInMap = this.pendingToActiveCraftKeysMap.get(pcId);
            if (keysInMap != null) {
                keysInMap.remove(acKey);
                if (keysInMap.isEmpty()) {
                    this.pendingToActiveCraftKeysMap.remove(pcId);
                }
            }

            acToRemove.pendingCraft.pushedToMachines -= acToRemove.pushedToMachineTotalPatterns;
            acToRemove.pendingCraft.reportedToCpu -= acToRemove.reportedToCpuTotalPatterns;
            acToRemove.pendingCraft.pushedToMachines = Math.max(0, acToRemove.pendingCraft.pushedToMachines);
            acToRemove.pendingCraft.reportedToCpu = Math.max(0, acToRemove.pendingCraft.reportedToCpu);
        }
    }

    private boolean pushContainedItemsToMe(final int slot) {
        IItemHandler inv = this.getInventoryByName("storage");
        final ItemStack stored = inv.getStackInSlot(slot);
        boolean changed = false;

        if (!stored.isEmpty()) {
            try {
                final IAEItemStack work = AEItemStack.fromItemStack(stored);

                IMEMonitor<IAEItemStack> storage = this.proxy.getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                IAEItemStack overflow = storage.injectItems(work, Actionable.SIMULATE, this.mySource);

                if (overflow == null || overflow.getStackSize() == 0) {
                    storage.injectItems(work, Actionable.MODULATE, this.mySource);
                    if (this.requireWork[slot] != null) changed = true;
                    this.requireWork[slot] = null;
                    inv.extractItem(slot, stored.getCount(), false);
                    changed = true;
                } else {
                    if (overflow.getStackSize() < work.getStackSize()) {
                        long amountToInject = work.getStackSize() - overflow.getStackSize();
                        IAEItemStack toInject = work.copy();
                        toInject.setStackSize(amountToInject);

                        storage.injectItems(toInject, Actionable.MODULATE, this.mySource);

                        inv.extractItem(slot, (int)amountToInject, false);

                        IAEItemStack newRequireWork = work.copy();
                        newRequireWork.setStackSize(overflow.getStackSize());
                        if (this.requireWork[slot] == null || !this.requireWork[slot].equals(newRequireWork)) changed = true;
                        this.requireWork[slot] = newRequireWork;
                        changed = true;
                    }
                }
            } catch (GridAccessException ignored) {}
        }
        return changed;
    }

    private void updateCraftingList() {
        final Boolean[] accountedFor = new Boolean[this.getInventoryByName("patterns").getSlots()];
        Arrays.fill(accountedFor, false);

        if (!this.proxy.isReady()) {
            return;
        }

        if (this.craftingList != null) {
            final Iterator<ICraftingPatternDetails> i = this.craftingList.iterator();
            while (i.hasNext()) {
                final ICraftingPatternDetails details = i.next();
                boolean found = false;

                for (int x = 0; x < accountedFor.length; x++) {
                    final ItemStack is = this.getInventoryByName("patterns").getStackInSlot(x);
                    if (Platform.itemComparisons().isSameItem(details.getPattern(), is)) {
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
                this.addToCraftingList(this.getInventoryByName("patterns").getStackInSlot(x));
            }
        }
        try {
            this.proxy.getGrid().postEvent(new MEInterfaceHostStateUpdateEv(this.proxy.getNode()));
        } catch (GridAccessException e) {
            // :(
        }
    }

    public void onReady() {
        if (Platform.isServer()) {
            this.updateCraftingList();

            if (!this.activeCrafts.isEmpty()) {
                TileEntity tile = this.iHost.getTileEntity();
                World world = tile.getWorld();

                for (QuantumInterfaceDuality.ActiveInterfaceCraft ac : this.activeCrafts.values()) {
                    if (ac.inventoryAdaptor == null) {
                        TileEntity neighborTE = world.getTileEntity(tile.getPos().offset(ac.targetSide));
                        ac.tryRestoreAdaptor(neighborTE, ac.targetSide.getOpposite());
                    }
                }
            }
        }
    }

    private void addToCraftingList(final ItemStack is) {
        if (is.isEmpty()) {
            return;
        }

        if (is.getItem() instanceof ICraftingPatternItem cpi) {
            final ICraftingPatternDetails details = cpi.getPatternForItem(is, this.iHost.getTileEntity().getWorld());

            if (details != null) {
                if (this.craftingList == null) {
                    this.craftingList = new ArrayList<>();
                }

                this.craftingList.add(details);
            }
        }
    }

    private boolean isBlocking() {
        return this.cm.getSetting(Settings.BLOCK) == YesNo.YES;
    }

    private void addToSendListFacing(final IAEItemStack is, EnumFacing f) {
        if (this.waitingToSendFacing == null) {
            this.waitingToSendFacing = new EnumMap<>(EnumFacing.class);
        }

        this.waitingToSendFacing.computeIfAbsent(f, k -> new ArrayList<>());

        this.waitingToSendFacing.get(f).add(is);
    }

    private void pushItemsOut(final EnumFacing s) {
        List<IAEItemStack> itemsToPush = this.waitingToSendFacing.get(s);
        if (itemsToPush == null || itemsToPush.isEmpty()) {
            return;
        }

        final TileEntity tile = this.iHost.getTileEntity();
        final World w = tile.getWorld();
        final TileEntity te = w.getTileEntity(tile.getPos().offset(s));
        if (te == null) {
            return;
        }

        Map<IAEItemStack, Long> consolidatedMap = new HashMap<>();
        for (IAEItemStack currentStack : itemsToPush) {
            if (currentStack == null || currentStack.getStackSize() <= 0) continue;
            IAEItemStack keyStack = currentStack.copy();
            keyStack.setStackSize(1);
            consolidatedMap.merge(keyStack, currentStack.getStackSize(), Long::sum);
        }

        itemsToPush.clear();
        for (Map.Entry<IAEItemStack, Long> entry : consolidatedMap.entrySet()) {
            IAEItemStack consolidatedStack = entry.getKey();
            consolidatedStack.setStackSize(entry.getValue());
            itemsToPush.add(consolidatedStack);
        }

        if (itemsToPush.isEmpty()) {
            this.waitingToSendFacing.remove(s);
            return;
        }

        if (te instanceof IInterfaceHost || (te instanceof TileCableBus && ((TileCableBus) te).getPart(s.getOpposite()) instanceof PartInterface)) {
            try {
                IInterfaceHost targetTE;
                Class<?> targetClass;

                if (te instanceof IInterfaceHost) {
                    targetTE = (IInterfaceHost) te;
                    targetClass = te.getClass();
                } else {
                    Object part = ((TileCableBus) te).getPart(s.getOpposite());
                    targetTE = (IInterfaceHost) part;
                    targetClass = part.getClass();
                }

                Method m;
                if (cachedSameGridMethod != null && cachedSameGridMethodClass == targetClass) {
                    m = cachedSameGridMethod;
                } else {
                    m = targetClass.getDeclaredMethod("sameGrid", IGrid.class);
                    m.setAccessible(true);
                    cachedSameGridMethod = m;
                    cachedSameGridMethodClass = targetClass;
                }
                if (((Boolean) m.invoke(targetTE, this.proxy.getGrid()))) {
                    IStorageMonitorableAccessor mon = te.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR, s.getOpposite());
                    if (mon != null) {
                        IStorageMonitorable sm = mon.getInventory(this.mySource);
                        if (sm != null) {
                            IMEMonitor<IAEItemStack> inv = sm.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                            if (inv != null) {
                                final Iterator<IAEItemStack> i = itemsToPush.iterator();
                                while (i.hasNext()) {
                                    IAEItemStack whatToSend = i.next();
                                    final IAEItemStack result = inv.injectItems(whatToSend, Actionable.MODULATE, this.mySource);
                                    if (result != null && result.getStackSize() > 0) {
                                        whatToSend.setStackSize(result.getStackSize());
                                    } else {
                                        i.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (GridAccessException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {}
        } else {
            final InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
            if (ad == null) return;

            final Iterator<IAEItemStack> i = itemsToPush.iterator();
            while (i.hasNext()) {
                IAEItemStack whatToSend = i.next();
                if (whatToSend == null || whatToSend.getStackSize() <= 0) {
                    i.remove();
                    continue;
                }

                ItemStack mcStack = whatToSend.createItemStack();
                final ItemStack result = ad.addItems(mcStack);

                if (!result.isEmpty()) {
                    whatToSend.setStackSize(result.getCount());
                } else {
                    i.remove();
                }
            }
        }

        if (itemsToPush.isEmpty()) {
            this.waitingToSendFacing.remove(s);
        }
    }

    private void onPushPatternSuccess() {
        resetCraftingLock();

        LockCraftingMode lockMode = (LockCraftingMode) cm.getSetting(Settings.UNLOCK);
        switch (lockMode) {
            case LOCK_UNTIL_PULSE, LOCK_UNTIL_RESULT -> saveChanges();
        }
    }

    @Override
    public void cancelCraftingForPattern(ICraftingPatternDetails detailsToCancel, ICrazyCraftHost requestingCpu) {
        if (detailsToCancel == null || requestingCpu == null) {
            return;
        }
        DimensionalCoord cpuCoord = getRequesterCoord(requestingCpu);
        if (cpuCoord == null) {
            return;
        }

        ItemStack pattern = detailsToCancel.getPattern();
        if (pattern == null || pattern.isEmpty()) {
            return;
        }

        boolean changed = false;

        Iterator<Map.Entry<String, QuantumInterfaceDuality.ActiveInterfaceCraft>> activeIterator = this.activeCrafts.entrySet().iterator();
        while (activeIterator.hasNext()) {
            Map.Entry<String, QuantumInterfaceDuality.ActiveInterfaceCraft> entry = activeIterator.next();
            QuantumInterfaceDuality.ActiveInterfaceCraft active = entry.getValue();
            QuantumInterfaceDuality.PendingCraft pending = active.pendingCraft;

            if (pending != null && pending.resolveEnvironment(this.iHost.getTileEntity().getWorld()) && pending.patternDetails != null) {
                ItemStack currentPatternStack = pending.patternDetails.getPattern();
                if (Platform.itemComparisons().isSameItem(currentPatternStack, pattern) &&
                        pending.requester != null && pending.requester.equals(cpuCoord)) {

                    String acKey = entry.getKey();
                    activeIterator.remove();

                    if (pending.uuid != null) {
                        List<String> keysInMap = pendingToActiveCraftKeysMap.get(pending.uuid);
                        if (keysInMap != null) {
                            keysInMap.remove(acKey);
                            if (keysInMap.isEmpty()) {
                                pendingToActiveCraftKeysMap.remove(pending.uuid);
                            }
                        }
                    }
                    pending.pushedToMachines -= active.pushedToMachineTotalPatterns;
                    pending.reportedToCpu -= active.reportedToCpuTotalPatterns;
                    pending.pushedToMachines = Math.max(0, pending.pushedToMachines);
                    pending.reportedToCpu = Math.max(0, pending.reportedToCpu);

                    changed = true;
                }
            }
        }

        Iterator<QuantumInterfaceDuality.PendingCraft> pendingIterator = this.pendingCrafts.iterator();
        while (pendingIterator.hasNext()) {
            QuantumInterfaceDuality.PendingCraft pending = pendingIterator.next();
            if (pending.resolveEnvironment(this.iHost.getTileEntity().getWorld()) && pending.patternDetails != null) {
                ItemStack currentPatternStack = pending.patternDetails.getPattern();
                boolean shouldRemovePending = Platform.itemComparisons().isSameItem(currentPatternStack, pattern) &&
                        pending.requester != null && pending.requester.equals(cpuCoord);

                if (!pendingToActiveCraftKeysMap.containsKey(pending.uuid) && activeCrafts.values().stream().noneMatch(ac -> ac.pendingCraft == pending)) {
                    shouldRemovePending = true;
                }


                if (shouldRemovePending) {
                    pendingIterator.remove();
                    pendingToActiveCraftKeysMap.remove(pending.uuid);
                    changed = true;
                }
            } else if (pending.patternDetails == null && !pending.patternStack.isEmpty()){
                if (Platform.itemComparisons().isSameItem(pending.patternStack, pattern) &&
                        pending.requester != null && pending.requester.equals(cpuCoord)) {
                    pendingIterator.remove();
                    pendingToActiveCraftKeysMap.remove(pending.uuid);
                    changed = true;
                }
            }
        }

        if (changed) {
            this.iHost.saveChanges();
        }
    }

    @Override
    public long estimatePushableBatchSize(ICraftingPatternDetails details, long desiredBatchSize, ICrazyCraftHost requestingCpu, World world) {
        if (details == null || desiredBatchSize <= 0 || world == null) {
            return 0;
        }

        final TileEntity tile = this.getHost().getTile();
        if (tile == null) return 0;

        long maxPushableBatch = 0;

        int facesAvailable = 0;
        EnumSet<EnumFacing> facesToCheck = this.iHost.getTargets();
        for (final EnumFacing s : facesToCheck) {
            final TileEntity te = world.getTileEntity(tile.getPos().offset(s));
            if (te == null) continue;

            if (te instanceof IInterfaceHost || (te instanceof TileCableBus && ((TileCableBus) te).getPart(s.getOpposite()) instanceof PartInterface)) {
                try {
                    IGridNode otherNode = null;
                    if (te instanceof IInterfaceHost)
                        otherNode = ((IInterfaceHost) te).getActionableNode();
                    else if (te instanceof TileCableBus) {
                        Object part = ((TileCableBus) te).getPart(s.getOpposite());
                        if (part instanceof IInterfaceHost)
                            otherNode = ((IInterfaceHost) part).getActionableNode();
                    }
                    if (otherNode != null && this.proxy.getGrid() != null && otherNode.getGrid() == this.proxy.getGrid()) {
                        continue;
                    }
                } catch (GridAccessException ignored) {}
            }


            InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
            if (ad == null) {
                continue;
            }

            if (!(ad instanceof IMixinAdaptorItemHandler mixinAdaptor)) {
                long currentBatch = 0;
                boolean canPushOneUnit = true;
                for (IAEItemStack inputTemplate : details.getCondensedInputs()) {
                    if (inputTemplate == null || inputTemplate.getStackSize() <= 0) continue;
                    ItemStack mcStack = inputTemplate.createItemStack();
                    if (mcStack.isEmpty()) {
                        canPushOneUnit = false;
                        break;
                    }
                    ItemStack remainder = ad.simulateAdd(mcStack);
                    if (!remainder.isEmpty()) {
                        canPushOneUnit = false;
                        break;
                    }
                }

                if (canPushOneUnit) {
                    currentBatch = 1;
                }

                maxPushableBatch = Math.max(maxPushableBatch, currentBatch);
                facesAvailable++;
                continue;
            }

            long currentMaxBatchForFace = desiredBatchSize;

            for (IAEItemStack inputTemplate : details.getCondensedInputs()) {
                if (inputTemplate == null || inputTemplate.getStackSize() <= 0) continue;

                ItemStack templateStack = inputTemplate.createItemStack();
                if (templateStack.isEmpty()) {
                    currentMaxBatchForFace = 0;
                    break;
                }

                long canAcceptForItemType = mixinAdaptor.crazyae$estimateInsertableAmount(templateStack);
                long itemsPerPatternExecution = inputTemplate.getStackSize();

                if (itemsPerPatternExecution <= 0) {
                    currentMaxBatchForFace = 0;
                    break;
                }

                long numPatternExecutionsPossibleForItem = canAcceptForItemType / itemsPerPatternExecution;
                currentMaxBatchForFace = Math.min(currentMaxBatchForFace, numPatternExecutionsPossibleForItem);

                if (currentMaxBatchForFace == 0) break;
            }
            maxPushableBatch = Math.max(maxPushableBatch, currentMaxBatchForFace);
            facesAvailable++;
        }
        return maxPushableBatch * facesAvailable;
    }


    @Override
    public boolean pushDetails(ICraftingPatternDetails details, long requestedBatchSize, ICrazyCraftHost who) {
        final TileEntity tile = this.getHost().getTile();
        if (tile == null) return false;
        final World w = tile.getWorld();
        final DimensionalCoord requesterCoord = getRequesterCoord(who);

        if (requesterCoord == null) {
            return false;
        }

        if (getCraftingLockedReason() != LockCraftingMode.NONE) {
            return false;
        }

        if (requestedBatchSize <= 0) {
            return false;
        }

        boolean successfullyPushed = false;
        QuantumInterfaceDuality.PendingCraft pendingCraft = null;


        EnumSet<EnumFacing> facesToCheck = this.iHost.getTargets();
        for (final EnumFacing s : facesToCheck) {
            final TileEntity te = w.getTileEntity(tile.getPos().offset(s));
            if (te == null) continue;

            if (te instanceof IInterfaceHost || (te instanceof TileCableBus && ((TileCableBus) te).getPart(s.getOpposite()) instanceof PartInterface)) {
                try {
                    IGridNode otherNode = null;
                    if (te instanceof IInterfaceHost)
                        otherNode = ((IInterfaceHost) te).getActionableNode();
                    else if (te instanceof TileCableBus) {
                        Object part = ((TileCableBus) te).getPart(s.getOpposite());
                        if (part instanceof IInterfaceHost)
                            otherNode = ((IInterfaceHost) part).getActionableNode();
                    }

                    if (otherNode != null && otherNode.getGrid() == this.proxy.getGrid()) {
                        continue;
                    }
                } catch (GridAccessException ignored) {}
            }

            InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
            if (ad == null) {
                continue;
            }

            if (ad instanceof IMixinAdaptorItemHandler mixinAdaptor) {
                if (mixinAdaptor.crazyae$isInventoryFull()) {
                    continue;
                }
            }

            if (isBlocking() && checkBlocking(w, tile.getPos().offset(s), te, ad, s)) {
                continue;
            }

            if (pendingCraft == null) {
                Map<IAEItemStack, Long> expectedOutputsTotal = calculateExpectedOutputs(details, requestedBatchSize);
                if (expectedOutputsTotal == null) {
                    return false;
                }
                pendingCraft = new QuantumInterfaceDuality.PendingCraft(requesterCoord, details.getPattern(), details, requestedBatchSize, expectedOutputsTotal);
            }

            QuantumInterfaceDuality.ActiveInterfaceCraft active = new QuantumInterfaceDuality.ActiveInterfaceCraft(pendingCraft, s, ad);
            addActiveCraftInternal(active);

            boolean foundPending = false;
            for (QuantumInterfaceDuality.PendingCraft pc : this.pendingCrafts) {
                if (pc.uuid.equals(pendingCraft.uuid)) {
                    foundPending = true;
                    break;
                }
            }

            if (!foundPending) {
                this.pendingCrafts.add(pendingCraft);
            }

            successfullyPushed = true;
        }

        if (successfullyPushed) {
            this.iHost.saveChanges();
            if (who instanceof ICrazyCraftCallback c) {
                c.onCraftSentCallback(details, requestedBatchSize);
            }
            onPushPatternSuccess();
            return true;
        }

        return false;
    }

    private DimensionalCoord getRequesterCoord(ICrazyCraftHost who) {
        if (who instanceof TileEntity) {
            return new DimensionalCoord((TileEntity) who);
        } else if (who instanceof IGridNode node && node.getMachine() instanceof TileEntity) {
            return new DimensionalCoord((TileEntity)node.getMachine());
        }
        return null;
    }

    private static Map<IAEItemStack, Long> calculateExpectedOutputs(ICraftingPatternDetails details, long batchSize) {
        Map<IAEItemStack, Long> expectedOutputs = new HashMap<>();
        for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
            if (outputTemplate != null && outputTemplate.getStackSize() > 0) {
                long totalExpected = Utils.multiplySafely(outputTemplate.getStackSize(), batchSize);
                if (totalExpected > 0) {
                    IAEItemStack keyStack = outputTemplate.copy();
                    keyStack.setStackSize(1);
                    expectedOutputs.put(keyStack, expectedOutputs.getOrDefault(keyStack, 0L) + totalExpected);
                } else if (totalExpected < 0) {
                    return null;
                }
            }
        }
        return expectedOutputs;
    }

    private boolean checkBlocking(World w, BlockPos targetPos, TileEntity te, InventoryAdaptor ad, EnumFacing s) {
        IPhantomTile phantomTE;
        if (ModsChecker.AA_LOADED && te instanceof IPhantomTile) {
            phantomTE = ((IPhantomTile) te);
            if (phantomTE.hasBoundPosition()) {
                TileEntity phantom = w.getTileEntity(phantomTE.getBoundPosition());
                if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(phantomTE.getBoundPosition()).getBlock().getRegistryName().getNamespace())) {
                    return isCustomInvBlocking(phantom, s);
                }
            }
        } else if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(targetPos).getBlock().getRegistryName().getNamespace())) {
            return isCustomInvBlocking(te, s);
        }
        return invIsBlocked(ad);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        NBTTagList pendingList = new NBTTagList();
        for (QuantumInterfaceDuality.PendingCraft craft : pendingCrafts) {
            pendingList.appendTag(craft.writeToNBT());
        }
        data.setTag("pendingCrafts", pendingList);

        NBTTagList activeCraftsNBTList = new NBTTagList();
        for (QuantumInterfaceDuality.ActiveInterfaceCraft ac : this.activeCrafts.values()) {
            activeCraftsNBTList.appendTag(ac.writeToNBT());
        }
        data.setTag("activeCraftsData", activeCraftsNBTList);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        this.pendingCrafts.clear();
        NBTTagList pendingList = data.getTagList("pendingCrafts", 10);
        for (int i = 0; i < pendingList.tagCount(); i++) {
            NBTTagCompound craftTag = pendingList.getCompoundTagAt(i);
            QuantumInterfaceDuality.PendingCraft craft = QuantumInterfaceDuality.PendingCraft.readFromNBT(craftTag);
            if (craft != null) {
                this.pendingCrafts.add(craft);
            }
        }

        for (QuantumInterfaceDuality.PendingCraft pc : this.pendingCrafts) {
            pc.pushedToMachines = 0;
            pc.reportedToCpu = 0;
        }

        this.activeCrafts.clear();
        this.pendingToActiveCraftKeysMap.clear();
        NBTTagList activeCraftsNBTList = data.getTagList("activeCraftsData", 10);
        World worldForACLoad = (this.iHost != null && this.iHost.getTileEntity() != null) ? this.iHost.getTileEntity().getWorld() : null;

        for (int i = 0; i < activeCraftsNBTList.tagCount(); i++) {
            NBTTagCompound acTag = activeCraftsNBTList.getCompoundTagAt(i);
            QuantumInterfaceDuality.ActiveInterfaceCraft ac = QuantumInterfaceDuality.ActiveInterfaceCraft.readFromNBT(acTag, this.pendingCrafts, worldForACLoad);
            if (ac != null && ac.pendingCraft != null) {
                addActiveCraftInternal(ac);
            }
        }
    }

    private boolean isCustomInvBlocking(TileEntity te, EnumFacing s) {
        BlockingInventoryAdaptor blockingInventoryAdaptor = BlockingInventoryAdaptor.getAdaptor(te, s.getOpposite());
        return invIsCustomBlocking(blockingInventoryAdaptor);
    }

    private static boolean invIsCustomBlocking(BlockingInventoryAdaptor inv) {
        return (inv.containsBlockingItems());
    }

    private boolean invIsBlocked(InventoryAdaptor inv) {
        return (inv.containsItems());
    }

    private long tryPushNextMicroBatch(QuantumInterfaceDuality.ActiveInterfaceCraft active, long maxUnitsAmt) {
        if (maxUnitsAmt <= 0) {
            return 0;
        }
        if (active.inventoryAdaptor == null) {
            return 0;
        }

        final World currentHostWorld = this.getTile().getWorld();

        if (!active.pendingCraft.resolveEnvironment(currentHostWorld)) {
            return 0;
        }

        ICraftingPatternDetails details = active.pendingCraft.patternDetails;
        if (details == null) {
            return 0;
        }

        IAEItemStack[] condensedInputs = details.getCondensedInputs();
        if (condensedInputs == null || condensedInputs.length == 0) {
            return maxUnitsAmt;
        }

        long maxUnitsToPush = maxUnitsAmt;

        if (active.inventoryAdaptor instanceof IMixinAdaptorItemHandler mixinAdaptor) {
            long estimatedUnits = Long.MAX_VALUE;
            for (IAEItemStack inputTemplate : condensedInputs) {
                if (inputTemplate == null || inputTemplate.getStackSize() <= 0) continue;
                ItemStack templateStack = inputTemplate.createItemStack();
                long canAccept = mixinAdaptor.crazyae$estimateInsertableAmount(templateStack);
                long itemsPerPatternExecution = inputTemplate.getStackSize();
                if (itemsPerPatternExecution <= 0) {
                    estimatedUnits = 0;
                    break;
                }
                long patternExecsAmt = canAccept / itemsPerPatternExecution;
                estimatedUnits = Math.min(estimatedUnits, patternExecsAmt);
                if (estimatedUnits == 0) break;
            }
            maxUnitsToPush = Math.min(maxUnitsAmt, estimatedUnits);
        }

        if (maxUnitsToPush == 0) {
            return 0;
        }

        Map<IAEItemStack, ItemStack> templateToMcStackCache = new HashMap<>();
        for (IAEItemStack inputTemplate : condensedInputs) {
            if (inputTemplate != null && inputTemplate.getStackSize() > 0) {
                ItemStack mcStack = inputTemplate.createItemStack();
                if (mcStack.isEmpty()) return 0;
                templateToMcStackCache.put(inputTemplate, mcStack);
            }
        }

        long actualPushUnitsAmt = 0;

        if (maxUnitsToPush > 0) {
            boolean canPushFirstUnit = true;
            for (IAEItemStack inputTemplate : condensedInputs) {
                if (inputTemplate == null || inputTemplate.getStackSize() <= 0) continue;
                ItemStack mcStackToSimulate = templateToMcStackCache.get(inputTemplate);
                ItemStack remainder = active.inventoryAdaptor.simulateAdd(mcStackToSimulate);
                if (!remainder.isEmpty()) {
                    canPushFirstUnit = false;
                    break;
                }
            }

            if (canPushFirstUnit) {
                actualPushUnitsAmt = maxUnitsToPush;
            }
        }


        if (actualPushUnitsAmt > 0) {
            Map<IAEItemStack, Long> itemsSent = new HashMap<>();
            for (IAEItemStack inputTemplate : condensedInputs) {
                if (inputTemplate == null || inputTemplate.getStackSize() <= 0) continue;
                IAEItemStack key = inputTemplate.copy();
                key.setStackSize(1);
                itemsSent.merge(key, Utils.multiplySafely(inputTemplate.getStackSize(), actualPushUnitsAmt), Long::sum);
            }

            if (!itemsSent.isEmpty()) {
                for (Map.Entry<IAEItemStack, Long> entry : itemsSent.entrySet()) {
                    IAEItemStack itemToSend = entry.getKey();
                    itemToSend.setStackSize(entry.getValue());
                    addToSendListFacing(itemToSend, active.targetSide);
                }
            }
        }

        return actualPushUnitsAmt;
    }

    private static List<IAEItemStack> calculateExpectedOutputsForMicroBatch(ICraftingPatternDetails details, long microBatchPatternExecutions) {
        List<IAEItemStack> expected = new ArrayList<>();
        if (details == null || microBatchPatternExecutions <= 0) return expected;

        for (IAEItemStack outputTemplate : details.getCondensedOutputs()) {
            if (outputTemplate != null && outputTemplate.getStackSize() > 0) {
                long totalAmount = Utils.multiplySafely(outputTemplate.getStackSize(), microBatchPatternExecutions);
                if (totalAmount > 0) {
                    IAEItemStack batchOutput = outputTemplate.copy();
                    batchOutput.setStackSize(totalAmount);
                    expected.add(batchOutput);
                }
            }
        }

        expected.addAll(Utils.getContainerItemsFromInputs(details, microBatchPatternExecutions));
        return expected;
    }

    @Override
    public void tickInterfaceHost(IGrid grid, CrazyAutocraftingSystem cache) {
        boolean changedThisTick = false;

        TileEntity hostTileEntity = this.iHost.getTileEntity();
        World hostWorld = hostTileEntity.getWorld();

        IItemHandler storageInv = this.getInventoryByName("storage");

        for (int i = 0; i < storageInv.getSlots(); i++) {
            ItemStack is = storageInv.getStackInSlot(i);
            if (!is.isEmpty()) {
                pushContainedItemsToMe(i);
            }
        }


        List<String> activeCraftKeysToRemove = new ArrayList<>();
        List<QuantumInterfaceDuality.PendingCraft> pendingCraftsToRemove = new ArrayList<>();

        for (Map.Entry<String, QuantumInterfaceDuality.ActiveInterfaceCraft> entry : this.activeCrafts.entrySet()) {
            String activeCraftKey = entry.getKey();
            QuantumInterfaceDuality.ActiveInterfaceCraft active = entry.getValue();

            if (active == null) continue;

            if (active.pendingCraft == null) {
                if (!activeCraftKeysToRemove.contains(activeCraftKey)) {
                    activeCraftKeysToRemove.add(activeCraftKey);
                }
                continue;
            }

            if (active.inventoryAdaptor == null) {
                TileEntity neighborTE = hostWorld.getTileEntity(hostTileEntity.getPos().offset(active.targetSide));
                active.tryRestoreAdaptor(neighborTE, active.targetSide.getOpposite());
                if (active.inventoryAdaptor == null) {
                    continue;
                }
            }

            if (!active.pendingCraft.resolveEnvironment(hostWorld)) {
                continue;
            }

            QuantumInterfaceDuality.PendingCraft currentPendingCraft = active.pendingCraft;
            if (currentPendingCraft.patternDetails == null) {
                continue;
            }

            long totalCurrentlyPushed = currentPendingCraft.pushedToMachines;
            long globallyRemaining = currentPendingCraft.originalBatchSize - totalCurrentlyPushed;

            if (globallyRemaining > 0 && active.canPushNextMicroBatch) {
                long patternsLeftForAC = currentPendingCraft.originalBatchSize - active.pushedToMachineTotalPatterns;
                if (patternsLeftForAC <= 0) {
                    continue;
                }

                long attemptsForAC = Math.min(globallyRemaining, MAX_PATTERN_EXECUTIONS_TO_PUSH_PER_AC_TICK);
                attemptsForAC = Math.min(attemptsForAC, patternsLeftForAC);

                if (attemptsForAC > 0) {
                    long pushed = tryPushNextMicroBatch(active, attemptsForAC);

                    if (pushed > 0) {
                        active.startNewMicroBatch(pushed, currentPendingCraft.patternDetails);
                        currentPendingCraft.updatePushedByAC(pushed);
                        changedThisTick = true;

                        if (active.isMicroBatchTrulyComplete()) {
                            long completedSize = active.finalizeMicrobatch();
                            if (completedSize <= 0 && active.currentMicroBatchTotalOutputsExpected.isEmpty()) {
                                completedSize = pushed;
                            }

                            if (completedSize > 0) {
                                active.reportedToCpuTotalPatterns += completedSize;
                                currentPendingCraft.updateReportedByAC(completedSize);

                                if (currentPendingCraft.requesterHost instanceof ICrazyCraftCallback callbackHost) {
                                    callbackHost.onCraftBatchCompletedCallback(currentPendingCraft.patternDetails, completedSize);
                                }

                                if (currentPendingCraft.reportedToCpu >= currentPendingCraft.originalBatchSize) {
                                    List<String> keysForThisPC = pendingToActiveCraftKeysMap.get(currentPendingCraft.uuid);
                                    if (keysForThisPC != null && !keysForThisPC.isEmpty()) {
                                        for (String keyOfAcInPc : keysForThisPC) {
                                            if (!activeCraftKeysToRemove.contains(keyOfAcInPc)) {
                                                activeCraftKeysToRemove.add(keyOfAcInPc);
                                            }
                                        }
                                    }
                                    if (!pendingCraftsToRemove.contains(currentPendingCraft)) {
                                        pendingCraftsToRemove.add(currentPendingCraft);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!activeCraftKeysToRemove.isEmpty()) {
            for (String key : activeCraftKeysToRemove) {
                removeActiveCraftInternal(key);
            }
            changedThisTick = true;
        }

        if (!pendingCraftsToRemove.isEmpty()) {
            for (QuantumInterfaceDuality.PendingCraft pc : pendingCraftsToRemove) {
                this.pendingCrafts.removeIf(p -> p.uuid.equals(pc.uuid));
                this.pendingToActiveCraftKeysMap.remove(pc.uuid);
            }
        }

        if (this.waitingToSendFacing != null && !this.waitingToSendFacing.isEmpty()) {
            boolean itemsPushed = false;
            EnumSet<EnumFacing> faces = EnumSet.noneOf(EnumFacing.class);
            if (this.waitingToSendFacing != null) {
                faces.addAll(this.waitingToSendFacing.keySet());
            }


            for (EnumFacing face : faces) {
                List<IAEItemStack> itemsList = this.waitingToSendFacing.get(face);
                if (itemsList != null && !itemsList.isEmpty()) {
                    long beforePush = 0;
                    for (IAEItemStack stack : itemsList) {
                        if (stack != null) {
                            beforePush += stack.getStackSize();
                        }
                    }

                    pushItemsOut(face);

                    List<IAEItemStack> itemsListAfterPush = this.waitingToSendFacing.get(face);
                    if (itemsListAfterPush == null || itemsListAfterPush.isEmpty()) {
                        itemsPushed = true;
                    } else {
                        long countAfterPush = 0;
                        for (IAEItemStack stack : itemsListAfterPush) {
                            if (stack != null) {
                                countAfterPush += stack.getStackSize();
                            }
                        }
                        if (countAfterPush < beforePush) {
                            itemsPushed = true;
                        }
                    }
                } else {
                    if (this.waitingToSendFacing != null) {
                        this.waitingToSendFacing.remove(face);
                    }
                }
            }
            if (itemsPushed) {
                changedThisTick = true;
            }
        }

        if (this.waitingToSendFacing != null) {
            this.waitingToSendFacing.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        }

        if (changedThisTick) {
            this.iHost.saveChanges();
        }
    }

    @Override
    public boolean canAcceptPattern(ICraftingPatternDetails details) {
        return this.craftingList.contains(details);
    }


    @Override
    public IGridNode getNode() {
        return this.proxy.getNode();
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        try {
            // NO-OP send own packet instead :P
            this.proxy.getGrid().postEvent(new MEInterfaceHostStateUpdateEv(this.proxy.getNode()));
        } catch (GridAccessException e) {
            // :(
        }
    }

    @Override
    public void provideCrafting(ICrazyCraftingProviderHelper var1) {
        if (this.proxy.isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                details.setPriority(this.getPriority());
                var1.addCraftingOption(this, details);
            }
        }
    }

    private static class ActiveInterfaceCraft {
        final QuantumInterfaceDuality.PendingCraft pendingCraft;
        final EnumFacing targetSide;

        InventoryAdaptor inventoryAdaptor;
        long pushedToMachineTotalPatterns;
        long reportedToCpuTotalPatterns;

        boolean canPushNextMicroBatch;

        long currentMicroBatchSize;
        Map<IAEItemStack, Long> currentMicroBatchTotalOutputsExpected;
        Map<IAEItemStack, Long> currentMicroBatchTotalOutputsReceived;

        ActiveInterfaceCraft(QuantumInterfaceDuality.PendingCraft pending, EnumFacing side, InventoryAdaptor adaptor) {
            this.pendingCraft = pending;
            this.targetSide = side;
            this.inventoryAdaptor = adaptor;
            this.pushedToMachineTotalPatterns = 0;
            this.reportedToCpuTotalPatterns = 0;
            this.canPushNextMicroBatch = true;

            this.currentMicroBatchSize = 0;
            this.currentMicroBatchTotalOutputsExpected = new HashMap<>();
            this.currentMicroBatchTotalOutputsReceived = new HashMap<>();
        }

        public void tryRestoreAdaptor(TileEntity neighbor, EnumFacing oppositeSide) {
            if (this.inventoryAdaptor == null && neighbor != null) {
                this.inventoryAdaptor = InventoryAdaptor.getAdaptor(neighbor, oppositeSide);
            }
        }


        void startNewMicroBatch(long pushedPatternExecs, ICraftingPatternDetails details) {
            this.pushedToMachineTotalPatterns += pushedPatternExecs;
            this.currentMicroBatchSize = pushedPatternExecs;

            this.currentMicroBatchTotalOutputsReceived.clear();
            this.currentMicroBatchTotalOutputsExpected.clear();

            List<IAEItemStack> outputsForThisBatch = calculateExpectedOutputsForMicroBatch(details, pushedPatternExecs);
            for (IAEItemStack output : outputsForThisBatch) {
                if (output == null || output.getStackSize() <= 0) continue;
                IAEItemStack key = output.copy();
                key.setStackSize(1);
                this.currentMicroBatchTotalOutputsExpected.merge(key, output.getStackSize(), Long::sum);
            }

            this.canPushNextMicroBatch = false;
        }

        boolean recordReceivedAndComplete(IAEItemStack newItemFromSlot, Longium consumedFromNewItem) {
            consumedFromNewItem.set(0);

            if (this.currentMicroBatchSize == 0) {
                return false;
            }

            if (this.currentMicroBatchTotalOutputsExpected.isEmpty()) {
                if (pendingCraft.patternDetails != null && pendingCraft.patternDetails.getCondensedOutputs().length == 0) {
                    return isMicroBatchTrulyComplete();
                }
                return false;
            }


            IAEItemStack keyItem = newItemFromSlot.copy();
            keyItem.setStackSize(1);

            Long totalExpectedForType = this.currentMicroBatchTotalOutputsExpected.get(keyItem);
            if (totalExpectedForType != null) {
                long alreadyReceived = this.currentMicroBatchTotalOutputsReceived.getOrDefault(keyItem, 0L);
                long neededMore = totalExpectedForType - alreadyReceived;

                if (neededMore > 0) {
                    long canTake = Math.min(newItemFromSlot.getStackSize(), neededMore);
                    this.currentMicroBatchTotalOutputsReceived.merge(keyItem, canTake, Long::sum);
                    consumedFromNewItem.set(canTake);
                }
            }
            return isMicroBatchTrulyComplete();
        }

        boolean isMicroBatchTrulyComplete() {
            if (this.currentMicroBatchSize == 0) {
                return false;
            }

            if (this.currentMicroBatchTotalOutputsExpected.isEmpty()) {
                return pendingCraft.patternDetails != null && pendingCraft.patternDetails.getCondensedOutputs().length == 0;
            }

            for (Map.Entry<IAEItemStack, Long> entry : this.currentMicroBatchTotalOutputsExpected.entrySet()) {
                if (this.currentMicroBatchTotalOutputsReceived.getOrDefault(entry.getKey(), 0L) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        long finalizeMicrobatch() {
            long size = this.currentMicroBatchSize;
            this.currentMicroBatchSize = 0;
            this.canPushNextMicroBatch = true;
            return size;
        }

        NBTTagCompound writeToNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            if (this.pendingCraft == null || this.pendingCraft.uuid == null) {
                // :(
                return new NBTTagCompound();
            }
            tag.setString("pendingId", this.pendingCraft.uuid);
            tag.setString("side", this.targetSide.getName());
            tag.setLong("pushedTotal", this.pushedToMachineTotalPatterns);
            tag.setLong("reportedTotal", this.reportedToCpuTotalPatterns);
            tag.setBoolean("canPushNext", this.canPushNextMicroBatch);
            tag.setLong("microBatchSize", this.currentMicroBatchSize);

            NBTTagList expectedList = new NBTTagList();
            if (this.currentMicroBatchTotalOutputsExpected != null) {
                for(Map.Entry<IAEItemStack, Long> entry : this.currentMicroBatchTotalOutputsExpected.entrySet()){
                    NBTTagCompound eTag = new NBTTagCompound();
                    NBTTagCompound key = new NBTTagCompound();
                    entry.getKey().writeToNBT(key);
                    eTag.setTag("key", key);
                    eTag.setLong("val", entry.getValue());
                    expectedList.appendTag(eTag);
                }
            }
            tag.setTag("microExpected", expectedList);

            NBTTagList receivedList = new NBTTagList();
            if (this.currentMicroBatchTotalOutputsReceived != null) {
                for(Map.Entry<IAEItemStack, Long> entry : this.currentMicroBatchTotalOutputsReceived.entrySet()){
                    NBTTagCompound rTag = new NBTTagCompound();
                    NBTTagCompound key = new NBTTagCompound();
                    entry.getKey().writeToNBT(key);
                    rTag.setTag("key", key);
                    rTag.setLong("val", entry.getValue());
                    receivedList.appendTag(rTag);
                }
            }
            tag.setTag("microReceived", receivedList);
            return tag;
        }

        static QuantumInterfaceDuality.ActiveInterfaceCraft readFromNBT(NBTTagCompound tag, List<QuantumInterfaceDuality.PendingCraft> allPendingCrafts, World world) {
            String pendingId = tag.getString("pendingId");
            QuantumInterfaceDuality.PendingCraft foundPending = null;
            for (QuantumInterfaceDuality.PendingCraft pc : allPendingCrafts) {
                if (pc.uuid.equals(pendingId)) {
                    foundPending = pc;
                    break;
                }
            }
            if (foundPending == null) {
                return null;
            }

            EnumFacing side = EnumFacing.byName(tag.getString("side"));
            if (side == null) {
                return null;
            }

            QuantumInterfaceDuality.ActiveInterfaceCraft ac = new QuantumInterfaceDuality.ActiveInterfaceCraft(foundPending, side, null);
            ac.pushedToMachineTotalPatterns = tag.getLong("pushedTotal");
            ac.reportedToCpuTotalPatterns = tag.getLong("reportedTotal");
            ac.canPushNextMicroBatch = tag.getBoolean("canPushNext");
            ac.currentMicroBatchSize = tag.getLong("microBatchSize");

            ac.currentMicroBatchTotalOutputsExpected = new HashMap<>();
            NBTTagList expectedList = tag.getTagList("microExpected", 10);
            for (int i = 0; i < expectedList.tagCount(); i++) {
                NBTTagCompound eTag = expectedList.getCompoundTagAt(i);
                IAEItemStack key = AEItemStack.fromNBT(eTag.getCompoundTag("key"));
                if(key != null) ac.currentMicroBatchTotalOutputsExpected.put(key, eTag.getLong("val"));
            }

            ac.currentMicroBatchTotalOutputsReceived = new HashMap<>();
            NBTTagList receivedList = tag.getTagList("microReceived", 10);
            for (int i = 0; i < receivedList.tagCount(); i++) {
                NBTTagCompound rTag = receivedList.getCompoundTagAt(i);
                IAEItemStack key = AEItemStack.fromNBT(rTag.getCompoundTag("key"));
                if(key != null) ac.currentMicroBatchTotalOutputsReceived.put(key, rTag.getLong("val"));
            }
            return ac;
        }
    }

    private static class PendingCraft {
        final DimensionalCoord requester;
        final ItemStack patternStack;
        ICraftingPatternDetails patternDetails;
        final long originalBatchSize;
        final Map<IAEItemStack, Long> totalExpectedOutputs;
        final String uuid;

        ICrazyCraftHost requesterHost = null;

        long pushedToMachines = 0;
        long reportedToCpu = 0;

        PendingCraft(DimensionalCoord requesterCoord, ItemStack patternStack, ICraftingPatternDetails details, long batchSize, Map<IAEItemStack, Long> expectedOutputs) {
            this.requester = requesterCoord;
            this.patternStack = patternStack;
            this.patternDetails = details;
            this.originalBatchSize = batchSize;
            this.totalExpectedOutputs = new HashMap<>(expectedOutputs);
            this.uuid = UUID.randomUUID().toString().substring(0, 8);
        }

        PendingCraft(DimensionalCoord requesterCoord, ItemStack patternStack, long batchSize, String uniqueId) {
            this.requester = requesterCoord;
            this.patternStack = patternStack;
            this.originalBatchSize = batchSize;
            this.uuid = uniqueId;
            this.patternDetails = null;
            this.totalExpectedOutputs = new HashMap<>();
        }

        public void updatePushedByAC(long deltaPushed) {
            this.pushedToMachines += deltaPushed;
        }

        public void updateReportedByAC(long deltaReported) {
            this.reportedToCpu += deltaReported;
        }

        boolean resolveEnvironment(World world) {
            if (world == null) return false;
            if (requesterHost == null && requester != null) {
                World worldForCoord = requester.getWorld();
                if (worldForCoord == null) worldForCoord = world;
                TileEntity te = worldForCoord.getTileEntity(requester.getPos());
                if (te instanceof ICrazyCraftHost)
                    requesterHost = (ICrazyCraftHost) te;
                else
                    return false;
            }
            if (patternDetails == null && !patternStack.isEmpty() && patternStack.getItem() instanceof ICraftingPatternItem cpi) {
                patternDetails = cpi.getPatternForItem(patternStack, world);
                if (patternDetails != null && totalExpectedOutputs.isEmpty()) {
                    Map<IAEItemStack, Long> calculatedExpected = calculateExpectedOutputs(patternDetails, originalBatchSize);
                    if(calculatedExpected != null) this.totalExpectedOutputs.putAll(calculatedExpected);
                }
                if (patternDetails == null)
                    return false;
            }
            return requesterHost != null && patternDetails != null;
        }

        NBTTagCompound writeToNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            if (requester != null) tag.setTag("reqCoord", NBTUtils.writeDimensionalCoord(requester));
            if (!patternStack.isEmpty()) tag.setTag("pattern", patternStack.writeToNBT(new NBTTagCompound()));
            tag.setLong("origBatch", originalBatchSize);
            tag.setString("uid", uuid);
            return tag;
        }

        static QuantumInterfaceDuality.PendingCraft readFromNBT(NBTTagCompound tag) {
            try {
                DimensionalCoord coord = NBTUtils.readDimensionalCoord(tag.getCompoundTag("reqCoord"));
                ItemStack stack = new ItemStack(tag.getCompoundTag("pattern"));
                long batchSize = tag.getLong("origBatch");
                String uid = tag.getString("uid");

                if (coord == null || stack.isEmpty() || batchSize <= 0) return null;

                return new QuantumInterfaceDuality.PendingCraft(coord, stack, batchSize, uid);
            } catch (Exception ignored) {}

            return null;
        }
    }
}