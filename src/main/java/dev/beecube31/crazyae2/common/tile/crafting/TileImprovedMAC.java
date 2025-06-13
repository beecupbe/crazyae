package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
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
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.ContainerNull;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.interfaces.IGridHostMonitorable;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvOCTile;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TileImprovedMAC extends CrazyAENetworkInvOCTile implements IConfigManagerHost, IGridTickable, ICraftingMedium, ICraftingProvider, IUpgradesInfoProvider, IGridHostMonitorable {

    private final int maxQueueSize = CrazyAEConfig.improvedMolecularAssemblerMaxQueueSize;
    private int itemsToSendPerTick = 8;

    private final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, 45, 1).setItemFilter(RestrictedSlot.PlaceableItemType.ENCODED_CRAFTING_PATTERN.associatedFilter);
    private final IConfigManager settings;
    private final CrazyAEBlockUpgradeInv upgrades;
    private boolean isPowered = false;
    private boolean cached = false;
    private final IActionSource actionSource = new MachineSource(this);

    private int priority = 1;
    private List<ICraftingPatternDetails> craftingList = null;

    private final List<IAEItemStack> itemsToSend = new ArrayList<>(); // not used, just for compatibility
    private final List<InventoryCrafting> activeJobs = new ArrayList<>();

    public TileImprovedMAC() {
        final Block assembler = CrazyAE.definitions().blocks().improvedMolecularAssembler().maybeBlock().orElse(null);
        Preconditions.checkNotNull(assembler);

        this.settings = new ConfigManager(this);
        this.settings.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getProxy().setIdlePowerUsage(32.0);
        this.upgrades = new CrazyAEBlockUpgradeInv(assembler, this, this.getUpgradeSlots());

    }

    private int getUpgradeSlots() {
        return 5;
    }

    @Override
    public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
        if (this.isPowered && !this.isBusy()) {
            this.activeJobs.add(table);

            try {
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
            } catch (GridAccessException ignored) {}

            return true;
        }

        return false;
    }

    @Override
    public boolean isBusy() {
        return this.activeJobs.size() >= this.maxQueueSize;
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

        NBTTagList jobsList = new NBTTagList();
        for (InventoryCrafting job : this.activeJobs) {
            NBTTagCompound jobTag = new NBTTagCompound();
            NBTTagList itemsList = new NBTTagList();
            for (int i = 0; i < job.getSizeInventory(); i++) {
                ItemStack stack = job.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setByte("Slot", (byte) i);
                    stack.writeToNBT(itemTag);
                    itemsList.appendTag(itemTag);
                }
            }
            jobTag.setTag("Items", itemsList);
            jobsList.appendTag(jobTag);
        }
        data.setTag("activeJobs", jobsList);

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

        this.activeJobs.clear();
        if (data.hasKey("activeJobs")) {
            NBTTagList jobsList = data.getTagList("activeJobs", 10);
            for (int i = 0; i < jobsList.tagCount(); i++) {
                NBTTagCompound jobTag = jobsList.getCompoundTagAt(i);
                InventoryCrafting job = new InventoryCrafting(new ContainerNull(), 3, 3);

                NBTTagList itemsList = jobTag.getTagList("Items", 10);
                for (int j = 0; j < itemsList.tagCount(); j++) {
                    NBTTagCompound itemTag = itemsList.getCompoundTagAt(j);
                    int slot = itemTag.getByte("Slot");
                    if (slot >= 0 && slot < job.getSizeInventory()) {
                        job.setInventorySlotContents(slot, new ItemStack(itemTag));
                    }
                }
                this.activeJobs.add(job);
            }
        }

        this.patternsInv.readFromNBT(data, "patterns");
        this.priority = data.getInteger("priority");
        this.upgrades.readFromNBT(data, "upgrades");
        this.settings.readFromNBT(data);
    }

    @Override
    public @NotNull AECableType getCableConnectionType(final @NotNull AEPartLocation dir) {
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
    public @NotNull IItemHandler getInternalInventory() {
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
            default:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWithoutUpgrades;
                break;
            case 1:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWith1Upgrade;
                break;
            case 2:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWith2Upgrades;
                break;
            case 3:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWith3Upgrades;
                break;
            case 4:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWith4Upgrades;
                break;
            case 5:
                this.itemsToSendPerTick = CrazyAEConfig.improvedMolecularAssemblerCraftsPerTickWith5Upgrades;
                break;
        }
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
        try {
            int remaining = this.itemsToSendPerTick;
            IMEMonitor<IAEItemStack> storage = this.getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            Iterator<IAEItemStack> iterator = this.itemsToSend.iterator();
            while (remaining > 0 && iterator.hasNext()) {
                IAEItemStack iaeItemStack = iterator.next();
                IAEItemStack overflow = storage.injectItems(iaeItemStack, Actionable.SIMULATE, this.actionSource);

                if (overflow == null) {
                    storage.injectItems(iaeItemStack, Actionable.MODULATE, this.actionSource);
                    this.addCompletedOperations(iaeItemStack.getStackSize());
                    iterator.remove();
                } else {
                    if (overflow.getStackSize() == iaeItemStack.getStackSize()) {
                        continue;
                    }

                    final IAEItemStack item = iaeItemStack.setStackSize(iaeItemStack.getStackSize() - overflow.getStackSize());
                    storage.injectItems(item, Actionable.MODULATE, this.actionSource);
                    this.addCompletedOperations(item.getStackSize());
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
        return this.upgrades.getInstalledUpgrades(u);
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
    public @NotNull TickingRequest getTickingRequest(final @NotNull IGridNode node) {
        return new TickingRequest(1, 10, this.checkStatus(), false);
    }

    @Override
    public @NotNull TickRateModulation tickingRequest(final @NotNull IGridNode node, int ticksSinceLastCall) {
        if (!this.cached) {
            if (this.getProxy().isActive()) {
                this.notifyPatternsChanged();
            }
            return TickRateModulation.URGENT;
        }

        if (this.getWorld().provider.getWorldTime() % 2 == 0) {
            this.checkUpgrades();
        }

        if (!this.itemsToSend.isEmpty()) {
            this.pushItemsOut();
        }

        if (!this.activeJobs.isEmpty()) {
            IStorageGrid storage;

            try {
                storage = this.getProxy().getGrid().getCache(IStorageGrid.class);
            } catch (GridAccessException e) {
                return TickRateModulation.IDLE;
            }

            int processedThisTick = 0;

            Iterator<InventoryCrafting> iterator = this.activeJobs.iterator();
            while (iterator.hasNext() && processedThisTick < this.itemsToSendPerTick) {
                InventoryCrafting craftingGrid = iterator.next();

                ItemStack result = Platform.findMatchingRecipeOutput(craftingGrid, this.world);

                if (result != null && !result.isEmpty()) {
                    List<ItemStack> outputs = new ArrayList<>();
                    outputs.add(result.copy());

                    for (int i = 0; i < craftingGrid.getSizeInventory(); ++i) {
                        ItemStack remaining = craftingGrid.getStackInSlot(i).getItem().getContainerItem(craftingGrid.getStackInSlot(i));
                        if (!remaining.isEmpty()) {
                            outputs.add(remaining);
                        }
                    }

                    boolean allPushed = true;
                    for (ItemStack outputStack : outputs) {
                        IAEItemStack toInject = AEItemStack.fromItemStack(outputStack);
                        IAEItemStack overflow = storage.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))
                                .injectItems(toInject, Actionable.SIMULATE, this.actionSource);

                        if (overflow != null && overflow.getStackSize() > 0) {
                            allPushed = false;
                            break;
                        }
                    }

                    if (allPushed) {
                        for (ItemStack outputStack : outputs) {
                            IAEItemStack toInject = AEItemStack.fromItemStack(outputStack);
                            storage.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class))
                                    .injectItems(toInject, Actionable.MODULATE, this.actionSource);
                        }

                        processedThisTick += outputs.size();
                        this.addCompletedOperations(1);
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }

        if (this.activeJobs.isEmpty()) {
            return TickRateModulation.SLEEP;
        }

        return TickRateModulation.URGENT;
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
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        if (this.getProxy().getNode().isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                details.setPriority(this.priority);
                craftingTracker.addCraftingOption(this, details);
            }
        }
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().improvedMolecularAssembler();
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
        return CrazyAEGuiText.IMPROVED_MAC_GUI.getLocal();
    }
}
