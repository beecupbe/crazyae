package dev.beecube31.crazyae2.common.duality;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.implementations.tiles.ICraftingMachine;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPart;
import appeng.api.storage.*;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.capabilities.Capabilities;
import appeng.core.localization.GuiText;
import appeng.helpers.*;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.MachineSource;
import appeng.me.storage.MEMonitorIInventory;
import appeng.me.storage.MEMonitorPassThrough;
import appeng.me.storage.NullInventory;
import appeng.parts.automation.StackUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import appeng.parts.misc.PartInterface;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.AppEngInternalOversizedInventory;
import appeng.tile.networking.TileCableBus;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.*;
import appeng.util.item.AEItemStack;
import de.ellpeck.actuallyadditions.api.tile.IPhantomTile;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEInterfaceHost;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.core.CrazyAE;
import gregtech.api.block.machines.BlockMachine;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;

import static appeng.helpers.ItemStackHelper.stackFromNBT;
import static appeng.helpers.ItemStackHelper.stackToNBT;

public class PatternsInterfaceDuality implements IGridTickable, IStorageMonitorable, IInventoryDestination, IAEAppEngInventory, IConfigManagerHost, ICraftingProvider, IUpgradesInfoProvider {
    public static final int NUMBER_OF_STORAGE_SLOTS = 9;
    public static final int NUMBER_OF_PATTERN_SLOTS = 72;

    private static final Collection<Block> BAD_BLOCKS = new HashSet<>(100);

    private final AENetworkProxy gridProxy;
    private final ICrazyAEInterfaceHost iHost;
    private final ConfigManager cm = new ConfigManager(this);
    private final AppEngInternalInventory storage = new AppEngInternalOversizedInventory(this, NUMBER_OF_STORAGE_SLOTS, 8192);
    private final AppEngInternalInventory patterns = new AppEngInternalInventory(this, NUMBER_OF_PATTERN_SLOTS, 1);
    private final MEMonitorPassThrough<IAEItemStack> items = new MEMonitorPassThrough<>(new NullInventory<IAEItemStack>(), AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
    private final MEMonitorPassThrough<IAEFluidStack> fluids = new MEMonitorPassThrough<>(new NullInventory<IAEFluidStack>(), AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
    private final UpgradeInventory upgrades;
    private final Accessor accessor = new Accessor();
    private int priority;
    private List<ICraftingPatternDetails> craftingList = null;
    private List<ItemStack> waitingToSend = null;
    private IMEInventory<IAEItemStack> destination;
    private int isWorking = -1;
    private final MachineSource actionSource;
    private EnumSet<EnumFacing> visitedFaces = EnumSet.noneOf(EnumFacing.class);
    private EnumMap<EnumFacing, List<ItemStack>> waitingToSendFacing = new EnumMap<>(EnumFacing.class);
    private final boolean isActuallyAdditionsLoaded;

    public PatternsInterfaceDuality(final AENetworkProxy networkProxy, final ICrazyAEInterfaceHost ih) {
        this.isActuallyAdditionsLoaded = Loader.isModLoaded("actuallyadditions");
        this.gridProxy = networkProxy;
        this.gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);

        this.upgrades = new StackUpgradeInventory(this.gridProxy.getMachineRepresentation(), this, 4);
        this.cm.registerSetting(Settings.BLOCK, YesNo.NO);
        this.cm.registerSetting(Settings.INTERFACE_TERMINAL, YesNo.YES);

        this.iHost = ih;

        final MachineSource actionSource = new MachineSource(this.iHost);
        this.fluids.setChangeSource(actionSource);
        this.items.setChangeSource(actionSource);
        this.actionSource = actionSource;

    }

    private static boolean invIsCustomBlocking(BlockingInventoryAdaptor inv) {
        return (inv.containsBlockingItems());
    }

    @Override
    public void saveChanges() {
        this.iHost.saveChanges();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.isWorking == slot) {
            return;
        }
        if (inv == this.patterns && (!removed.isEmpty() || !added.isEmpty())) {
            this.updateCraftingList();
        } else if (inv == this.storage && slot >= 0) {
            final boolean had = this.hasWorkToDo();

            final boolean now = this.hasWorkToDo();

            for (int i = 0; i < this.storage.getSlots(); i++) {
                if (!this.storage.getStackInSlot(i).isEmpty()) {
                    this.pushOutStoredItems();
                    break;
                }
            }

            if (had != now) {
                try {
                    if (now) {
                        this.gridProxy.getTick().alertDevice(this.gridProxy.getNode());
                    } else {
                        this.gridProxy.getTick().sleepDevice(this.gridProxy.getNode());
                    }
                } catch (final GridAccessException e) {
                    // :P
                }
            }
        }
    }

