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
import cofh.redstoneflux.api.IEnergyReceiver;
import com.denfop.api.energy.IEnergyTile;
import com.denfop.api.energy.event.EnergyTileLoadEvent;
import com.denfop.api.energy.event.EnergyTileUnLoadEvent;
import com.denfop.api.sytem.*;
import com.denfop.componets.ComponentBaseEnergy;
import com.denfop.componets.Energy;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.components.ComponentEFEnergySource;
import dev.beecube31.crazyae2.common.components.ComponentEUEnergySource;
import dev.beecube31.crazyae2.common.components.ComponentMoreEnergySource;
import dev.beecube31.crazyae2.common.components.ComponentRFEnergySource;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

import static dev.beecube31.crazyae2.common.util.ModsChecker.*;

@SuppressWarnings("unused")
public class PartEnergyExportBus extends CrazyAEPartSharedBus implements IEnergyBus, IEnergyStorage, IPartActivationOverrider {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/energy_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/energy_bus_has_channel"));

    private final IActionSource source;
    private long itemsToSend = 32;
    private IItemDefinition currentEnergy = null;
    private Object energyComponent = null;
    private boolean worked;
    private long maxConfigEnergy = 0;

    public double pastEnergy;
    public double perEnergy;
    public final List<InfoTile<IEnergyTile>> validTEs = new ArrayList<>();
    public final List<InfoTile<ITile>> validTEsQS = new ArrayList<>();
    public final Map<EnumFacing, IEnergyTile> energyConductorMap = new HashMap<>();
    public final Map<EnumFacing, ITile> energyConductorMapQS = new HashMap<>();
    public long id;
    public int hashCodeSource;

    private ComponentRFEnergySource rfDelegate;
    private ComponentEUEnergySource euDelegate;
    private ComponentEFEnergySource energyDelegate;
    private ComponentMoreEnergySource solariumDelegate;
    private ComponentMoreEnergySource quantumDelegate;

    @Reflected
    public PartEnergyExportBus(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.source = new MachineSource(this);
    }

