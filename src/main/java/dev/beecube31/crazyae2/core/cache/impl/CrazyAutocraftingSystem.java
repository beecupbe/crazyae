package dev.beecube31.crazyae2.core.cache.impl;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.*;
import appeng.api.networking.crafting.*;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.CraftingJob;
import appeng.me.helpers.BaseActionSource;
import appeng.me.helpers.GenericInterestManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftingMethod;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftingProviderHelper;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyInterfaceHost;
import dev.beecube31.crazyae2.common.networking.events.MECraftHostPatternsChangedEv;
import dev.beecube31.crazyae2.common.networking.events.MECraftHostStateUpdateEv;
import dev.beecube31.crazyae2.common.networking.events.MEInterfaceHostStateUpdateEv;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingJob;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingLink;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingLinkNexus;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingWatcher;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CrazyAutocraftingSystem implements IGridCache, ICellProvider, ICrazyCraftingProviderHelper, IMEInventoryHandler<IAEItemStack>, ICrazyAutocraftingSystem {

    private static final ExecutorService CRAFTING_POOL;

    static {
        final ThreadFactory factory = ar -> new Thread(ar, "CrazyAE Crafting Calculator");

        CRAFTING_POOL = Executors.newCachedThreadPool(factory);
    }

    private static final Comparator<ICraftingPatternDetails> COMPARATOR = (firstDetail, nextDetail) -> nextDetail.getPriority() - firstDetail.getPriority();

    private final Set<ICrazyCraftHost> workersList = new HashSet<>();
    private final Set<ICrazyInterfaceHost> interfacesList = new HashSet<>();
    private final Map<IGridNode, ICraftingWatcher> craftingWatchers = new HashMap<>();
    private final Map<String, CrazyCraftingLinkNexus> craftingLinks = new HashMap<>();
    private final Object2ObjectMap<ICraftingPatternDetails, List<ICrazyCraftingMethod>> craftingMethods = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> craftableItems = new Object2ObjectOpenHashMap<>();
    private final Set<IAEItemStack> emitableItems = new HashSet<>();
    private final Multimap<IAEStack, CrazyCraftingWatcher> interests = HashMultimap.create();
    private final GenericInterestManager<CrazyCraftingWatcher> interestManager = new GenericInterestManager<>(this.interests);

    private Map<ICraftingPatternDetails, List<ICrazyCraftingMethod>> patternToMethodsCache = new HashMap<>();

    private boolean updateList = false;
    private boolean updatePatterns = false;

    private IStorageGrid storageGrid;
    private final IGrid grid;

    public CrazyAutocraftingSystem(final IGrid grid) {
        this.grid = grid;
    }

    @MENetworkEventSubscribe
    public void onCacheConstructionEndEv(final MENetworkPostCacheConstruction cacheConstruction) {
        this.storageGrid = this.grid.getCache(IStorageGrid.class);
        this.storageGrid.registerCellProvider(this);
    }

    @Override
    public void onUpdateTick() {
        if (this.updateList) {
            this.updateList = false;
            this.checkWorkers();
        }

        if (this.updatePatterns) {
            this.recalculateCraftingPatterns();
            this.updatePatterns = false;
        }

        this.craftingLinks.values().removeIf(crazyCraftingLinkNexus -> crazyCraftingLinkNexus.isDead(this.grid, this));

        for (final ICrazyCraftHost host : workersList) {
            host.tickCraftHost(this.grid, this);
        }

        for (final ICrazyInterfaceHost host : interfacesList) {
            host.tickInterfaceHost(this.grid, this);
        }
    }

    private void checkWorkers() {
        workersList.clear();
        interfacesList.clear();

        int uuid = 0;
        for (Object cls : StreamSupport.stream(grid.getMachinesClasses().spliterator(), false).filter(ICrazyCraftHost.class::isAssignableFrom).toArray()) {
            for (final IGridNode cst : this.grid.getMachines((Class<? extends IGridHost>) cls)) {
                final ICrazyCraftHost tile = (ICrazyCraftHost) cst.getMachine();
                if (tile.getStorageCount() > 0) {
                    uuid++;
                    tile.setCpuName(" #" + uuid);
                    workersList.add(tile);

                    if (tile.getLastLink() != null) {
                        this.addLink(tile.getLastLink());
                    }
                }
            }
        }

        for (Object cls : StreamSupport.stream(grid.getMachinesClasses().spliterator(), false).filter(ICrazyInterfaceHost.class::isAssignableFrom).toArray()) {
            for (final IGridNode cst : this.grid.getMachines((Class<? extends IGridHost>) cls)) {
                final ICrazyInterfaceHost tile = (ICrazyInterfaceHost) cst.getMachine();
                interfacesList.add(tile);
            }
        }
    }

    private void recalculateCraftingPatterns() {
        final Object2ObjectMap<IAEItemStack, ImmutableList<ICraftingPatternDetails>> oldItems = new Object2ObjectOpenHashMap<>(this.craftableItems);
        final Set<IAEItemStack> oldEmitableItems = new HashSet<>(this.emitableItems);

        this.craftingMethods.clear();
        this.craftableItems.clear();
        this.emitableItems.clear();
        this.patternToMethodsCache.clear();

        for (final ICrazyCraftHost host : workersList) {
            ((ICrazyCraftingMethod) host).provideCrafting(this);
        }

        for (final ICrazyInterfaceHost host : interfacesList) {
            host.provideCrafting(this);
        }

        for (Map.Entry<ICraftingPatternDetails, List<ICrazyCraftingMethod>> entry : this.craftingMethods.entrySet()) {
            ICraftingPatternDetails details = entry.getKey();
            ItemStack patternStack = details.getPattern();
            if (patternStack != null && !patternStack.isEmpty()) {
                this.patternToMethodsCache.computeIfAbsent(details, k -> new ArrayList<>()).addAll(entry.getValue());
            }
        }

        final Object2ObjectMap<IAEItemStack, ObjectSet<ICraftingPatternDetails>> tmpCraft = new Object2ObjectOpenHashMap<>();

        // new craftables!
        for (final ICraftingPatternDetails details : this.craftingMethods.keySet()) {
            for (IAEItemStack out : details.getOutputs()) {
                if (out == null) {
                    continue;
                }
                out = out.copy();
                out.reset();
                out.setCraftable(true);

                ObjectSet<ICraftingPatternDetails> methods = tmpCraft.get(out);

                if (methods == null) {
                    tmpCraft.put(out, methods = new ObjectRBTreeSet<>(COMPARATOR));
                }

                methods.add(details);
            }
        }

        // make them immutable
        for (final Map.Entry<IAEItemStack, ObjectSet<ICraftingPatternDetails>> e : tmpCraft.entrySet()) {
            this.craftableItems.put(e.getKey(), ImmutableList.copyOf(e.getValue()));
        }

        List<IAEItemStack> craftablesChanged = new ArrayList<>();

        ObjectSet<Map.Entry<IAEItemStack, ImmutableList<ICraftingPatternDetails>>> i = oldItems.entrySet();
        for (Map.Entry<IAEItemStack, ImmutableList<ICraftingPatternDetails>> ais : i) {
            if (!this.craftableItems.containsKey(ais.getKey())) {
                IAEItemStack changedStack = ais.getKey().copy();
                changedStack.reset();
                changedStack.setCraftable(false);
                craftablesChanged.add(changedStack);
            }
        }

        ObjectSet<Map.Entry<IAEItemStack, ImmutableList<ICraftingPatternDetails>>> j = this.craftableItems.entrySet();
        for (Map.Entry<IAEItemStack, ImmutableList<ICraftingPatternDetails>> ais : j) {
            if (!oldItems.containsKey(ais)) {
                IAEItemStack changedStack = ais.getKey().copy();
                changedStack.reset();
                changedStack.setCraftable(true);
                craftablesChanged.add(changedStack);
            }
        }

        for (final IAEItemStack st : oldEmitableItems) {
            if (!emitableItems.contains(st)) {
                IAEItemStack changedStack = st.copy();
                changedStack.reset();
                changedStack.setCraftable(false);
                craftablesChanged.add(changedStack);
            }
        }

        for (final IAEItemStack st : this.emitableItems) {
            if (!oldEmitableItems.contains(st)) {
                IAEItemStack changedStack = st.copy();
                changedStack.reset();
                changedStack.setCraftable(true);
                craftablesChanged.add(changedStack);
            }
        }

        this.storageGrid.postCraftablesChanges(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class), craftablesChanged, new BaseActionSource());
    }

    public void addLink(final CrazyCraftingLink link) {
        if (link.isStandalone()) {
            return;
        }

        CrazyCraftingLinkNexus nexus = this.craftingLinks.computeIfAbsent(link.getCraftingID(), k -> new CrazyCraftingLinkNexus(link.getCraftingID()));

        link.setNexus(nexus);
    }

    @MENetworkEventSubscribe
    public void updateWorkers(final MECraftHostStateUpdateEv c) {
        this.updateList = true;
    }

    @MENetworkEventSubscribe
    public void updateCPUClusters(final MECraftHostPatternsChangedEv c) {
        this.updatePatterns = true;
    }

    @MENetworkEventSubscribe
    public void updateInterfaces(final MEInterfaceHostStateUpdateEv c) {
        this.updatePatterns = true;
    }

    @Override
    public void removeNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof ICraftingWatcherHost) {
            final ICraftingWatcher craftingWatcher = this.craftingWatchers.get(iGridHost);
            if (craftingWatcher != null) {
                craftingWatcher.reset();
                this.craftingWatchers.remove(iGridHost);
            }
        }

        if (iGridHost instanceof ICraftingRequester) {
            for (final CrazyCraftingLinkNexus link : this.craftingLinks.values()) {
                link.removeNode();
            }
        }

        if (iGridHost instanceof final ICrazyCraftHost r) {
            workersList.remove(r);
            this.updateList = true;
            this.updatePatterns = true;
        }

        if (iGridHost instanceof ICrazyInterfaceHost h) {
            interfacesList.remove(h);
            this.updateList = true;
            this.updatePatterns = true;
        }
    }

    @Override
    public void addNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof ICraftingWatcherHost watcherHost) {
            final CrazyCraftingWatcher watcher = new CrazyCraftingWatcher(this, watcherHost);
            this.craftingWatchers.put(iGridNode, watcher);
            watcherHost.updateWatcher(watcher);
        }

        if (iGridHost instanceof final ICrazyCraftHost r) {
            workersList.add(r);
            this.updateList = true;
            this.updatePatterns = true;
        }

        if (iGridHost instanceof ICrazyInterfaceHost h) {
            interfacesList.add(h);
            this.updateList = true;
            this.updatePatterns = true;
        }
    }

    public GenericInterestManager<CrazyCraftingWatcher> getInterestManager() {
        return this.interestManager;
    }

    @Override
    public void onSplit(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void onJoin(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void populateGridStorage(@NotNull IGridStorage iGridStorage) {}

    @Override
    public ImmutableCollection<ICraftingPatternDetails> getCraftingFor(final IAEItemStack whatToCraft, final ICraftingPatternDetails details, final int slotIndex, final World world) {
        final ImmutableList<ICraftingPatternDetails> res = this.craftableItems.get(whatToCraft);

        if (res == null) {
            return ImmutableSet.of();
        }

        return res;
    }

    @Override
    public boolean canEmitFor(IAEItemStack var1) {
        return this.emitableItems.contains(var1);
    }

    @Override
    public ICraftingLink submitCraftingJob(ICraftingJob job, ICraftingRequester requester, ICrazyCraftHost host, IActionSource src) {
        Preconditions.checkNotNull(job, "Cannot submit a null ICraftingJob");
        if (job.isSimulation() || job instanceof CraftingJob) {
            return null;
        }

        List<ICrazyCraftHost> freeWorkers = new ArrayList<>(this.getFreeWorkers());

        if (!freeWorkers.isEmpty()) {
            freeWorkers.sort(Comparator.comparingDouble(ICrazyCraftHost::getAcceleratorCount).thenComparingDouble(ICrazyCraftHost::getStorageCount));

            return freeWorkers.get(0).pushJob(job, requester, src);
        }

        return null;
    }

    @Override
    public Future<ICraftingJob> beginCraftingJob(World world, IGrid grid, IActionSource source, IAEItemStack ais, ICraftingCallback callback) {
        Preconditions.checkArgument(world != null && grid != null && source != null && ais != null, "Invalid crafting job request provided");

        final CrazyCraftingJob job = new CrazyCraftingJob(world, grid, source, ais, callback);

        return CRAFTING_POOL.submit(job, job);
    }

    @Override
    public Set<ICrazyCraftHost> getFreeWorkers() {
        return workersList.stream().filter(ic -> !ic.isBusy()).collect(Collectors.toSet());
    }

    @Override
    public Set<ICrazyCraftHost> getBusyWorkers() {
        return workersList.stream().filter(ICrazyCraftHost::isBusy).collect(Collectors.toSet());
    }

    @Override
    public Set<ICrazyCraftHost> getWorkers() {
        return Collections.unmodifiableSet(this.workersList);
    }


    @Override
    public Set<ICrazyInterfaceHost> findInterfaceByDetails(ICraftingPatternDetails details) {
        Set<ICrazyInterfaceHost> out = new HashSet<>();
        if (details == null) return out;

        for (ICrazyInterfaceHost host : this.interfacesList) {
            if (!host.isBusy() && host.canAcceptPattern(details)) {
                out.add(host);
            }
        }

        return out;
    }

    @Override
    public boolean containsCraftingItem(IAEItemStack req) {
        return this.craftableItems.keySet().stream().anyMatch(is -> is == req || is.getDefinition() == req.getDefinition());
    }

    @Override
    public void addCraftingOption(ICrazyCraftingMethod crafter, ICraftingPatternDetails patternDetails) {
        List<ICrazyCraftingMethod> methodsForPattern = this.craftingMethods.computeIfAbsent(patternDetails, k -> new ArrayList<>());
        if (!methodsForPattern.contains(crafter)) {
            methodsForPattern.add(crafter);
        }
    }

    @Override
    public void setEmitable(IAEItemStack var1) {
        this.emitableItems.add(var1.copy());
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
        final List<IMEInventoryHandler> list = new ArrayList<>(1);

        if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
            list.add(this);
        }

        return list;
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.WRITE;
    }

    @Override
    public boolean isPrioritized(IAEItemStack iaeItemStack) {
        return true;
    }

    @Override
    public boolean canAccept(IAEItemStack iaeItemStack) {
        for (final ICrazyCraftHost cpu : workersList) {
            if (cpu.canAccept(iaeItemStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return i == 1;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        for (final ICrazyCraftHost cpu : workersList) {
            iaeItemStack = cpu.injectItems(iaeItemStack, actionable, iActionSource);
        }

        return iaeItemStack;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
        for (final IAEItemStack stack : this.craftableItems.keySet()) {
            iItemList.addCrafting(stack);
        }

        for (final IAEItemStack st : this.emitableItems) {
            iItemList.addCrafting(st);
        }

        return iItemList;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}
