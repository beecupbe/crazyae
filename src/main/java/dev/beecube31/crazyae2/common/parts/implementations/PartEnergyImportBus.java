package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.implementations.items.MemoryCardMessages;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.PartModel;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.Platform;
import appeng.util.SettingsFrom;
import appeng.util.inv.InvOperation;
import cofh.redstoneflux.api.IEnergyProvider;
import com.denfop.api.sytem.EnergyType;
import com.denfop.api.sytem.IAcceptor;
import com.denfop.componets.ComponentBaseEnergy;
import com.denfop.componets.Energy;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.compat.IUCompat;
import dev.beecube31.crazyae2.common.components.ComponentEFEnergySink;
import dev.beecube31.crazyae2.common.components.ComponentEUEnergySink;
import dev.beecube31.crazyae2.common.components.ComponentMoreEnergySink;
import dev.beecube31.crazyae2.common.components.ComponentRFEnergySink;
import dev.beecube31.crazyae2.common.enums.EnergyBusType;
import dev.beecube31.crazyae2.common.interfaces.IEnergyBus;
import dev.beecube31.crazyae2.common.interfaces.IPartActivationOverrider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.util.AEUtils;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

import static dev.beecube31.crazyae2.common.util.ModsChecker.*;

@SuppressWarnings("unused")
public class PartEnergyImportBus extends CrazyAEPartSharedBus implements IEnergyBus, IEnergyStorage, IPartActivationOverrider {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/energy_import_bus_base");
    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_import_bus_off"));
    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_import_bus_off"));
    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_import_bus_has_channel"));

    private final IActionSource source;
    private long itemsToSend = 32;
    private IItemDefinition currentEnergy = null;
    private Object energyComponent = null;
    private boolean worked;
    private long maxConfigEnergy;

    private ComponentRFEnergySink rfDelegate;
    private ComponentEUEnergySink euDelegate;
    private ComponentEFEnergySink energyDelegate;
    private ComponentMoreEnergySink solariumDelegate;
    private ComponentMoreEnergySink quantumDelegate;

    @Reflected
    public PartEnergyImportBus(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.source = new MachineSource(this);
    }

    public ComponentRFEnergySink getRfDelegate() {
        return this.rfDelegate;
    }

    @Override
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        super.onChangeInventory(inv, slot, mc, removedStack, newStack);
        if (inv == this.upgrades) {
            this.updateMaxConfigEnergy();
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.maxConfigEnergy = extra.getLong("energyConfig");
    }

    @Override
    public void writeToNBT(final NBTTagCompound extra) {
        super.writeToNBT(extra);
        extra.setLong("energyConfig", this.maxConfigEnergy);
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(6, 6, 11, 10, 10, 13);
        bch.addBox(5, 5, 13, 11, 11, 14);
        bch.addBox(4, 4, 14, 12, 12, 16);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        if (Platform.isServer()) {
            this.rfDelegate = RFLUX_LOADED ? new ComponentRFEnergySink(this)
                    : null;

            this.euDelegate = IC2_LOADED ? new ComponentEUEnergySink(this)
                    : null;

            this.energyDelegate = IU_LOADED ? new ComponentEFEnergySink(this)
                    : null;

            this.solariumDelegate = IU_LOADED ? new ComponentMoreEnergySink(CrazyAE.definitions().items().SEEnergyAsAeStack(), this)
                    : null;

            this.quantumDelegate = IU_LOADED ? new ComponentMoreEnergySink(CrazyAE.definitions().items().QEEnergyAsAeStack(), this)
                    : null;

            if (this.euDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new ic2.api.energy.event.EnergyTileLoadEvent(this.euDelegate));
            }

            if (this.energyDelegate != null) {
                IUCompat.addEFTileToWorld(this.getTile().getWorld(), this.energyDelegate);
            }

            if (this.solariumDelegate != null) {
                IUCompat.addMultiTileToWorld(this.getTile().getWorld(), EnergyType.SOLARIUM, this.solariumDelegate);
            }

            if (this.quantumDelegate != null) {
                IUCompat.addMultiTileToWorld(this.getTile().getWorld(), EnergyType.QUANTUM, this.quantumDelegate);
            }
        }

    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
        if (Platform.isServer()) {
            if (this.euDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new ic2.api.energy.event.EnergyTileUnloadEvent(this.euDelegate));
            }

