package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.GridFlags;
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
import appeng.me.GridAccessException;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.inv.filter.IAEItemFilter;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.common.interfaces.device.mechanical.IBotaniaMechanicalDevice;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvOCTile;
import dev.beecube31.crazyae2.common.util.AEUtils;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.api.storage.IManaStorageChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class TileBotaniaMechanicalMachineBase extends CrazyAENetworkInvOCTile implements IBotaniaMechanicalDevice, IUpgradesInfoProvider, IConfigManagerHost, IGridTickable, ICraftingMedium, ICraftingProvider {

    protected List<ICraftingPatternDetails> craftingList = null;
    protected List<CraftingTask> queueMap = new ArrayList<>();
    protected CrazyAEInternalInv craftingInputInv;
    protected CrazyAEInternalInv craftingOutputInv;
    protected final CrazyAEInternalInv internalPatternsStorageInv = new CrazyAEInternalInv(this, 45);
    protected final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, 2);
    protected final CrazyAEInternalInv findSlot = new CrazyAEInternalInv(this, 1, 1);
    protected final IConfigManager settings;
    protected CrazyAEBlockUpgradeInv upgrades;
    protected boolean isPowered = false;
    protected boolean cached = false;
    protected IActionSource actionSource;

    protected int tasksQueued = 0;
    protected int tasksMaxAmt = 1;
    protected int progressPerTick = 1;
    protected int itemsPerTick = 1;

    protected boolean isRecipeValidated = false;

    public TileBotaniaMechanicalMachineBase() {
        this.settings = new ConfigManager(this);
        this.settings.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getProxy().setIdlePowerUsage(64);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    public int getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
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
        NBTUtils.writeQueueMapToNBT(this.queueMap, data, "queueMap");
        data.setInteger("tasksAmt", this.tasksQueued);
        this.patternsInv.writeToNBT(data, "patterns");
        this.internalPatternsStorageInv.writeToNBT(data, "patternsInternal");
        this.craftingOutputInv.writeToNBT(data, "output");
        this.craftingInputInv.writeToNBT(data, "input");
        this.upgrades.writeToNBT(data, "upgrades");
        this.settings.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        this.tasksQueued = data.getInteger("tasksAmt");
        this.queueMap = NBTUtils.readQueueMapFromNBT(data, "queueMap");
        this.patternsInv.readFromNBT(data, "patterns");
        this.internalPatternsStorageInv.readFromNBT(data, "patternsInternal");
        this.craftingOutputInv.readFromNBT(data, "output");
        this.craftingInputInv.readFromNBT(data, "input");
        this.upgrades.readFromNBT(data, "upgrades");
        this.settings.readFromNBT(data);
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "upgrades" -> this.upgrades;
            case "input" -> this.craftingInputInv;
            case "output" -> this.craftingOutputInv;
            case "patterns" -> this.patternsInv;
            case "patternsInternal" -> this.internalPatternsStorageInv;
            case "findSlot" -> this.findSlot;
            default -> null;
        };
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        if (this.getProxy().getNode().isActive() && this.craftingList != null) {
            for (final ICraftingPatternDetails details : this.craftingList) {
                craftingTracker.addCraftingOption(this, details);
            }
        }
    }

    protected void updateCraftingList() {
        final Boolean[] accountedFor = new Boolean[this.internalPatternsStorageInv.getSlots()];
        Arrays.fill(accountedFor, false);

        if (this.craftingList != null) {
            final Iterator<ICraftingPatternDetails> i = this.craftingList.iterator();
            while (i.hasNext()) {
                final ICraftingPatternDetails details = i.next();
                boolean found = false;

                for (int x = 0; x < accountedFor.length; x++) {
                    final ItemStack is = this.internalPatternsStorageInv.getStackInSlot(x);
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
                this.addToCraftingList(this.internalPatternsStorageInv.getStackInSlot(x));
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

    protected void notifyPatternsChanged() {
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

    @Override public abstract boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting);

    @Override
    public boolean isBusy() {
        return this.tasksQueued >= this.tasksMaxAmt;
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

        for (final ItemStack is : this.internalPatternsStorageInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    protected void checkUpgrades() {
        this.itemsPerTick = 0;
        this.tasksMaxAmt = 0;
        this.progressPerTick = 0;
        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1:
                this.itemsPerTick += 8;
                this.tasksMaxAmt += 8;
                this.progressPerTick += 10;
                break;
            case 2:
                this.itemsPerTick += 24;
                this.tasksMaxAmt += 24;
                this.progressPerTick += 25;
                break;
            case 3:
                this.itemsPerTick += 48;
                this.tasksMaxAmt += 48;
                this.progressPerTick += 50;
                break;
            case 4:
                this.itemsPerTick += 192;
                this.tasksMaxAmt += 192;
                this.progressPerTick += 100;
                break;
            case 5:
                this.itemsPerTick += 320;
                this.tasksMaxAmt += 320;
                this.progressPerTick += 100;
                break;
        }

        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.IMPROVED_SPEED)) {
            case 1:
                this.progressPerTick += 2;
                break;
            case 2:
                this.progressPerTick += 5;
                break;
            case 3:
                this.progressPerTick += 10;
                break;
            case 4:
                this.progressPerTick += 15;
                break;
            case 5:
                this.progressPerTick += 20;
                break;
        }

        if (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            this.progressPerTick += 100;
        }

        if (this.progressPerTick > 100) this.progressPerTick = 100;
        if (this.progressPerTick <= 0) this.progressPerTick = 1;
        if (this.itemsPerTick <= 0) this.itemsPerTick = 1;
        if (this.tasksMaxAmt <= 0) this.tasksMaxAmt = 1;
    }

    @Nullable
    protected IAEItemStack pushItemsOut(IAEItemStack is) {
        try {
            IMEMonitor<IAEItemStack> storage = this.getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            IAEItemStack overflow = storage.injectItems(is, Actionable.SIMULATE, this.actionSource);

            if (overflow == null) {
                storage.injectItems(is, Actionable.MODULATE, this.actionSource);
                return null;
            } else {
                return is;
            }

        } catch (GridAccessException e) {
            // :(
        }
        return is;
    }

    protected boolean tryUseMana(int amt) {
        try {
            final IMEMonitor<IAEItemStack> inv = this.getProxy()
                    .getStorage()
                    .getInventory(
                            AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class));

            IAEItemStack item = AEItemStack.fromItemStack(CrazyAE.definitions().items().manaAsAEStack()
                    .maybeStack(amt).orElse(ItemStack.EMPTY));

            if (item != null && item.getStackSize() == amt) {
                IAEItemStack simulate = AEUtils.extractFromME(inv, item, this.actionSource, Actionable.SIMULATE);
                if (simulate != null && simulate.getStackSize() == amt) {
                    AEUtils.extractFromME(inv, item, this.actionSource, Actionable.MODULATE);
                    return true;
                }
            }
        } catch (GridAccessException e) {
            // :(
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

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode iGridNode) {
        return new TickingRequest(1, 20, false, true);
    }

    @NotNull
    @Override
    public abstract TickRateModulation tickingRequest(@NotNull IGridNode iGridNode, int ticksSinceLastCall);

    public abstract void encodePattern();

    public abstract boolean validateRecipe();

    @Override
    public IConfigManager getConfigManager() {
        return this.settings;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull AEPartLocation aePartLocation) {
        return AECableType.COVERED;
    }

    @NotNull
    @Override
    public IItemHandler getInternalInventory() {
        return this.patternsInv;
    }

    public boolean isRecipeValidated() {
        return this.isRecipeValidated;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (inv == this.craftingInputInv && (!removed.isEmpty() || !added.isEmpty())) {
            this.validateRecipe();
        }

        if (inv == this.internalPatternsStorageInv && (!removed.isEmpty() || !added.isEmpty())) {
            this.updateCraftingList();
            this.notifyPatternsChanged();
        }

        if (inv == this.upgrades && (!removed.isEmpty() || !added.isEmpty())) {
            this.checkUpgrades();
        }
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {}

    protected static final class DisabledFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(IItemHandler inv, int slot, int amount) {
            return false;
        }

        @Override
        public boolean allowInsert(IItemHandler inv, int slot, ItemStack itemstack) {
            return false;
        }
    }

    public interface IManaTask {
        int getRequiredMana();
    }

    public interface IPureDaisyTask {
        boolean requireOutputBucket();
    }

    public interface IRuneAltarTask extends IManaTask {

    }

    public static class CraftingTask {
        private IAEItemStack[] taskItems;
        private int progress;

        public CraftingTask(
                IAEItemStack[] taskItems,
                int progress
        ) {
            this.taskItems = taskItems;
            this.progress = progress;
        }

        protected void setProgress(int progress) {
            this.progress = progress;
        }

        protected void addProgress(int progress) {
            this.progress += progress;
        }

        protected void setTaskItems(IAEItemStack[] taskItems) {
            this.taskItems = taskItems;
        }

        public IAEItemStack[] getTaskItems() {
            return taskItems;
        }

        public int getProgress() {
            return progress;
        }
    }

    public static class ManaCraftingTask extends CraftingTask implements IManaTask {
        private final int requiredMana;

        public ManaCraftingTask(
                IAEItemStack[] taskItems,
                int progress,
                int requiredMana
        ) {
            super(taskItems, progress);
            this.requiredMana = requiredMana;
        }

        @Override
        public int getRequiredMana() {
            return requiredMana;
        }
    }

    public static class PureDaisyCraftingTask extends CraftingTask implements IPureDaisyTask {
        private final boolean requireOutputBucket;

        public PureDaisyCraftingTask(
                IAEItemStack[] taskItems,
                int progress,
                boolean requireOutputBucket
        ) {
            super(taskItems, progress);
            this.requireOutputBucket = requireOutputBucket;
        }

        @Override
        public boolean requireOutputBucket() {
            return this.requireOutputBucket;
        }
    }

    public static class RuneAltarCraftingTask extends CraftingTask implements IRuneAltarTask {
        private final int requiredMana;

        public RuneAltarCraftingTask(
                IAEItemStack[] taskItems,
                int progress,
                int requiredMana
        ) {
            super(taskItems, progress);
            this.requiredMana = requiredMana;
        }

        @Override
        public int getRequiredMana() {
            return requiredMana;
        }
    }

    public static class TeraplateCraftingTask extends CraftingTask implements IManaTask {
        private final int requiredMana;

        public TeraplateCraftingTask(
                IAEItemStack[] taskItems,
                int progress,
                int requiredMana
        ) {
            super(taskItems, progress);
            this.requiredMana = requiredMana;
        }

        @Override
        public int getRequiredMana() {
            return requiredMana;
        }
    }

    public static class BreweryCraftingTask extends CraftingTask implements IManaTask {
        private final int requiredMana;

        public BreweryCraftingTask(
                IAEItemStack[] taskItems,
                int progress,
                int requiredMana
        ) {
            super(taskItems, progress);
            this.requiredMana = requiredMana;
        }

        @Override
        public int getRequiredMana() {
            return requiredMana;
        }
    }
}