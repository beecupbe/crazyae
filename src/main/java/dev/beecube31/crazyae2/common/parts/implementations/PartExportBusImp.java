package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.core.AELog;
import appeng.core.settings.TickRates;
import appeng.helpers.MultiCraftingTracker;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.PartModel;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class PartExportBusImp extends CrazyAEPartSharedBus implements ICraftingRequester {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/export_bus_base_gold");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/export_bus_off_gold"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/export_bus_on_gold"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/export_bus_has_channel_gold"));

    private final MultiCraftingTracker craftingTracker = new MultiCraftingTracker(this, 9);
    private final IActionSource mySrc;
    private long itemToSend = 8;
    private boolean didSomething = false;
    private int nextSlot = 0;

    @Reflected
    public PartExportBusImp(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        this.getConfigManager().registerSetting(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
        this.mySrc = new MachineSource(this);
    }

    @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.craftingTracker.readFromNBT(extra);
        this.nextSlot = extra.getInteger("nextSlot");
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        this.craftingTracker.writeToNBT(extra);
        extra.setInteger("nextSlot", this.nextSlot);
    }

    @Override
    protected TickRateModulation doBusWork() {
        if (!this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }

        this.itemToSend = this.calculateItemsToSend();
        this.didSomething = false;

        try {
            final InventoryAdaptor destination = this.getHandler();
            final IMEMonitor<IAEItemStack> inv = this.getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            final IEnergyGrid energy = this.getProxy().getEnergy();
            final ICraftingGrid cg = this.getProxy().getCrafting();
            final FuzzyMode fzMode = (FuzzyMode) this.getConfigManager().getSetting(Settings.FUZZY_MODE);
            final SchedulingMode schedulingMode = (SchedulingMode) this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

            if (destination != null) {
                int x;

                for (x = 0; x < this.availableSlots() && this.itemToSend > 0; x++) {
                    final int slotToExport = this.getStartingSlot(schedulingMode, x);

                    final IAEItemStack ais = this.getConfig().getAEStackInSlot(slotToExport);

                    if (ais == null || this.itemToSend <= 0) {
                        continue;
                    }

                    if (this.craftOnly()) {
                        this.didSomething = this.craftingTracker.handleCrafting(slotToExport, this.itemToSend, ais, destination, this.getTile().getWorld(), this.getProxy().getGrid(), cg, this.mySrc) || this.didSomething;
                        continue;
                    }

                    final long before = this.itemToSend;

                    if (this.getInstalledUpgrades(Upgrades.FUZZY) > 0) {
                        for (final IAEItemStack o : ImmutableList.copyOf(inv.getStorageList().findFuzzy(ais, fzMode))) {
                            if (o.getStackSize() > 0) {
                                this.pushItemIntoTarget(destination, energy, inv, o);
                                if (this.itemToSend <= 0) {
                                    break;
                                }
                            }
                        }
                    } else {
                        final IAEItemStack o = inv.getStorageList().findPrecise(ais);
                        if (o != null && o.getStackSize() > 0) {
                            this.pushItemIntoTarget(destination, energy, inv, ais);
                        }
                    }

                    if (this.itemToSend == before && this.isCraftingEnabled()) {
                        this.didSomething = this.craftingTracker.handleCrafting(slotToExport, this.itemToSend, ais, destination, this.getTile().getWorld(), this.getProxy().getGrid(), cg, this.mySrc) || this.didSomething;
                    }
                }

                this.updateSchedulingMode(schedulingMode, x);
            } else {
                return TickRateModulation.SLEEP;
            }
        } catch (final GridAccessException e) {
            // :P
        }

        return this.didSomething ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 5;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (Platform.isServer()) {
            CrazyAEGuiHandler.openGUI(player, this.getHost().getTile(), this.getSide(), CrazyAEGuiBridge.IMPROVED_BUS);
        }
        return true;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, TickRates.FluidExportBus.getMax(), this.isSleeping(), false);
    }

    @Override
    public RedstoneMode getRSMode() {
        return (RedstoneMode) this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return this.doBusWork();
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.craftingTracker.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(final ICraftingLink link, final IAEItemStack items, final Actionable mode) {
        final InventoryAdaptor d = this.getHandler();

        try {
            if (d != null && this.getProxy().isActive()) {
                final IEnergyGrid energy = this.getProxy().getEnergy();
                final double power = items.getStackSize();

                if (energy.extractAEPower(power, mode, PowerMultiplier.CONFIG) > power - 0.01) {
                    ItemStack inputStack = items.getCachedItemStack(items.getStackSize());

                    ItemStack remaining;

                    if (mode == Actionable.SIMULATE) {
                        remaining = d.simulateAdd(inputStack);
                        items.setCachedItemStack(inputStack);
                    } else {
                        remaining = d.addItems(inputStack);
                        if (!remaining.isEmpty()) {
                            items.setCachedItemStack(remaining);
                        }
                    }

                    if (remaining == inputStack) {
                        return items;
                    }

                    return AEItemStack.fromItemStack(remaining);
                }
            }
        } catch (final GridAccessException e) {
            AELog.debug(e);
        }

        return items;
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.craftingTracker.jobStateChange(link);
    }

    @Override
    protected boolean isSleeping() {
        return this.getHandler() == null || super.isSleeping();
    }

    private boolean craftOnly() {
        return this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    private boolean isCraftingEnabled() {
        return this.getInstalledUpgrades(Upgrades.CRAFTING) > 0;
    }

    private void pushItemIntoTarget(final InventoryAdaptor d, final IEnergyGrid energy, final IMEInventory<IAEItemStack> inv, IAEItemStack org) {
        ItemStack inputStack = org.getCachedItemStack(org.getStackSize());

        ItemStack remaining = d.simulateAdd(inputStack);

        // Store the stack in the cache for next time.
        if (!remaining.isEmpty()) {
            org.setCachedItemStack(remaining);
            if (remaining == inputStack) {
                return;
            }
        }

        final long canFit = remaining.isEmpty() ? this.itemToSend : this.itemToSend - remaining.getCount();

        if (canFit > 0) {
            IAEItemStack ais = org.copy();
            ais.setStackSize(canFit);
            final IAEItemStack itemsToAdd = Platform.poweredExtraction(energy, inv, ais, this.mySrc);

            if (itemsToAdd != null) {
                this.itemToSend -= itemsToAdd.getStackSize();

                inputStack.setCount(Ints.saturatedCast(itemsToAdd.getStackSize()));

                final ItemStack failed = d.addItems(inputStack);
                if (!failed.isEmpty()) {
                    ais.setStackSize(failed.getCount());
                    inv.injectItems(ais, Actionable.MODULATE, this.mySrc);
                } else {
                    this.didSomething = true;
                }
            } else {
                org.setCachedItemStack(inputStack);
            }
        }
    }

    private int getStartingSlot(final SchedulingMode schedulingMode, final int x) {
        if (schedulingMode == SchedulingMode.RANDOM) {
            return Platform.getRandom().nextInt(this.availableSlots());
        }

        if (schedulingMode == SchedulingMode.ROUNDROBIN) {
            return (this.nextSlot + x) % this.availableSlots();
        }

        return x;
    }

    private void updateSchedulingMode(final SchedulingMode schedulingMode, final int x) {
        if (schedulingMode == SchedulingMode.ROUNDROBIN) {
            this.nextSlot = (this.nextSlot + x) % this.availableSlots();
        }
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().parts().improvedExportBus();
    }
}