    public void writeToNBT(final NBTTagCompound data) {
        this.patterns.writeToNBT(data, "patterns");
        this.storage.writeToNBT(data, "storage");
        this.upgrades.writeToNBT(data, "upgrades");
        this.cm.writeToNBT(data);
        data.setInteger("priority", this.priority);

        final NBTTagList waitingToSend = new NBTTagList();
        if (this.waitingToSend != null) {
            for (final ItemStack is : this.waitingToSend) {
                final NBTTagCompound itemNBT = stackToNBT(is);
                waitingToSend.appendTag(itemNBT);
            }
        }
        data.setTag("waitingToSend", waitingToSend);

        NBTTagCompound sidedWaitList = new NBTTagCompound();

        if (this.waitingToSendFacing != null) {
            for (EnumFacing s : this.iHost.getTargets()) {
                NBTTagList waitingListSided = new NBTTagList();
                if (this.waitingToSendFacing.containsKey(s)) {
                    for (final ItemStack is : this.waitingToSendFacing.get(s)) {
                        final NBTTagCompound itemNBT = stackToNBT(is);
                        waitingListSided.appendTag(itemNBT);
                    }
                    sidedWaitList.setTag(s.name(), waitingListSided);
                }
            }
        }
        data.setTag("sidedWaitList", sidedWaitList);
    }

    public void readFromNBT(final NBTTagCompound data) {
        this.waitingToSend = null;
        final NBTTagList waitingList = data.getTagList("waitingToSend", 10);
        if (waitingList != null) {
            for (int x = 0; x < waitingList.tagCount(); x++) {
                final NBTTagCompound c = waitingList.getCompoundTagAt(x);
                if (c != null) {
                    final ItemStack is = stackFromNBT(c);
                    this.addToSendList(is);
                }
            }
        }

        this.waitingToSendFacing = null;
        final NBTTagCompound waitingListSided = data.getCompoundTag("sidedWaitList");

        for (EnumFacing s : EnumFacing.values()) {
            if (waitingListSided.hasKey(s.name())) {
                NBTTagList w = waitingListSided.getTagList(s.name(), 10);
                for (int x = 0; x < w.tagCount(); x++) {
                    final NBTTagCompound c = w.getCompoundTagAt(x);
                    if (c != null) {
                        final ItemStack is = stackFromNBT(c);
                        this.addToSendListFacing(is, EnumFacing.byIndex(s.getIndex()));
                    }
                }
            }
        }

        // fix upgrade slot size mismatch
        NBTTagCompound up = data.getCompoundTag("upgrades");
        if (up.hasKey("Size") && up.getInteger("Size") != this.upgrades.getSlots()) {
            up.setInteger("Size", this.upgrades.getSlots());
            this.upgrades.writeToNBT(up, "upgrades");
        }

        this.upgrades.readFromNBT(data, "upgrades");

        NBTTagCompound pa = data.getCompoundTag("patterns");
        if (pa.hasKey("Size") && pa.getInteger("Size") != this.patterns.getSlots()) {
            pa.setInteger("Size", this.patterns.getSlots());
            this.upgrades.writeToNBT(pa, "patterns");
        }

        this.patterns.readFromNBT(data, "patterns");
        this.storage.readFromNBT(data, "storage");
        this.priority = data.getInteger("priority");
        this.cm.readFromNBT(data);
        this.updateCraftingList();
    }