            if (this.energyDelegate != null) {
                IUCompat.removeEFTileFromWorld(this.getTile().getWorld(), this.energyDelegate);
            }

            if (this.solariumDelegate != null) {
                IUCompat.removeMultiTileFromWorld(this.getTile().getWorld(), EnergyType.SOLARIUM, this.solariumDelegate);
            }

            if (this.quantumDelegate != null) {
                IUCompat.removeMultiTileFromWorld(this.getTile().getWorld(), EnergyType.QUANTUM, this.quantumDelegate);
            }
        }
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 5;
    }

    @Override
    public void uploadSettings(final SettingsFrom from, final NBTTagCompound compound, EntityPlayer player) {
        if (compound != null) {
            final IConfigManager cm = this.getConfigManager();
            if (cm != null) {
                cm.readFromNBT(compound);
            }

            if (compound.hasKey("EnergyConfig")) {
                this.maxConfigEnergy = compound.getLong("EnergyConfig");
            }
        }

        final IItemHandler inv = this.getInventoryByName("config");
        if (inv instanceof AppEngInternalAEInventory target) {
            final AppEngInternalAEInventory tmp = new AppEngInternalAEInventory(null, target.getSlots());
            tmp.readFromNBT(compound, "config");
            for (int x = 0; x < tmp.getSlots(); x++) {
                target.setStackInSlot(x, tmp.getStackInSlot(x));
            }
        }
    }

    private boolean useMemoryCard(final EntityPlayer player) {
        final ItemStack memCardIS = player.inventory.getCurrentItem();

        if (!memCardIS.isEmpty() && this.useStandardMemoryCard() && memCardIS.getItem() instanceof IMemoryCard memoryCard) {
            ItemStack is = this.getItemStack(PartItemStack.NETWORK);

            final String name = is.getTranslationKey();

            if (player.isSneaking()) {
                final NBTTagCompound data = this.downloadSettings(SettingsFrom.MEMORY_CARD);
                if (data != null) {
                    this.setMemoryCardContents(memCardIS, name, data);
                    memoryCard.notifyUser(player, MemoryCardMessages.SETTINGS_SAVED);
                }
            } else {
                final String storedName = memoryCard.getSettingsName(memCardIS);
                final NBTTagCompound data = memoryCard.getData(memCardIS);
                if (name.equals(storedName)) {
                    this.uploadSettings(SettingsFrom.MEMORY_CARD, data, player);
                    memoryCard.notifyUser(player, MemoryCardMessages.SETTINGS_LOADED);
                } else {
                    memoryCard.notifyUser(player, MemoryCardMessages.INVALID_MACHINE);
                }
            }
            return true;
        }
        return false;
    }

    public void setMemoryCardContents(final ItemStack is, final String settingsName, final NBTTagCompound data) {
        final NBTTagCompound c = Platform.openNbtData(is);
        c.setString("Config", settingsName);
        c.setTag("Data", data);
        c.setLong("EnergyConfig", this.maxConfigEnergy);
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (!this.useMemoryCard(player)) {
            if (Platform.isServer()) {
                CrazyAEGuiHandler.openGUI(player, this.getHost().getTile(), this.getSide(), CrazyAEGuiBridge.ENERGY_BUS);
            }
        }
        return true;
    }

    @Override
    public @NotNull TickingRequest getTickingRequest(final @NotNull IGridNode node) {
        return new TickingRequest(1, 10, this.isSleeping(), false);
    }

    @Override
    public @NotNull TickRateModulation tickingRequest(final @NotNull IGridNode node, final int ticksSinceLastCall) {
        return this.doBusWork();
    }

    @Override
    protected TickRateModulation doBusWork() {
        if (!this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }

        this.energyComponent = null;
        this.currentEnergy = null;
        this.itemsToSend = -1;
        this.worked = false;

        this.itemsToSend = Math.min(this.calculateEFEnergyToSend(), (this.maxConfigEnergy == 0 ? Long.MAX_VALUE : this.maxConfigEnergy));

        final TileEntity te = this.getVictim();

        if (IU_LOADED && Utils.isIUBlock(te)) {
            if (this.itemsToSend > 0L && Utils.findEnergyComponents(te).isEmpty()) {
                try {
                    final IMEMonitor<IAEItemStack> inv = this.getProxy()
                            .getStorage()
                            .getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

                    while (this.itemsToSend > 0) {
                        if (this.importStuff(te, inv)) {
                            break;
                        }
                    }
                } catch (final GridAccessException e) {
                    // :3
                }
            } else {
                return TickRateModulation.SLEEP;
            }
        } else if (te instanceof IEnergyStorage storage) {
            if (storage.canExtract()) {
                int extracted = storage.extractEnergy((int) this.availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);

                IAEItemStack ais = AEUtils.injectToME(
                        this.getEnergyInv(),
                        AEUtils.createAEStackFromDefinition(
                                CrazyAE.definitions().items().FEEnergyAsAeStack(),
                                extracted
                        ),
                        this.source,
                        Actionable.MODULATE
                );

                if (ais == null) {
                    this.worked = true;
                }
            }
        } else if (RFLUX_LOADED && te instanceof IEnergyProvider r) {
            int extracted = r.extractEnergy(this.getSide().getFacing(), (int) this.availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);

            IAEItemStack ais = AEUtils.injectToME(
                    this.getEnergyInv(),
                    AEUtils.createAEStackFromDefinition(
                            CrazyAE.definitions().items().FEEnergyAsAeStack(),
                            extracted
                    ),
                    this.source,
                    Actionable.MODULATE
            );

            if (ais == null) {
                this.worked = true;
            }
        } else if (te != null) {
            this.tryImportFromFEComponents(te);
        }

        return this.worked ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    private boolean importStuff(final TileEntity tile,final IMEMonitor<IAEItemStack> inv) {
        final long toSend = this.calculateMaximumAmountToImport(tile, inv);

        if (toSend == 0) {
            return true;
        }

        final IAEItemStack failed = AEUtils.injectToME(
                inv,
                AEUtils.createAEStackFromDefinition(
                        this.currentEnergy,
                        toSend
                ),
                this.source,
                Actionable.MODULATE
        );

        if (failed != null) {
            if (failed.getStackSize() == toSend) return false;

            this.extractEnergyFromComponent(failed.getStackSize());
            this.itemsToSend -= failed.getStackSize();
            return true;
        } else {
            this.extractEnergyFromComponent(toSend);
            this.itemsToSend -= toSend;
            this.worked = true;
            return false;
        }
    }

    private void tryImportFromFEComponents(final TileEntity te) {
        try {
            for (Field field : te.getClass().getDeclaredFields()) {
                if (IEnergyStorage.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    IEnergyStorage candidate = (IEnergyStorage) field.get(te);

                    if (candidate.canExtract()) {
                        int extracted = candidate.extractEnergy((int) this.availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);

                        IAEItemStack ais = AEUtils.injectToME(
                                this.getEnergyInv(),
                                AEUtils.createAEStackFromDefinition(
                                        CrazyAE.definitions().items().FEEnergyAsAeStack(),
                                        extracted
                                ),
                                this.source,
                                Actionable.MODULATE
                        );

                        if (ais == null) {
                            this.worked = true;
                            break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException ignored) {}
    }

    private void extractEnergyFromComponent(double amt) {
        if (this.energyComponent instanceof ComponentBaseEnergy energy) {
            energy.useEnergy(amt);
        } else if (this.energyComponent instanceof Energy energy) {
            energy.useEnergy(amt);
        }
    }

    private void addEnergyToComponent(double amt) {
        if (this.energyComponent instanceof ComponentBaseEnergy energy) {
            energy.addEnergy(amt);
        } else if (this.energyComponent instanceof Energy energy) {
            energy.addEnergy(amt);
        }
    }

    private long calculateMaximumAmountToImport(final TileEntity tile, final IMEMonitor<IAEItemStack> inv) {
        long toSend = 0;

        List<Object> comp = Utils.findEnergyComponents(tile);

        boolean found = false;
        for (Object cmp : comp) {
            if (found) break;
            if (cmp instanceof ComponentBaseEnergy energy && !energy.sendingSidabled && !energy.sourceDirections.isEmpty()) {
                switch (energy.getType()) {
                    default -> {
                        return 0;
                    }

                    case QUANTUM -> {
                        this.currentEnergy = CrazyAE.definitions().items().QEEnergyAsAeStack();
                        toSend = (long) Math.min(energy.getEnergy(), this.itemsToSend);
                        found = true;
                        this.energyComponent = cmp;
                    }

                    case SOLARIUM -> {
                        this.currentEnergy = CrazyAE.definitions().items().SEEnergyAsAeStack();
                        toSend = (long) Math.min(energy.getEnergy(), this.itemsToSend);
                        found = true;
                        this.energyComponent = cmp;
                    }
                }
            } else if (cmp instanceof Energy energy && !energy.sendingSidabled && !energy.sourceDirections.isEmpty()) {
                this.currentEnergy = CrazyAE.definitions().items().EFEnergyAsAeStack();
                toSend = (long) Math.min(energy.getEnergy(), this.itemsToSend);
                found = true;
                this.energyComponent = cmp;
            }
        }

        if (toSend > 0) {
            IAEItemStack itemAmountNotStorable = null;

            if (this.currentEnergy != null) {
                itemAmountNotStorable = inv.injectItems(
                        AEUtils.createAEStackFromItemstack(
                                this.currentEnergy.maybeStack(1).orElse(ItemStack.EMPTY),
                                toSend
                        ),
                        Actionable.SIMULATE, this.source
                );
            }

            if (itemAmountNotStorable != null) {
                if (toSend == itemAmountNotStorable.getStackSize()) {
                    return 0;
                }

                return (int) Math.min(toSend - itemAmountNotStorable.getStackSize(), toSend);
            }

        }

        return toSend;
    }

    @Override
    protected boolean isSleeping() {
        return this.getVictim() == null || super.isSleeping();
    }

    @Override
    public RedstoneMode getRSMode() {
        return (RedstoneMode) this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    @Override
    public @NotNull IPartModel getStaticModels() {
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
        return CrazyAE.definitions().parts().energyImportBus();
    }

    public double getDemandedEnergy(IItemDefinition what) {
        return this.availableEnergy(what);
    }

    public double availableEnergy(IItemDefinition what) {
        if (what != null) {
            IAEItemStack simulate = AEUtils.injectToME(
                    this.getEnergyInv(),
                    AEUtils.createAEStackFromDefinition(
                            what,
                            Long.MAX_VALUE
                    ),
                    this.source,
                    Actionable.SIMULATE
            );

            return simulate != null && simulate.getStackSize() == Long.MAX_VALUE ? 0
                    : Math.min(Math.min(this.calculateEFEnergyToSend(), (Long.MAX_VALUE - (simulate == null ? 0 : simulate.getStackSize()))), (this.maxConfigEnergy == 0 ? Long.MAX_VALUE : this.maxConfigEnergy));
        }

        return 0;
    }

    public double receiveEnergy(double var2, IItemDefinition what) {
        IAEItemStack ais = AEUtils.injectToME(
                this.getEnergyInv(),
                AEUtils.createAEStackFromDefinition(
                        what,
                        (long) var2
                ),
                this.source,
                Actionable.MODULATE
        );
        return ais == null ? 0 : ais.getStackSize();

    }

    public void receiveEnergy(IItemDefinition what, double amt) {
        this.receiveEnergy(amt, what);
    }

    @Override
    public long getMaxConfigEnergy() {
        return this.maxConfigEnergy;
    }

    @Override
    public void setMaxConfigEnergy(long amt) {
        this.maxConfigEnergy = Math.max(Math.min(this.calculateEFEnergyToSend(), amt), 1);
    }

    public void updateMaxConfigEnergy() {
        if (this.maxConfigEnergy > this.calculateEFEnergyToSend()) {
            this.maxConfigEnergy = Math.max(this.calculateEFEnergyToSend(), 1);
        }
    }


    @Override
    public ItemStack getItemStackRepresentation() {
        return CrazyAE.definitions().parts().energyImportBus().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public EnergyBusType getBusType() {
        return EnergyBusType.IMPORT;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        IAEItemStack ais = AEUtils.injectToME(
                this.getEnergyInv(),
                AEUtils.createAEStackFromDefinition(
                        CrazyAE.definitions().items().FEEnergyAsAeStack(),
                        maxReceive
                ),
                this.source,
                simulate ? Actionable.SIMULATE : Actionable.MODULATE
        );
        return ais == null ? maxReceive : (int) ais.getStackSize();
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.getMaxEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) this.availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack());
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public boolean emitsTo(IAcceptor var1, EnumFacing var2) {
        return true;
    }

    public int receiveEnergy(EnumFacing var1, int var2, boolean var3) {
        return this.receiveEnergy(var2, var3);
    }
}
