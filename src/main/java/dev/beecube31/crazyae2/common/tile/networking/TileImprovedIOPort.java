package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.core.settings.TickRates;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.AdaptorItemHandler;
import appeng.util.inv.InvOperation;
import appeng.util.inv.WrapperChainedItemHandler;
import appeng.util.inv.WrapperFilteredItemHandler;
import appeng.util.inv.filter.AEItemFilters;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvOCTile;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TileImprovedIOPort extends CrazyAENetworkInvOCTile implements IUpgradesInfoProvider, IConfigManagerHost, IGridTickable {
    private static final int NUMBER_OF_CELL_SLOTS = 12;
    private static final int NUMBER_OF_UPGRADE_SLOTS = 3;

    private final ConfigManager manager;

    private final CrazyAEInternalInv inputCells = new CrazyAEInternalInv(this, NUMBER_OF_CELL_SLOTS, 1).setItemFilter(RestrictedSlot.PlaceableItemType.STORAGE_CELLS.associatedFilter);
    private final CrazyAEInternalInv outputCells = new CrazyAEInternalInv(this, NUMBER_OF_CELL_SLOTS, 1).setItemFilter(RestrictedSlot.PlaceableItemType.STORAGE_CELLS.inputLockedFilter());
    private final IItemHandler combinedInventory = new WrapperChainedItemHandler(this.inputCells, this.outputCells);

    private final IItemHandler inputCellsExt = new WrapperFilteredItemHandler(this.inputCells, AEItemFilters.INSERT_ONLY);
    private final IItemHandler outputCellsExt = new WrapperFilteredItemHandler(this.outputCells, AEItemFilters.EXTRACT_ONLY);

    private final CrazyAEBlockUpgradeInv upgrades;
    private final IActionSource mySrc;
    private YesNo lastRedstoneState;
    private ItemStack currentCell;
    private Map<IStorageChannel<?>, IMEInventory<?>> cachedInventories;
    private boolean isActive = false;

    public TileImprovedIOPort() {
        this.getProxy().setIdlePowerUsage(64.0);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.manager = new ConfigManager(this);
        this.manager.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.manager.registerSetting(Settings.FULLNESS_MODE, FullnessMode.EMPTY);
        this.manager.registerSetting(Settings.OPERATION_MODE, OperationMode.EMPTY);
        this.mySrc = new MachineSource(this);
        this.lastRedstoneState = YesNo.UNDECIDED;

        final Block ioPortBlock = CrazyAE.definitions().blocks().ioPortImp().maybeBlock().orElse(null);
        Preconditions.checkNotNull(ioPortBlock);
        this.upgrades = new CrazyAEBlockUpgradeInv(ioPortBlock, this, NUMBER_OF_UPGRADE_SLOTS);
    }

    @MENetworkEventSubscribe
    public void onPower(final MENetworkPowerStatusChange ch) {
        this.markForUpdate();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        this.upgrades.writeToNBT(data, "upgrades");
        this.manager.writeToNBT(data);
        data.setInteger("lastRedstoneState", this.lastRedstoneState.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.manager.readFromNBT(data);
        if (data.hasKey("lastRedstoneState")) {
            this.lastRedstoneState = YesNo.values()[data.getInteger("lastRedstoneState")];
        }
    }

    @Override
    protected boolean readFromStream(ByteBuf data) throws IOException {
        boolean c = super.readFromStream(data);

        final boolean oldIsActive = this.isActive;
        this.isActive = data.readBoolean();
        return oldIsActive != this.isActive || c;
    }

    @Override
    protected void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeBoolean(this.isActive());
    }

    public boolean isActive() {
        if (Platform.isServer()) {
            try {
                return this.getProxy().getEnergy().isNetworkPowered();
            } catch (GridAccessException e) {
                return false;
            }
        }
        return this.isActive;
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    private void updateTask() {
        try {
            if (this.hasWork()) {
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
            } else {
                this.getProxy().getTick().sleepDevice(this.getProxy().getNode());
            }
        } catch (final GridAccessException e) {
            // :P
        }
    }

    public void updateRedstoneState() {
        final YesNo currentState = this.world.getRedstonePowerFromNeighbors(this.pos) != 0 ? YesNo.YES : YesNo.NO;
        if (this.lastRedstoneState != currentState) {
            this.lastRedstoneState = currentState;
            this.updateTask();
            if (currentState == YesNo.YES) {
                if (this.manager.getSetting(Settings.REDSTONE_CONTROLLED) == RedstoneMode.SIGNAL_PULSE) {
                    this.doWork();
                }
            }
        }
    }

    private boolean getRedstoneState() {
        if (this.lastRedstoneState == YesNo.UNDECIDED) {
            this.updateRedstoneState();
        }

        return this.lastRedstoneState == YesNo.YES;
    }

    private boolean isEnabled() {
        if (this.getInstalledUpgrades(Upgrades.REDSTONE) == 0) {
            return true;
        }

        final RedstoneMode rs = (RedstoneMode) this.manager.getSetting(Settings.REDSTONE_CONTROLLED);
        switch (rs) {
            case IGNORE:
                return true;

            case HIGH_SIGNAL:
                return this.getRedstoneState();

            case LOW_SIGNAL:
                return !this.getRedstoneState();

            case SIGNAL_PULSE:
            default:
                return false;
        }
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        if (name.equals("upgrades")) {
            return this.upgrades;
        }

        if (name.equals("cells")) {
            return this.combinedInventory;
        }

        return null;
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
        this.updateTask();
    }

    private boolean hasWork() {
        if (this.isEnabled()) {
            return !ItemHandlerUtil.isEmpty(this.inputCells);
        }

        return false;
    }

    @Override
    public IItemHandler getInternalInventory() {
        return this.combinedInventory;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.inputCells == inv) {
            this.updateTask();
        }
    }

    @Override
    protected IItemHandler getItemHandlerForSide(final EnumFacing facing) {
        if (facing == this.getUp() || facing == this.getUp().getOpposite()) {
            return this.inputCellsExt;
        } else {
            return this.outputCellsExt;
        }
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(TickRates.IOPort.getMin(), TickRates.IOPort.getMax(), !this.hasWork(), false);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (!this.getProxy().isActive()) {
            return TickRateModulation.IDLE;
        }
        return this.doWork();
    }

    public int getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    public TickRateModulation doWork() {
        TickRateModulation ret = TickRateModulation.SLEEP;
        long itemsToMove = 2048;
        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1:
                itemsToMove *= 32;
                break;
            case 2:
                itemsToMove *= 512;
                break;
            case 3:
                itemsToMove *= 4096;
                break;
        }

        try {
            final IEnergySource energy = this.getProxy().getEnergy();
            for (int x = 0; x < NUMBER_OF_CELL_SLOTS; x++) {
                final ItemStack is = this.inputCells.getStackInSlot(x);
                if (!is.isEmpty()) {
                    boolean shouldMove = true;

                    for (IStorageChannel<? extends IAEStack<?>> c : AEApi.instance().storage().storageChannels()) {
                        if (itemsToMove > 0) {
                            final IMEMonitor<? extends IAEStack<?>> network = this.getProxy().getStorage().getInventory(c);
                            final IMEInventory<?> inv = this.getInv(is, c);

                            if (inv == null) {
                                continue;
                            }

                            if (this.manager.getSetting(Settings.OPERATION_MODE) == OperationMode.EMPTY) {
                                itemsToMove = this.transferContents(energy, inv, network, itemsToMove, c);
                            } else {
                                itemsToMove = this.transferContents(energy, network, inv, itemsToMove, c);
                            }

                            shouldMove &= this.shouldMove(inv);

                            if (itemsToMove > 0) {
                                ret = TickRateModulation.IDLE;
                            } else {
                                ret = TickRateModulation.URGENT;
                            }
                        }
                    }

                    if (itemsToMove > 0 && shouldMove && this.moveSlot(x)) {
                        ret = TickRateModulation.URGENT;
                    } else {
                        ret = TickRateModulation.URGENT;
                    }

                }
            }
        } catch (final GridAccessException e) {
            ret = TickRateModulation.IDLE;
        }

        return ret;
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    private IMEInventory<?> getInv(final ItemStack is, final IStorageChannel<?> chan) {
        if (this.currentCell != is) {
            this.currentCell = is;
            this.cachedInventories = new IdentityHashMap<>();

            for (IStorageChannel<? extends IAEStack<?>> c : AEApi.instance().storage().storageChannels()) {
                this.cachedInventories.put(c, AEApi.instance().registries().cell().getCellInventory(is, null, c));
            }
        }

        return this.cachedInventories.get(chan);
    }

    private long transferContents(final IEnergySource energy, final IMEInventory src, final IMEInventory destination, long itemsToMove, final IStorageChannel chan) {
        final IItemList<? extends IAEStack> myList;
        if (src instanceof IMEMonitor) {
            myList = ((IMEMonitor) src).getStorageList();
        } else {
            myList = src.getAvailableItems(src.getChannel().createList());
        }

        itemsToMove *= chan.transferFactor();

        boolean didStuff;

        do {
            didStuff = false;

            for (final IAEStack s : myList) {
                final long totalStackSize = s.getStackSize();
                if (totalStackSize > 0) {
                    final IAEStack stack = destination.injectItems(s, Actionable.SIMULATE, this.mySrc);

                    long possible = 0;
                    if (stack == null) {
                        possible = totalStackSize;
                    } else {
                        possible = totalStackSize - stack.getStackSize();
                    }

                    if (possible > 0) {
                        IAEStack injectable = s.copy();

                        possible = Math.min(possible, itemsToMove);
                        injectable.setStackSize(possible);

                        final IAEStack extracted = src.extractItems(injectable, Actionable.MODULATE, this.mySrc);
                        if (extracted != null) {
                            possible = extracted.getStackSize();
                            extracted.setCraftable(false);
                            final IAEStack failed = Platform.poweredInsert(energy, destination, extracted, this.mySrc);

                            if (failed != null) {
                                possible -= failed.getStackSize();
                                src.injectItems(failed, Actionable.MODULATE, this.mySrc);
                            }

                            if (possible > 0) {
                                itemsToMove -= possible;
                                this.addCompletedOperations(possible);
                                didStuff = true;
                            }

                            break;
                        }
                    }
                }
            }
        }
        while (itemsToMove > 0 && didStuff);

        return itemsToMove / chan.transferFactor();
    }

    private boolean shouldMove(final IMEInventory<?> inv) {
        final FullnessMode fm = (FullnessMode) this.manager.getSetting(Settings.FULLNESS_MODE);

        if (inv != null) {
            return this.matches(fm, inv);
        }

        return true;
    }

    private boolean moveSlot(final int x) {
        final InventoryAdaptor ad = new AdaptorItemHandler(this.outputCells);
        if (ad.addItems(this.inputCells.getStackInSlot(x)).isEmpty()) {
            this.inputCells.setStackInSlot(x, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    private boolean matches(final FullnessMode fm, final IMEInventory src) {
        if (fm == FullnessMode.HALF) {
            return true;
        }

        final IItemList<? extends IAEStack> myList;

        if (src instanceof IMEMonitor) {
            myList = ((IMEMonitor) src).getStorageList();
        } else {
            myList = src.getAvailableItems(src.getChannel().createList());
        }

        if (fm == FullnessMode.EMPTY) {
            return myList.isEmpty();
        }

        final IAEStack test = myList.getFirstItem();
        if (test != null) {
            IAEStack testCopy = test.copy();
            testCopy.setStackSize(1);
            return src.injectItems(testCopy, Actionable.SIMULATE, this.mySrc) != null;
        }
        return false;
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        super.getDrops(w, pos, drops);
        for (int upgradeIndex = 0; upgradeIndex < this.upgrades.getSlots(); upgradeIndex++) {
            final ItemStack stackInSlot = this.upgrades.getStackInSlot(upgradeIndex);

            if (!stackInSlot.isEmpty()) {
                drops.add(stackInSlot);
            }
        }
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().ioPortImp();
    }
}