    private void addToSendList(final ItemStack is) {
        if (is.isEmpty()) {
            return;
        }

        if (this.waitingToSend == null) {
            this.waitingToSend = new ArrayList<>();
        }

        this.waitingToSend.add(is);

        try {
            this.gridProxy.getTick().wakeDevice(this.gridProxy.getNode());
        } catch (final GridAccessException e) {
            // :P
        }
    }

    private void addToSendListFacing(final ItemStack is, EnumFacing f) {
        if (is.isEmpty()) {
            return;
        }
        if (this.waitingToSendFacing == null) {
            this.waitingToSendFacing = new EnumMap<>(EnumFacing.class);
        }

        this.waitingToSendFacing.computeIfAbsent(f, k -> new ArrayList<>());

        this.waitingToSendFacing.get(f).add(is);

        try {
            this.gridProxy.getTick().wakeDevice(this.gridProxy.getNode());
        } catch (final GridAccessException e) {
            // :P
        }
    } 

    private void updateCraftingList() {
        final Boolean[] accountedFor = new Boolean[this.patterns.getSlots()];
        Arrays.fill(accountedFor, false);

        if (!this.gridProxy.isReady()) {
            return;
        }

        boolean removed = false;

        if (this.craftingList != null) {
            final Iterator<ICraftingPatternDetails> i = this.craftingList.iterator();
            while (i.hasNext()) {
                final ICraftingPatternDetails details = i.next();
                boolean found = false;

                for (int x = 0; x < accountedFor.length; x++) {
                    final ItemStack is = this.patterns.getStackInSlot(x);
                    if (details.getPattern() == is) {
                        accountedFor[x] = found = true;
                    }
                }

                if (!found) {
                    removed = true;
                    i.remove();
                }
            }
        }

        boolean newPattern = false;

        for (int x = 0; x < accountedFor.length; x++) {
            if (!accountedFor[x]) {
                newPattern = true;
                this.addToCraftingList(this.patterns.getStackInSlot(x));
            }
        }
        try {
            this.gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.gridProxy.getNode()));
        } catch (GridAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean hasWorkToDo() {

        if (hasItemsToSend()) {
            return true;
        }


        return hasItemsToSendFacing();
    }

    public void notifyNeighbors() {
        if (this.gridProxy.isActive()) {
            try {
                this.gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.gridProxy.getNode()));
                this.gridProxy.getTick().wakeDevice(this.gridProxy.getNode());
            } catch (final GridAccessException e) {
                // :P
            }
        }

        final TileEntity te = this.iHost.getTileEntity();
        if (te != null) {
            Platform.notifyBlocksOfNeighbors(te.getWorld(), te.getPos());
        }
    }

    private void addToCraftingList(final ItemStack is) {
        if (is.isEmpty()) {
            return;
        }

        if (is.getItem() instanceof final ICraftingPatternItem cpi) {
            final ICraftingPatternDetails details = cpi.getPatternForItem(is, this.iHost.getTileEntity().getWorld());

            if (details != null) {
                if (this.craftingList == null) {
                    this.craftingList = new ArrayList<>();
                }

                this.craftingList.add(details);
            }
        }
    }

    private boolean hasItemsToSend() {
        return this.waitingToSend != null && !this.waitingToSend.isEmpty();
    }

    private boolean hasItemsToSendFacing() {
        if (waitingToSendFacing != null) {
            for (EnumFacing enumFacing : waitingToSendFacing.keySet()) {
                if (!waitingToSendFacing.get(enumFacing).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        final IAEItemStack out = this.destination.injectItems(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack), Actionable.SIMULATE, null);
        if (out == null) {
            return true;
        }
        return out.getStackSize() != stack.getCount();
    }

    public IItemHandler getPatterns() {
        return this.patterns;
    }

    public void gridChanged() {
        try {
            this.items.setInternal(this.gridProxy.getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)));
            this.fluids.setInternal(this.gridProxy.getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)));
        } catch (final GridAccessException gae) {
            this.items.setInternal(new NullInventory<IAEItemStack>());
            this.fluids.setInternal(new NullInventory<IAEFluidStack>());
        }

        this.notifyNeighbors();
    }

    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this.iHost.getTileEntity());
    }

    public IItemHandler getInternalInventory() {
        return this.storage;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 40, !this.hasWorkToDo(), true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (!this.gridProxy.isActive()) {
            return TickRateModulation.SLEEP;
        }

        //Previous version might have items saved in this list
        //recover them
        if (this.hasItemsToSend()) {
            this.pushItemsOut(this.iHost.getTargets());
        }

        if (hasItemsToSendFacing()) {
            for (EnumFacing enumFacing : waitingToSendFacing.keySet()) {
                this.pushItemsOut(enumFacing);
            }
        }

        return this.hasWorkToDo() || this.pushOutStoredItems() ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    private boolean pushOutStoredItems() {
        IMEMonitor<IAEItemStack> storage = this.iHost.getActionableNode().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
        boolean hasChanged = false;
        for (int i = 0; i < this.storage.getSlots(); i++) {
            ItemStack item = this.storage.getStackInSlot(i);
            if (!item.isEmpty()) {

                IAEItemStack iaeItemStack = AEItemStack.fromItemStack(this.storage.getStackInSlot(i));
                IAEItemStack overflow = storage.injectItems(iaeItemStack, Actionable.SIMULATE, this.actionSource);

                if (overflow == null) {
                    storage.injectItems(iaeItemStack, Actionable.MODULATE, this.actionSource);
                    this.storage.setStackInSlot(i, ItemStack.EMPTY);
                    hasChanged = true;
                }
            }
        }

        return hasChanged;

    }

    private void pushItemsOut(final EnumSet<EnumFacing> possibleDirections) {
        if (!this.hasItemsToSend()) {
            return;
        }

        final TileEntity tile = this.iHost.getTileEntity();
        final World w = tile.getWorld();

        final Iterator<ItemStack> items = this.waitingToSend.iterator();
        while (items.hasNext()) {
            ItemStack itemToSend = items.next();

            for (final EnumFacing s : possibleDirections) {
                final TileEntity te = w.getTileEntity(tile.getPos().offset(s));
                if (te == null) {
                    continue;
                }

                if (te instanceof IInterfaceHost
                        || te instanceof ICrazyAEInterfaceHost
                        || (te instanceof TileCableBus && ((TileCableBus) te).getPart(s.getOpposite()) instanceof PartInterface
                )) {
                    continue;
                }

                final InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
                if (ad != null) {
                    final ItemStack result = ad.addItems(itemToSend);

                    if (result.isEmpty()) {
                        itemToSend = ItemStack.EMPTY;
                    } else {
                        itemToSend.setCount(result.getCount());
                        itemToSend.setTagCompound(result.getTagCompound());
                    }

                    if (itemToSend.isEmpty()) {
                        break;
                    }
                }
            }

            if (itemToSend.isEmpty()) {
                items.remove();
            }
        }

        if (this.waitingToSend.isEmpty()) {
            this.waitingToSend = null;
        }
    }

    private void pushItemsOut(final EnumFacing s) {
        if (!this.waitingToSendFacing.containsKey(s) || (this.waitingToSendFacing.containsKey(s) && this.waitingToSendFacing.get(s).isEmpty())) {
            return;
        }

        final TileEntity tile = this.iHost.getTileEntity();
        final World w = tile.getWorld();

        final TileEntity te = w.getTileEntity(tile.getPos().offset(s));
        if (te == null) {
            return;
        }

        final InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());

        final Iterator<ItemStack> i = this.waitingToSendFacing.get(s).iterator();
        while (i.hasNext()) {
            ItemStack whatToSend = i.next();
            if (ad != null) {
                final ItemStack result = ad.addItems(whatToSend);
                if (!result.isEmpty()) {
                    whatToSend.setCount(result.getCount());
                    whatToSend.setTagCompound(result.getTagCompound());
                } else {
                    i.remove();
                }
            }
        }

        if (this.waitingToSendFacing.get(s).isEmpty()) {
            this.waitingToSendFacing.remove(s);
        }
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        if (this.upgrades == null) {
            return 0;
        }
        return this.upgrades.getInstalledUpgrades(u);
    }

    @Override
    public TileEntity getTile() {
        return (TileEntity) (this.iHost instanceof TileEntity ? this.iHost : null);
    }

    @Override
    public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
        if (channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
            return (IMEMonitor<T>) this.items;
        } else if (channel == AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)) {
            return (IMEMonitor<T>) this.fluids;
        }

        return null;
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "storage" -> this.storage;
            case "patterns" -> this.patterns;
            case "upgrades" -> this.upgrades;
            default -> null;
        };

    }

    public IItemHandler getStorage() {
        return this.storage;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.cm;
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
        this.iHost.saveChanges();
    }

    public IStorageMonitorable getMonitorable(final IActionSource src, final IStorageMonitorable myInterface) {
        if (Platform.canAccess(this.gridProxy, src)) {
            return myInterface;
        }

        final PatternsInterfaceDuality di = this;

        return new IStorageMonitorable() {

            @Override
            public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
                if (channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
                    return (IMEMonitor<T>) new InterfaceInventory(di);
                }
                return null;
            }
        };
    }

    private boolean invIsBlocked(InventoryAdaptor inv) {
        return (inv.containsItems());
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (this.hasItemsToSend() || this.hasItemsToSendFacing() || !this.gridProxy.isActive() || !this.craftingList.contains(patternDetails)) {
            return false;
        }

        final TileEntity tile = this.iHost.getTileEntity();
        final World w = tile.getWorld();

        if (this.visitedFaces.isEmpty()) {
            this.visitedFaces = this.iHost.getTargets();
        }

        for (final EnumFacing s : visitedFaces) {
            final TileEntity te = w.getTileEntity(tile.getPos().offset(s));

            if (te instanceof final ICraftingMachine cm) {
                if (cm.acceptsPlans()) {
                    this.visitedFaces.remove(s);
                    if (cm.pushPattern(patternDetails, table, s.getOpposite())) {
                        return true;
                    }
                    continue;
                }
            }

            InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
            if (ad != null) {
                if (this.isBlocking()) {
                    IPhantomTile phantomTE;
                    if (this.isActuallyAdditionsLoaded && te instanceof IPhantomTile) {
                        phantomTE = ((IPhantomTile) te);
                        if (phantomTE.hasBoundPosition()) {
                            TileEntity phantom = w.getTileEntity(phantomTE.getBoundPosition());
                            if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(phantomTE.getBoundPosition()).getBlock().getRegistryName().getNamespace())) {
                                if (isCustomInvBlocking(phantom, s)) {
                                    visitedFaces.remove(s);
                                    continue;
                                }
                            }
                        }
                    } else if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(tile.getPos().offset(s)).getBlock().getRegistryName().getNamespace())) {
                        if (isCustomInvBlocking(te, s)) {
                            visitedFaces.remove(s);
                            continue;
                        }
                    } else if (invIsBlocked(ad)) {
                        visitedFaces.remove(s);
                        continue;
                    }
                }

                if (this.acceptsItems(ad, table)) {
                    visitedFaces.clear();
                    for (int x = 0; x < table.getSizeInventory(); x++) {
                        final ItemStack is = table.getStackInSlot(x);
                        if (!is.isEmpty()) {
                            addToSendListFacing(is, s);
                        }
                    }
                    pushItemsOut(s);
                    return true;
                }
            }
            visitedFaces.remove(s);
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        boolean busy = false;

        if (this.hasItemsToSend() || hasItemsToSendFacing()) {
            return true;
        }

        if (this.isBlocking()) {
            final EnumSet<EnumFacing> possibleDirections = this.iHost.getTargets();
            final TileEntity tile = this.iHost.getTileEntity();
            final World w = tile.getWorld();

            boolean allAreBusy = true;

            for (final EnumFacing s : possibleDirections) {
                final TileEntity te = w.getTileEntity(tile.getPos().offset(s));

                final InventoryAdaptor ad = InventoryAdaptor.getAdaptor(te, s.getOpposite());
                if (ad != null) {
                    if (this.isActuallyAdditionsLoaded && Platform.GTLoaded && te instanceof IPhantomTile phantomTE) {
                        if (phantomTE.hasBoundPosition()) {
                            TileEntity phantom = w.getTileEntity(phantomTE.getBoundPosition());
                            if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(phantomTE.getBoundPosition()).getBlock().getRegistryName().getNamespace())) {
                                if (!isCustomInvBlocking(phantom, s)) {
                                    allAreBusy = false;
                                    break;
                                }
                            }
                        }
                    } else if (NonBlockingItems.INSTANCE.getMap().containsKey(w.getBlockState(tile.getPos().offset(s)).getBlock().getRegistryName().getNamespace())) {
                        if (!isCustomInvBlocking(te, s)) {
                            allAreBusy = false;
                            break;
                        }
                    } else {
                        if (!invIsBlocked(ad)) {
                            allAreBusy = false;
                            break;
                        }
                    }
                }
            }
            busy = allAreBusy;
        }
        return busy;
    }

    boolean isCustomInvBlocking(TileEntity te, EnumFacing s) {
        BlockingInventoryAdaptor blockingInventoryAdaptor = BlockingInventoryAdaptor.getAdaptor(te, s.getOpposite());
        return invIsCustomBlocking(blockingInventoryAdaptor);
    }

    private boolean isBlocking() {
        return this.cm.getSetting(Settings.BLOCK) == YesNo.YES;
    }

    private boolean acceptsItems(final InventoryAdaptor ad, final InventoryCrafting table) {
        for (int x = 0; x < table.getSizeInventory(); x++) {
            final ItemStack is = table.getStackInSlot(x);
            if (is.isEmpty()) {
                continue;
            }

            if (!ad.simulateAdd(is).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        if (this.gridProxy.isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                details.setPriority(this.priority);
                craftingTracker.addCraftingOption(this, details);
            }
        }
    }

    public void addDrops(final List<ItemStack> drops) {
        if (this.waitingToSend != null) {
            for (final ItemStack is : this.waitingToSend) {
                if (!is.isEmpty()) {
                    drops.add(is);
                }
            }
        }

        if (this.waitingToSendFacing != null) {
            for (List<ItemStack> itemList : waitingToSendFacing.values()) {
                for (final ItemStack is : itemList) {
                    if (!is.isEmpty()) {
                        drops.add(is);
                    }
                }
            }
        }

        for (final ItemStack is : this.upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.storage) {
            if (!is.isEmpty()) {
                int maxStackSize = is.getMaxStackSize();
                while (is.getCount() > maxStackSize) {
                    ItemStack portionedStack = is.copy();
                    portionedStack.setCount(maxStackSize);
                    is.shrink(maxStackSize);
                    drops.add(portionedStack);
                }
                drops.add(is);
            }
        }

        for (final ItemStack is : this.patterns) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    public IUpgradesInfoProvider getHost() {
        if (this.getPart() != null) {
            return (IUpgradesInfoProvider) this.getPart();
        }
        if (this.getTile() instanceof IUpgradeableHost) {
            return (IUpgradesInfoProvider) this.getTile();
        }
        return null;
    }

    public String getTermName() {
        final TileEntity hostTile = this.iHost.getTileEntity();
        final World hostWorld = hostTile.getWorld();

        if (((ICustomNameObject) this.iHost).hasCustomInventoryName()) {
            return ((ICustomNameObject) this.iHost).getCustomInventoryName();
        }

        final EnumSet<EnumFacing> possibleDirections = this.iHost.getTargets();
        for (final EnumFacing direction : possibleDirections) {
            final BlockPos targ = hostTile.getPos().offset(direction);
            final TileEntity directedTile = hostWorld.getTileEntity(targ);

            if (directedTile == null) {
                continue;
            }

            final InventoryAdaptor adaptor = InventoryAdaptor.getAdaptor(directedTile, direction.getOpposite());
            if (directedTile instanceof ICraftingMachine || adaptor != null) {
                if (adaptor != null && !adaptor.hasSlots()) {
                    continue;
                }

                final IBlockState directedBlockState = hostWorld.getBlockState(targ);
                final Block directedBlock = directedBlockState.getBlock();
                ItemStack what = new ItemStack(directedBlock, 1, directedBlock.getMetaFromState(directedBlockState));

                if (Platform.GTLoaded && directedBlock instanceof BlockMachine) {
                    MetaTileEntity metaTileEntity = Platform.getMetaTileEntity(directedTile.getWorld(), directedTile.getPos());
                    if (metaTileEntity != null) {
                        return metaTileEntity.getMetaFullName();
                    }
                }

                try {
                    Vec3d from = new Vec3d(hostTile.getPos().getX() + 0.5, hostTile.getPos().getY() + 0.5, hostTile.getPos().getZ() + 0.5);
                    from = from.add(direction.getXOffset() * 0.501, direction.getYOffset() * 0.501, direction.getZOffset() * 0.501);
                    final Vec3d to = from.add(direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
                    final RayTraceResult mop = hostWorld.rayTraceBlocks(from, to, true);
                    if (mop != null && !BAD_BLOCKS.contains(directedBlock)) {
                        if (mop.getBlockPos().equals(directedTile.getPos())) {
                            final ItemStack g = directedBlock.getPickBlock(directedBlockState, mop, hostWorld, directedTile.getPos(), null);
                            if (!g.isEmpty()) {
                                what = g;
                            }
                        }
                    }
                } catch (final Throwable t) {
                    BAD_BLOCKS.add(directedBlock);
                }

                if (what.getItem() != Items.AIR) {
                    return what.getItem().getItemStackDisplayName(what);
                }

                final Item item = Item.getItemFromBlock(directedBlock);
                if (item == Items.AIR) {
                    return directedBlock.getTranslationKey();
                }
            }
        }

        return GuiText.Nothing.getLocal();
    }

    public long getSortValue() {
        final TileEntity te = this.iHost.getTileEntity();
        return ((long) te.getPos().getZ() << 24) ^ ((long) te.getPos().getX() << 8) ^ te.getPos().getY();
    }

    private IPart getPart() {
        return (IPart) (this.iHost instanceof IPart ? this.iHost : null);
    }

    public void initialize() {
        this.updateCraftingList();
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(final int newValue) {
        this.priority = newValue;
        this.iHost.saveChanges();

        try {
            this.gridProxy.getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.gridProxy.getNode()));
        } catch (final GridAccessException e) {
            // :P
        }
    }

    public boolean hasCapability(Capability<?> capabilityClass, EnumFacing facing) {
        return capabilityClass == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capabilityClass == Capabilities.STORAGE_MONITORABLE_ACCESSOR;
    }

    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capabilityClass, EnumFacing facing) {
        if (capabilityClass == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.storage;
        } else if (capabilityClass == Capabilities.STORAGE_MONITORABLE_ACCESSOR) {
            return (T) this.accessor;
        }
        return null;
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().patternsInterface();
    }

    private class InterfaceRequestContext implements Comparable<Integer> {
        @Override
        public int compareTo(Integer o) {
            return Integer.compare(PatternsInterfaceDuality.this.priority, o);
        }
    }

    private class InterfaceInventory extends MEMonitorIInventory {
        public InterfaceInventory(final PatternsInterfaceDuality tileInterface) {
            super(new AdaptorItemHandler(tileInterface.storage));
        }

        @Override
        public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final IActionSource src) {
            final Optional<InterfaceRequestContext> context = src.context(InterfaceRequestContext.class);
            final boolean isInterface = context.isPresent();

            if (isInterface) {
                return input;
            }

            return super.injectItems(input, type, src);
        }

        @Override
        public IAEItemStack extractItems(final IAEItemStack request, final Actionable type, final IActionSource src) {
            final Optional<InterfaceRequestContext> context = src.context(InterfaceRequestContext.class);
            final boolean hasLowerOrEqualPriority = context.map(c -> c.compareTo(PatternsInterfaceDuality.this.priority) <= 0).orElse(false);

            if (hasLowerOrEqualPriority) {
                return null;
            }

            return super.extractItems(request, type, src);
        }
    }

    private class Accessor implements IStorageMonitorableAccessor {
        @Nullable
        @Override
        public IStorageMonitorable getInventory(IActionSource src) {
            return PatternsInterfaceDuality.this.getMonitorable(src, PatternsInterfaceDuality.this);
        }

    }

}