    public ComponentRFEnergySource getRfDelegate() {
        return this.rfDelegate;
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
                    player.sendMessage(new TextComponentString("SAVED1"));
                }
            } else {
                final String storedName = memoryCard.getSettingsName(memCardIS);
                final NBTTagCompound data = memoryCard.getData(memCardIS);
                if (name.equals(storedName)) {
                    this.uploadSettings(SettingsFrom.MEMORY_CARD, data, player);
                    player.sendMessage(new TextComponentString("SAVED2"));
                } else {
                    memoryCard.notifyUser(player, MemoryCardMessages.INVALID_MACHINE);
                    player.sendMessage(new TextComponentString("INVALID_MACHINE1"));
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
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        super.onChangeInventory(inv, slot, mc, removedStack, newStack);
        if (inv == this.upgrades) {
            this.updateMaxConfigEnergy();
        }
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        if (Platform.isServer()) {
            this.rfDelegate = RFLUX_LOADED ? new ComponentRFEnergySource(this)
                    : null;

            this.euDelegate = IC2_LOADED ? new ComponentEUEnergySource(this)
                    : null;

            this.energyDelegate = IU_LOADED ? new ComponentEFEnergySource(this)
                    : null;

            this.solariumDelegate = IU_LOADED ? new ComponentMoreEnergySource(CrazyAE.definitions().items().SEEnergyAsAeStack(), this)
                    : null;

            this.quantumDelegate = IU_LOADED ? new ComponentMoreEnergySource(CrazyAE.definitions().items().QEEnergyAsAeStack(), this)
                    : null;

            if (this.euDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new ic2.api.energy.event.EnergyTileLoadEvent(this.euDelegate));
            }

            if (this.energyDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this.getTile().getWorld(), this.energyDelegate));
            }

            if (this.solariumDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new EnergyEvent(this.getTile().getWorld(), EnumTypeEvent.LOAD, EnergyType.SOLARIUM, this.solariumDelegate));
            }

            if (this.quantumDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new EnergyEvent(this.getTile().getWorld(), EnumTypeEvent.LOAD, EnergyType.QUANTUM, this.quantumDelegate));
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
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnLoadEvent(this.getTile().getWorld(), this.energyDelegate));
            }

            if (this.solariumDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new EnergyEvent(this.getTile().getWorld(), EnumTypeEvent.UNLOAD, EnergyType.SOLARIUM, this.solariumDelegate));
            }

            if (this.quantumDelegate != null) {
                MinecraftForge.EVENT_BUS.post(new EnergyEvent(this.getTile().getWorld(), EnumTypeEvent.UNLOAD, EnergyType.QUANTUM, this.quantumDelegate));
            }
        }
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 5;
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
        if (!canDoBusWork()) return TickRateModulation.IDLE;
        this.energyComponent = null;
        this.currentEnergy = null;
        this.itemsToSend = -1L;
        this.worked = false;
        this.itemsToSend = Math.min(calculateEFEnergyToSend(), (this.maxConfigEnergy == 0L) ? Long.MAX_VALUE : this.maxConfigEnergy);
        TileEntity te = getVictim();
        if (IU_LOADED && Utils.isIUBlock(te)) {
            if (this.itemsToSend > 0L && Utils.findEnergyComponents(te).isEmpty()) {
                try {
                    IMEMonitor<IAEItemStack> inv = getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

                    while (this.itemsToSend > 0) {
                        if (this.exportStuff(te, inv)) {
                            break;
                        }
                    }

                } catch (GridAccessException ignored) {}
            } else {
                return TickRateModulation.SLEEP;
            }

        } else if (te instanceof IEnergyStorage storage) {
            if (storage.canReceive()) {
                int received = storage.receiveEnergy((int)availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);
                IAEItemStack ais = AEUtils.extractFromME(
                        this.getEnergyInv(),
                        AEUtils.createAEStackFromDefinition(
                                CrazyAE.definitions().items().FEEnergyAsAeStack(),
                                received
                        ),
                        this.source,
                        Actionable.MODULATE
                );

                if (ais != null && ais.getStackSize() == received) this.worked = true;
            }
        } else if (Loader.isModLoaded("redstoneflux") && te instanceof IEnergyReceiver r) {
            int received = r.receiveEnergy(getSide().getFacing(), (int)availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);
            IAEItemStack ais = AEUtils.extractFromME(
                    this.getEnergyInv(),
                    AEUtils.createAEStackFromDefinition(
                            CrazyAE.definitions().items().FEEnergyAsAeStack(),
                            received
                    ),
                    this.source,
                    Actionable.MODULATE
            );
            if (ais != null && ais.getStackSize() == received) this.worked = true;
        } else if (te != null) {
            tryExportToFEComponents(te);
        }
        return this.worked ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    private void tryExportToFEComponents(final TileEntity te) {
        try {
            for (Field field : te.getClass().getDeclaredFields()) {
                if (IEnergyStorage.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    IEnergyStorage candidate = (IEnergyStorage) field.get(null);

                    if (candidate.canReceive()) {
                        int received = candidate.receiveEnergy((int) this.availableEnergy(CrazyAE.definitions().items().FEEnergyAsAeStack()), false);

                        IAEItemStack ais = AEUtils.extractFromME(
                                this.getEnergyInv(),
                                AEUtils.createAEStackFromDefinition(
                                        CrazyAE.definitions().items().FEEnergyAsAeStack(),
                                        received
                                ),
                                this.source,
                                Actionable.MODULATE
                        );

                        if (ais != null && ais.getStackSize() == received) {
                            this.worked = true;
                            break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException ignored) {}
    }

    private boolean exportStuff(TileEntity tile, IMEMonitor<IAEItemStack> inv) {
        long toSend = calculateMaximumAmountToExport(tile, inv);
        if (toSend == 0L) return true;

        IAEItemStack extracted = AEUtils.extractFromME(
                inv,
                AEUtils.createAEStackFromDefinition(
                        this.currentEnergy,
                        toSend
                ),
                this.source,
                Actionable.MODULATE
        );

        if (extracted != null && extracted.getStackSize() > 0L) {
            toSend = extracted.getStackSize();
            addEnergyToComponent(toSend);
            this.itemsToSend -= toSend;
            this.worked = true;
            return false;
        }

        return true;
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

    private long calculateMaximumAmountToExport(final TileEntity tile, final IMEMonitor<IAEItemStack> inv) {
        long toSend = 0;

        List<Object> comp = Utils.findEnergyComponents(tile);

        boolean found = false;
        for (Object cmp : comp) {
            if (found) break;
            if (cmp instanceof ComponentBaseEnergy energy && !energy.receivingDisabled && !energy.sinkDirections.isEmpty()) {
                switch (energy.getType()) {
                    default -> {
                        return 0;
                    }

                    case QUANTUM -> {
                        this.currentEnergy = CrazyAE.definitions().items().QEEnergyAsAeStack();
                        toSend = (long) Math.min(energy.getCapacity() - energy.getEnergy(), this.itemsToSend);
                        found = true;
                        this.energyComponent = cmp;
                    }

                    case SOLARIUM -> {
                        this.currentEnergy = CrazyAE.definitions().items().SEEnergyAsAeStack();
                        toSend = (long) Math.min(energy.getCapacity() - energy.getEnergy(), this.itemsToSend);
                        found = true;
                        this.energyComponent = cmp;
                    }
                }
            } else if (cmp instanceof Energy energy && !energy.receivingDisabled && !energy.sinkDirections.isEmpty()) {
                this.currentEnergy = CrazyAE.definitions().items().EFEnergyAsAeStack();
                toSend = (long) Math.min(energy.getCapacity() - energy.getEnergy(), this.itemsToSend);
                found = true;
                this.energyComponent = cmp;
            }
        }

        if (toSend > 0) {
            IAEItemStack extracted = null;

            if (this.currentEnergy != null) {
                extracted = inv.extractItems(
                        AEUtils.createAEStackFromItemstack(
                                this.currentEnergy.maybeStack(1).orElse(ItemStack.EMPTY),
                                toSend
                        ),
                        Actionable.SIMULATE, this.source
                );
            }

            return extracted != null ? extracted.getStackSize() : 0;

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
        return CrazyAE.definitions().parts().improvedImportBus();
    }

    public List<InfoTile<IEnergyTile>> getValidReceivers() {
        return this.validTEs;
    }

    public TileEntity getTileEntity() {
        return this.getHost().getTile();
    }

    public BlockPos getBlockPos() {
        return this.getHost().getTile().getPos();
    }

    public long getIdNetwork() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void AddTile(EnergyType energyType, ITile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            if (!this.energyConductorMapQS.containsKey(dir)) {
                this.energyConductorMapQS.put(dir, tile);
                validTEsQS.add(new InfoTile<>(tile, dir.getOpposite()));
            }
        }
    }

    public void RemoveTile(EnergyType energyType, ITile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            this.energyConductorMapQS.remove(dir);
            final Iterator<InfoTile<ITile>> iter = validTEsQS.iterator();
            while (iter.hasNext()){
                InfoTile<ITile> tileInfoTile = iter.next();
                if (tileInfoTile.tileEntity == tile) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    public Map<EnumFacing, ITile> getTiles(EnergyType energyType) {
        return this.energyConductorMapQS;
    }

    public List<InfoTile<ITile>> getValidReceivers(EnergyType energyType) {
        return this.validTEsQS;
    }

    public void AddTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            this.energyConductorMap.put(dir, tile);
            this.validTEs.add(new InfoTile<>(tile, dir.getOpposite()));
        }
    }

    public void RemoveTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            this.energyConductorMap.remove(dir);
            Iterator<InfoTile<IEnergyTile>> iter = this.validTEs.iterator();
            while (iter.hasNext()) {
                InfoTile<IEnergyTile> tileInfoTile = iter.next();
                if (tileInfoTile.tileEntity == tile) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    public Map<EnumFacing, IEnergyTile> getTiles() {
        return this.energyConductorMap;
    }

    public void setHashCodeSource(int hashCode) {
        this.hashCodeSource = hashCode;
    }

    public int getHashCodeSource() {
        return this.hashCodeSource;
    }

    public double getPerEnergy() {
        return this.perEnergy;
    }

    public double getPastEnergy() {
        return this.pastEnergy;
    }

    public void setPastEnergy(double pastEnergy) {
        this.pastEnergy = pastEnergy;
    }

    public void addPerEnergy(double setEnergy) {
        this.perEnergy = setEnergy;
    }

    public boolean isSource() {
        return true;
    }

    private IItemDefinition getEnergyDefinitionFromCable() {
        TileEntity te = getVictim();
        if (te != null)
            return (te instanceof com.denfop.tiles.transport.tiles.TileEntitySCable) ? CrazyAE.definitions().items().SEEnergyAsAeStack() : (
                    (te instanceof com.denfop.tiles.transport.tiles.TileEntityQCable) ? CrazyAE.definitions().items().QEEnergyAsAeStack() : (
                            (te instanceof com.denfop.tiles.transport.tiles.TileEntityCable || te instanceof com.denfop.tiles.transport.tiles.TileEntityUniversalCable) ? CrazyAE.definitions().items().EFEnergyAsAeStack() :
                                    null));
        return null;
    }

    public double canExtractEnergy() {
        return this.availableEnergy(this.getEnergyDefinitionFromCable());
    }

    public double canExtractEnergy(IItemDefinition def) {
        return this.availableEnergy(def);
    }

    public double availableEnergy(IItemDefinition what) {
        IMEMonitor<IAEItemStack> inv = this.getEnergyInv();
        if (what != null && inv != null) {
            IAEItemStack simulate = AEUtils.extractFromME(
                    inv,
                    AEUtils.createAEStackFromDefinition(
                            what, Long.MAX_VALUE
                    ),
                    this.source,
                    Actionable.SIMULATE
            );
            return (simulate == null) ? 0.0D : Math.min(Math.min(this.calculateEFEnergyToSend(), simulate.getStackSize()), (this.maxConfigEnergy == 0 ? Long.MAX_VALUE : this.maxConfigEnergy));
        }
        return 0.0D;
    }

    public double canProvideEnergy() {
        return this.availableEnergy(this.getEnergyDefinitionFromCable());
    }

    public void extractEnergy(double var1) {
        IItemDefinition what = this.getEnergyDefinitionFromCable();

        if (what != null) {
            AEUtils.extractFromME(
                    this.getEnergyInv(),
                    AEUtils.createAEStackFromDefinition(
                            what,
                            (long) var1
                    ),
                    this.source,
                    Actionable.MODULATE
            );
        }
    }

    public void extractEnergy(double var1, IItemDefinition what) {
        if (what != null) {
            AEUtils.extractFromME(
                    this.getEnergyInv(),
                    AEUtils.createAEStackFromDefinition(
                            what,
                            (long) var1
                    ),
                    this.source,
                    Actionable.MODULATE
            );
        }
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
        return CrazyAE.definitions().parts().energyExportBus().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        IAEItemStack ais = AEUtils.extractFromME(
                this.getEnergyInv(),
                AEUtils.createAEStackFromDefinition(
                        CrazyAE.definitions().items().FEEnergyAsAeStack(),
                        maxExtract
                ),
                this.source,
                simulate ? Actionable.SIMULATE : Actionable.MODULATE
        );
        return ais == null ? 0 : (int) ais.getStackSize();
    }

    @Override
    public int getEnergyStored() {
        return this.getMaxEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        IAEItemStack simulate = AEUtils.extractFromME(
                this.getEnergyInv(),
                AEUtils.createAEStackFromDefinition(
                        CrazyAE.definitions().items().FEEnergyAsAeStack(),
                        Long.MAX_VALUE
                ),
                this.source,
                Actionable.SIMULATE
        );

        return simulate == null ? 0 : (int) Math.min(Math.min(this.calculateEFEnergyToSend(), simulate.getStackSize()), (this.maxConfigEnergy == 0 ? Long.MAX_VALUE : this.maxConfigEnergy));
    }

    @Override
    public EnergyBusType getBusType() {
        return EnergyBusType.EXPORT;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
