package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import appeng.helpers.Reflected;
import appeng.items.parts.PartModels;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.PartModel;
import appeng.tile.inventory.AppEngCellInventory;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.util.DrivePartWatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class PartDrive extends CrazyAEPartSharedBus implements IChestOrDrive, IPriorityHost, IPriHostGuiOverrider {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/drive_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/drive_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/drive_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/drive_has_channel"));

    private final IActionSource mySrc;

    private final AppEngCellInventory inv = new AppEngCellInventory(this, 10);
    private final ICellHandler[] handlersBySlot = new ICellHandler[10];
    private final DrivePartWatcher<IAEItemStack>[] invBySlot = new DrivePartWatcher[10];
    private boolean isCached = false;
    private final Map<IStorageChannel<? extends IAEStack<?>>, List<IMEInventoryHandler>> inventoryHandlers;
    private int priority = 0;
    private boolean wasActive = false;

    private int cellState = 0;
    private boolean powered;
    // bit index corresponds to cell index
    private int blinking;

    @Reflected
    public PartDrive(final ItemStack is) {
        super(is);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        this.getConfigManager().registerSetting(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);

        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.inv.setFilter(new CellValidInventoryFilter());
        this.inventoryHandlers = new IdentityHashMap<>();
        this.mySrc = new MachineSource(this);
    }

    @Override
    public void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);

        int newState = 0;
        for (int x = 0; x < this.getCellCount(); x++) {
            newState |= (this.getCellStatus(x) << (3 * x));
        }

        data.writeInt(newState);
        data.writeBoolean(this.getProxy().isActive());
        data.writeInt(this.blinking);
    }

    @Override
    public boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        final int oldCellState = this.cellState;
        final boolean oldPowered = this.powered;
        final int oldBlinking = this.blinking;
        this.cellState = data.readInt();
        this.powered = data.readBoolean();
        this.blinking = data.readInt();
        return oldCellState != this.cellState || oldPowered != this.powered || oldBlinking != this.blinking || c;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.isCached = false;
        this.priority = data.getInteger("priority");
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("priority", this.priority);
    }

    @Override
    public int getCellCount() {
        return 10;
    }

    @Override
    public int getCellStatus(final int slot) {
        if (Platform.isClient()) {
            return (this.cellState >> (slot * 3)) & 0b111;
        }

        final DrivePartWatcher handler = this.invBySlot[slot];
        if (handler == null) {
            return 0;
        }

        return handler.getStatus();
    }

    @Override
    public boolean isPowered() {
        if (Platform.isClient()) {
            return this.powered;
        }

        return this.getProxy().isActive();
    }

    @Override
    public boolean isCellBlinking(final int slot) {
        return (this.blinking & (1 << slot)) == 1;
    }

    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        this.recalculateDisplay();
    }

    @MENetworkEventSubscribe
    public void channelRender(final MENetworkChannelsChanged c) {
        this.recalculateDisplay();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.isCached) {
            this.isCached = false;
            this.updateState();
            this.recalculateDisplay();
        }

        try {
            if (this.getProxy().isActive()) {
                final IStorageGrid gs = this.getProxy().getStorage();
                Platform.postChanges(gs, removed, added, this.mySrc);
            }
            this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException ignored) {
        }

        this.markForUpdate();
    }

    private void recalculateDisplay() {
        final boolean currentActive = this.getProxy().isActive();
        final int oldCellState = this.cellState;
        final boolean oldPowered = this.powered;

        this.powered = currentActive;

        if (this.wasActive != currentActive) {
            this.wasActive = currentActive;
            try {
                this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
            } catch (final GridAccessException e) {
                // :P
            }
        }

        for (int x = 0; x < this.getCellCount(); x++) {
            cellState |= (this.getCellStatus(x) << (3 * x));
        }

        if (oldCellState != this.cellState || oldPowered != this.powered) {
            this.markForUpdate();
        }
    }

    private void markForUpdate() {
        this.getHost().getTile().getWorld().notifyBlockUpdate(
                this.getHost().getTile().getPos(),
                this.getHost().getTile().getWorld().getBlockState(this.getHost().getTile().getPos()),
                this.getHost().getTile().getWorld().getBlockState(this.getHost().getTile().getPos()),
                3
        );
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    private void updateState() {
        if (!this.isCached) {
            final Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels = AEApi.instance().storage().storageChannels();
            storageChannels.forEach(channel -> this.inventoryHandlers.put(channel, new ArrayList<>(10)));

            double power = 2.0;

            for (int x = 0; x < this.inv.getSlots(); x++) {
                final ItemStack is = this.inv.getStackInSlot(x);
                this.invBySlot[x] = null;
                this.handlersBySlot[x] = null;

                if (!is.isEmpty()) {
                    this.handlersBySlot[x] = AEApi.instance().registries().cell().getHandler(is);

                    if (this.handlersBySlot[x] != null) {
                        for (IStorageChannel<? extends IAEStack<?>> channel : storageChannels) {

                            ICellInventoryHandler cell = this.handlersBySlot[x].getCellInventory(is, this, channel);

                            if (cell != null) {
                                this.inv.setHandler(x, cell);
                                power += this.handlersBySlot[x].cellIdleDrain(is, cell);

                                final DrivePartWatcher<IAEItemStack> ih = new DrivePartWatcher<>(cell, is, this.handlersBySlot[x], this);
                                ih.setPriority(this.priority);
                                this.invBySlot[x] = ih;
                                this.inventoryHandlers.get(channel).add(ih);

                                break;
                            }
                        }
                    }
                }
            }

            this.getProxy().setIdlePowerUsage(power);

            this.isCached = true;
        }
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
    }

    @Override
    protected TickRateModulation doBusWork() {
        return TickRateModulation.SAME;
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.updateState();
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(final IStorageChannel channel) {
        this.updateState();
        return this.inventoryHandlers.get(channel);
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(final int newValue) {
        this.priority = newValue;
        this.saveChanges();

        this.isCached = false; // recalculate the storage cell.
        this.updateState();

        try {
            this.getProxy().getGrid().postEvent(new MENetworkCellArrayUpdate());
        } catch (final GridAccessException e) {
            // :P
        }
    }

    @Override
    public void saveChanges(final ICellInventory<?> cellInventory) {
        this.getHost().getTile().getWorld().markChunkDirty(this.getHost().getTile().getPos(), null);
    }

    @Override
    public void blinkCell(final int slot) {
        this.blinking |= (1 << slot);

        this.recalculateDisplay();
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 13, 12, 12, 14);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 5;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (Platform.isServer()) {
            CrazyAEGuiHandler.openGUI(player, this.getHost().getTile(), this.getSide(), this.getOverrideGui());
        }
        return true;
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        if (name.equals("cells")) {
            return this.inv;
        }

        return super.getInventoryByName(name);
    }

    @Override
    public RedstoneMode getRSMode() {
        return RedstoneMode.IGNORE;
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return this.doBusWork();
    }

    @Override
    protected boolean isSleeping() {
        return this.getHandler() == null || super.isSleeping();
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
        return null;
        //return CrazyAE.definitions().parts().partDrive();
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    @Override
    public EnumFacing getForward() {
        return EnumFacing.random(new Random());
    }

    @Override
    public EnumFacing getUp() {
        return EnumFacing.random(new Random());
    }

    @Override public void setOrientation(EnumFacing enumFacing, EnumFacing enumFacing1) {}

    @Override
    public CrazyAEGuiBridge getOverrideGui() {
        return CrazyAEGuiBridge.GUI_DRIVE_PART;
    }

    private class CellValidInventoryFilter implements IAEItemFilter {
        @Override
        public boolean allowExtract(IItemHandler inv, int slot, int amount) {
            return true;
        }

        @Override
        public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
            return !stack.isEmpty() && AEApi.instance().registries().cell().isCellHandled(stack);
        }

    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return AEApi.instance().definitions().blocks().drive().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.GUI_DRIVE;
    }
}
