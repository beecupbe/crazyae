package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeInventory;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class TileBigCrystalCharger extends AENetworkTile implements IConfigManagerHost, IUpgradeableHost, IAEAppEngInventory, IGridTickable {
    private static final int NUMBER_OF_UPGRADE_SLOTS = 5;

    private final UpgradeInventory upgrades;
    private final IConfigManager manager;

    private final AppEngInternalInventory inputInv = new
            AppEngInternalInventory(this, 18, 64);
    private final AppEngInternalInventory outputInv = new
            AppEngInternalInventory(this, 18, 64);

    private int progressPerTick = 1;
    private int progress = 0;
    private int outputItemsPerJob = 0;

    private boolean isPowered = false;

    private final IActionSource actionSource = new MachineSource(this);


    public TileBigCrystalCharger() {
        final Block charger = CrazyAE.definitions().blocks().bigCrystalCharger().maybeBlock().orElse(null);
        Preconditions.checkNotNull(charger);

        this.getProxy().setIdlePowerUsage(16.0);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setVisualRepresentation(CrazyAE.definitions().blocks()
                .bigCrystalCharger().maybeStack(1).orElse(ItemStack.EMPTY));

        this.manager = new ConfigManager(this);
        this.manager.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.manager.registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.manager.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.manager.registerSetting(Settings.FULLNESS_MODE, FullnessMode.EMPTY);
        this.manager.registerSetting(Settings.OPERATION_MODE, OperationMode.EMPTY);

        this.upgrades = new BlockUpgradeInventory(charger, this, NUMBER_OF_UPGRADE_SLOTS);
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    @MENetworkEventSubscribe
    public void onPowerEvent(final MENetworkPowerStatusChange p) {
        try {
            this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
        } catch (GridAccessException ignored) {}
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
        this.upgrades.writeToNBT(data, "upgrades");
        this.inputInv.writeToNBT(data, "acceleratorsInv");
        this.outputInv.writeToNBT(data, "storageInv");
        this.manager.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.inputInv.readFromNBT(data, "acceleratorsInv");
        this.outputInv.readFromNBT(data, "storageInv");
        this.manager.readFromNBT(data);
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.COVERED;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "input" -> this.inputInv;
            case "output" -> this.outputInv;
            case "upgrades" -> this.upgrades;
            default -> null;
        };
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {}

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.inputInv == inv && (!removed.isEmpty() || !added.isEmpty())) {
            this.checkTasks();
        }
    }

    private void clearProgress() {
        this.progress = 0;
        this.progressPerTick = 0;
    }

    private void checkTasks() {
        try {
            if (this.hasWork()) {
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
            } else {
                this.clearProgress();
                this.getProxy().getTick().sleepDevice(this.getProxy().getNode());
            }
        } catch (final GridAccessException e) {
            // :P
        }
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

        for (final ItemStack is : this.inputInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.outputInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    public int getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
        return ((ICrazyAEUpgradeInventory) this.upgrades).getInstalledUpgrades(u);
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode iGridNode) {
        return new TickingRequest(1, 1, !this.hasWork(), false);
    }

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode iGridNode, int i) {
        try {
            if (this.hasWork() && this.getProxy().getEnergy().extractAEPower(16, Actionable.MODULATE, PowerMultiplier.CONFIG) > 0.0001) {
                this.checkUpgrades();
                this.progress += this.progressPerTick;

                if (this.progress >= 100) {
                    this.progress = 0;
                    int itemsLeft = this.outputItemsPerJob;
                    for (int j = 0; j < this.inputInv.getSlots(); j++) {
                        if (this.inputInv.getStackInSlot(j).getCount() > 0) {
                            if (!AEApi.instance().definitions().materials().certusQuartzCrystal().isSameAs(this.inputInv.getStackInSlot(j))) {
                                this.inputInv.setStackInSlot(j, ItemStack.EMPTY);
                                continue;
                            }

                            int amt = Math.min(Math.min(itemsLeft, 64 - this.outputInv.getStackInSlot(j).getCount()), this.inputInv.getStackInSlot(j).getCount());
                            this.outputInv.insertItem(j, AEApi.instance().definitions().materials().certusQuartzCrystalCharged().maybeStack(amt).orElse(ItemStack.EMPTY), false);
                            this.inputInv.extractItem(j, amt, false);
                            itemsLeft -= amt;
                            if (itemsLeft <= 0) {
                                break;
                            }
                        }
                    }

                    this.pushOut();
                }
                return TickRateModulation.SAME;
            }
        } catch (GridAccessException e) {
            // :c
        }

        return TickRateModulation.SLEEP;
    }

    private void pushOut() {
        if (!ItemHandlerUtil.isEmpty(this.outputInv)) {
            try {
                final IEnergyGrid grid = this.getProxy().getEnergy();
                IMEMonitor<IAEItemStack> storage = this.getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                for (int j = 0; j < this.outputInv.getSlots(); j++) {
                    if (this.outputInv.getStackInSlot(j).getCount() > 0) {
                        int amt = this.outputInv.getStackInSlot(j).getCount();
                        AEItemStack copy = AEItemStack.fromItemStack(this.outputInv.getStackInSlot(j));
                        if (copy != null) {
                            if (Platform.poweredInsert(grid, storage, copy, this.actionSource) != copy) {
                                this.outputInv.extractItem(j, amt, false);
                            }
                        }
                    }
                }
            } catch (GridAccessException e) {
                //:(
            }
        }
    }

    private void checkUpgrades() {
        this.progressPerTick = 0;
        this.outputItemsPerJob = 0;
        switch (this.getInstalledUpgrades(Upgrades.SPEED)) {
            case 1:
                this.progressPerTick++;
                break;
            case 2:
                this.progressPerTick += 3;
                break;
            case 3:
                this.progressPerTick += 5;
                break;
            case 4:
                this.progressPerTick += 8;
                break;
            case 5:
                this.progressPerTick += 12;
                break;
        }

        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.IMPROVED_SPEED)) {
            case 1:
                this.progressPerTick += 10;
                break;
            case 2:
                this.progressPerTick += 16;
                break;
            case 3:
                this.progressPerTick += 25;
                break;
            case 4:
                this.progressPerTick += 36;
                break;
            case 5:
                this.progressPerTick += 50;
                break;
        }

        if (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            this.progressPerTick = 100;
        }

        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1:
                this.outputItemsPerJob += 48;
                break;
            case 2:
                this.outputItemsPerJob += 160;
                break;
            case 3:
                this.outputItemsPerJob += 384;
                break;
            case 4:
                this.outputItemsPerJob += 640;
                break;
            case 5:
                this.outputItemsPerJob += 1152;
                break;
        }

        if (this.outputItemsPerJob == 0) this.outputItemsPerJob = 1;
        if (this.progressPerTick == 0) this.progressPerTick = 1;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inputInv;
        }
        return super.getCapability(capability, facing);
    }

    public int getProgress() {
        return this.progress;
    }

    private boolean hasWork() {
        return !ItemHandlerUtil.isEmpty(this.inputInv);
    }
}
