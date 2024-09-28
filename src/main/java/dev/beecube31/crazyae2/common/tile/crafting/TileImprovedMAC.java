package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
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
import appeng.helpers.ItemStackHelper;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeInventory;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.io.IOException;
import java.util.*;

public class TileImprovedMAC extends AENetworkInvTile implements IUpgradeableHost, IConfigManagerHost, IGridTickable, ICraftingMedium, ICraftingProvider {

    private final int maxQueueSize = 320;
    private int itemsToSendPerTick = 8;

    private final AppEngInternalInventory patternsInv = new AppEngInternalInventory(this, 45, 1);
    private final IConfigManager settings;
    private final UpgradeInventory upgrades;
    private boolean isPowered = false;
    private boolean cached = false;
    private final IActionSource actionSource = new MachineSource(this);

    private int priority = 1;
    private List<ICraftingPatternDetails> craftingList = null;
    private List<IAEItemStack> itemsToSend = new ArrayList<>();

    public TileImprovedMAC() {
        final Block assembler = CrazyAE.definitions().blocks().improvedMolecularAssembler().maybeBlock().get();

        this.settings = new ConfigManager(this);
        this.settings.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getProxy().setIdlePowerUsage(4.0);
        this.upgrades = new BlockUpgradeInventory(assembler, this, this.getUpgradeSlots());

    }

    private int getUpgradeSlots() {
        return 5;
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (this.cached && this.itemsToSend.size() < this.maxQueueSize) {
            return this.dispatchJob(table, patternDetails);
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        return this.itemsToSend.size() >= this.maxQueueSize;
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.upgrades.getInstalledUpgrades(u);
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
        NBTTagList nbtTagList = new NBTTagList();

        for (IAEItemStack i : this.itemsToSend) {
            ItemStack is = i.createItemStack();
            if (!is.isEmpty()) {
                NBTTagCompound itemTag = ItemStackHelper.stackToNBT(is);
                nbtTagList.appendTag(itemTag);
            }
        }
        data.setTag("itemsToSend", nbtTagList);

        this.patternsInv.writeToNBT(data, "patterns");
        this.upgrades.writeToNBT(data, "upgrades");
        this.settings.writeToNBT(data);
        data.setInteger("priority", this.priority);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("itemsToSend", 9)) {
            NBTTagList tagList = data.getTagList("itemsToSend", 10);

            this.itemsToSend.clear();

            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                this.itemsToSend.add(AEItemStack.fromItemStack(ItemStackHelper.stackFromNBT(itemTags)));
            }
        }

        this.patternsInv.readFromNBT(data, "patterns");
        this.priority = data.getInteger("priority");
        this.upgrades.readFromNBT(data, "upgrades");
        this.settings.readFromNBT(data);
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
    public IConfigManager getConfigManager() {
        return this.settings;
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "upgrades" -> this.upgrades;
            case "patterns" -> this.patternsInv;
            default -> null;
        };
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {}

    @Override
    public IItemHandler getInternalInventory() {
        return this.patternsInv;
    }


    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (inv == this.patternsInv && (!removed.isEmpty() || !added.isEmpty())) {
            if (this.getProxy().isActive()) {
                this.cached = false;
                this.updateCraftingList();
                this.notifyPatternsChanged();
            }
        }

        if (inv == this.upgrades && (!removed.isEmpty() || !added.isEmpty())) {
            this.checkUpgrades();
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
            this.updateCraftingList();
            this.notifyPatternsChanged();
            this.checkUpgrades();
        }
    }

    private void checkUpgrades() {
        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1:
                this.itemsToSendPerTick = 16;
                break;
            case 2:
                this.itemsToSendPerTick = 48;
                break;
            case 3:
                this.itemsToSendPerTick = 96;
                break;
            case 4:
                this.itemsToSendPerTick = 192;
                break;
            case 5:
                this.itemsToSendPerTick = 320;
                break;
        }
    }

    private void pushItemsOut() {
        try {
            int remaining = this.itemsToSendPerTick;
            IMEMonitor<IAEItemStack> storage = this.getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            Iterator<IAEItemStack> iterator = this.itemsToSend.iterator();
            while (remaining > 0 && iterator.hasNext()) {
                IAEItemStack iaeItemStack = iterator.next();
                IAEItemStack overflow = storage.injectItems(iaeItemStack, Actionable.SIMULATE, this.actionSource);

                if (overflow == null) {
                    storage.injectItems(iaeItemStack, Actionable.MODULATE, this.actionSource);
                    iterator.remove();
                } else {
                    if (overflow.getStackSize() == iaeItemStack.getStackSize()) {
                        continue;
                    }

                    final IAEItemStack item = iaeItemStack.setStackSize(iaeItemStack.getStackSize() - overflow.getStackSize());
                    storage.injectItems(item, Actionable.MODULATE, this.actionSource);
                    iterator.remove();
                    this.itemsToSend.add(item);
                }

                remaining--;
            }
        } catch (GridAccessException e) {
            // :(
        }
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
                    this.craftingList = new ArrayList<>();
                }

                this.craftingList.add(details);
            }
        }
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        for (int h = 0; h < this.upgrades.getSlots(); h++) {
            final ItemStack is = this.upgrades.getStackInSlot(h);
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.patternsInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final IAEItemStack is : this.itemsToSend) {
            drops.add(is.createItemStack());
        }
    }

    public int getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
        return ((ICrazyAEUpgradeInventory) this.upgrades).getInstalledUpgrades(u);
    }

    private void notifyPatternsChanged() {
        try {
            if (this.getProxy().isActive()) {
                this.getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.getProxy().getNode()));
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
                this.cached = true;
            }
        } catch (GridAccessException e) {
            // :(
        }
    }

    private boolean checkStatus() {
        final boolean empty = this.itemsToSend.isEmpty();
        if (empty) {
            try {
                this.getProxy().getTick().sleepDevice(this.getProxy().getNode());
            } catch (GridAccessException e) {
                // :(
            }
        }

        return empty && this.cached;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 10, this.checkStatus(), false);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, int ticksSinceLastCall) {
        if (!this.cached) {
            if (this.getProxy().isActive()) {
                this.notifyPatternsChanged();
            }
            return TickRateModulation.URGENT;
        }

        if (this.getWorld().provider.getWorldTime() % 2 == 0) {
            this.checkUpgrades();
            this.pushItemsOut();
        }

        return TickRateModulation.URGENT;
    }

    private boolean dispatchJob(InventoryCrafting ic, ICraftingPatternDetails details) {
        if (details != null) {
            IAEItemStack output = details.getPrimaryOutput();

//            AELog.log(Level.INFO, output.toString());

            this.itemsToSend.add(output);
            return true;
        }
        return false;
    }

    @MENetworkEventSubscribe
    public void onPowerEvent(final MENetworkPowerStatusChange p) {
        this.updatePowerState();
        if (Platform.isServer()) {
            this.updateCraftingList();
            this.notifyPatternsChanged();
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
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        if (this.getProxy().getNode().isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                details.setPriority(this.priority);
                craftingTracker.addCraftingOption(this, details);
            }
        }
    }
}
