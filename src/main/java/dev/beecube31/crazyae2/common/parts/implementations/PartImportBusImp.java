package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.core.settings.TickRates;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.PartModel;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.IInventoryDestination;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class PartImportBusImp extends CrazyAEPartSharedBus implements IInventoryDestination {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/import_bus_base_gold");
    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/import_bus_off_gold"));
    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/import_bus_on_gold"));
    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/import_bus_has_channel_gold"));

    private final IActionSource source;
    private int itemsToSend = 8; // used in tickingRequest
    private boolean worked; // used in tickingRequest

    @Reflected
    public PartImportBusImp(final ItemStack is) {
        super(is);

        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.source = new MachineSource(this);
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            return false;
        }

        try {
            final IMEMonitor<IAEItemStack> inv = this.getProxy()
                    .getStorage()
                    .getInventory(
                            AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            final IAEItemStack out = inv.injectItems(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(stack),
                    Actionable.SIMULATE,
                    this.source);
            if (out == null) {
                return true;
            }
            return out.getStackSize() != stack.getCount();
        } catch (GridAccessException ex) {
            return false;
        }
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(6, 6, 11, 10, 10, 13);
        bch.addBox(5, 5, 13, 11, 11, 14);
        bch.addBox(4, 4, 14, 12, 12, 16);
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
        return new TickingRequest(1, TickRates.ImportBus.getMax(), this.isSleeping(), false);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return this.doBusWork();
    }

    @Override
    protected TickRateModulation doBusWork() {
        if (!this.getProxy().isActive() || !this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }

        this.itemsToSend = this.calculateItemsToSend();
        this.worked = false;

        final InventoryAdaptor myAdaptor = this.getHandler();
        final FuzzyMode fzMode = (FuzzyMode) this.getConfigManager().getSetting(Settings.FUZZY_MODE);

        if (myAdaptor != null) {
            try {
                final IMEMonitor<IAEItemStack> inv = this.getProxy()
                        .getStorage()
                        .getInventory(
                                AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
                final IEnergyGrid energy = this.getProxy().getEnergy();

                boolean Configured = false;
                for (int x = 0; x < this.availableSlots(); x++) {
                    final IAEItemStack ais = this.getConfig().getAEStackInSlot(x);
                    if (ais != null && this.itemsToSend > 0) {
                        Configured = true;
                        while (this.itemsToSend > 0) {
                            if (this.importStuff(myAdaptor, ais, inv, energy, fzMode)) {
                                break;
                            }
                        }
                    }
                }

                if (!Configured) {
                    while (this.itemsToSend > 0) {
                        if (this.importStuff(myAdaptor, null, inv, energy, fzMode)) {
                            break;
                        }
                    }
                }
            } catch (final GridAccessException e) {
                // :3
            }
        } else {
            return TickRateModulation.SLEEP;
        }

        return this.worked ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    private boolean importStuff(final InventoryAdaptor myAdaptor, final IAEItemStack whatToImport, final IMEMonitor<IAEItemStack> inv, final IEnergySource energy, final FuzzyMode fzMode) {
        final int toSend = this.calculateMaximumAmountToImport(myAdaptor, whatToImport, inv, fzMode);

        if (toSend == 0) {
            return true;
        }

        final ItemStack newItems;

        if (this.getInstalledUpgrades(Upgrades.FUZZY) > 0) {
            newItems = myAdaptor.removeSimilarItems(toSend, whatToImport == null ? ItemStack.EMPTY : whatToImport.getDefinition(), fzMode, this);
        } else {
            newItems = myAdaptor.removeItems(toSend, whatToImport == null ? ItemStack.EMPTY : whatToImport.getDefinition(), this);
        }

        if (!newItems.isEmpty()) {
            final IAEItemStack aeStack = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(newItems);
            final IAEItemStack failed = Platform.poweredInsert(energy, inv, aeStack, this.source);

            if (failed != null) {
                // try unpowered insert, better be a bit lenient then void items
                final IAEItemStack spill = inv.injectItems(failed, Actionable.MODULATE, this.source);
                if (spill != null) {
                    // last resort try to put it back .. lets hope it's a chest type of thing
                    myAdaptor.addItems(spill.createItemStack());
                }
                return true;
            } else {
                this.itemsToSend -= newItems.getCount();
                this.worked = true;
            }
        } else {
            return true;
        }

        return false;
    }

    private int calculateMaximumAmountToImport(final InventoryAdaptor myAdaptor, final IAEItemStack whatToImport, final IMEMonitor<IAEItemStack> inv, final FuzzyMode fzMode) {
        final int toSend = this.itemsToSend;
        final ItemStack itemStackToImport;

        if (whatToImport == null) {
            itemStackToImport = ItemStack.EMPTY;
        } else {
            itemStackToImport = whatToImport.getDefinition();
        }

        final IAEItemStack itemAmountNotStorable;
        final ItemStack simResult;
        if (this.getInstalledUpgrades(Upgrades.FUZZY) > 0) {
            simResult = myAdaptor.simulateSimilarRemove(toSend, itemStackToImport, fzMode, null);
            itemAmountNotStorable = inv.injectItems(AEItemStack.fromItemStack(simResult), Actionable.SIMULATE, this.source);
        } else {
            simResult = myAdaptor.simulateRemove(toSend, itemStackToImport, null);
            itemAmountNotStorable = inv.injectItems(AEItemStack.fromItemStack(simResult), Actionable.SIMULATE, this.source);
        }

        if (simResult.isEmpty()) {
            return 0;
        }

        if (itemAmountNotStorable != null) {
            if (simResult.getCount() == itemAmountNotStorable.getStackSize()) {
                return 0;
            }
            return (int) Math.min(simResult.getCount() - itemAmountNotStorable.getStackSize(), toSend);
        }

        return toSend;
    }

    @Override
    protected boolean isSleeping() {
        return this.getHandler() == null || super.isSleeping();
    }

    @Override
    public RedstoneMode getRSMode() {
        return (RedstoneMode) this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
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

}
