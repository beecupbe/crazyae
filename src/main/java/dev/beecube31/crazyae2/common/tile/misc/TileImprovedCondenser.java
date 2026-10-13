package dev.beecube31.crazyae2.common.tile.misc;

import appeng.api.config.Actionable;
import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.inventories.BaseInternalInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.capabilities.Capabilities;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigManager;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import dev.beecube31.crazyae2.common.item.material.CrazyStorageComponentItem;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvTileOC;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TileImprovedCondenser extends CrazyAENetworkInvTileOC implements IConfigurableObject {
    public static final int BYTE_MULTIPLIER = 8;
    public static final int INPUT_SLOTS = 25;
    public static final int OUTPUT_SLOTS = 4;

    private final ConfigManager cm = new ConfigManager(() -> {
        saveChanges();
        addPower(0);
    });

    private final CrazyAEInternalInv outputSlot = new CrazyAEInternalInv(this, OUTPUT_SLOTS);
    private final CrazyAEInternalInv storageSlot = new CrazyAEInternalInv(this, 1);
    private final InternalInventory inputSlot = new CondenseItemHandler();
    private final IFluidHandler fluidHandler = new FluidHandler();
    private final MEStorage meStorage = new CondenserStorage();

    private final InternalInventory externalInv = new CombinedInternalInventory(this.inputSlot,
            new FilteredInternalInventory(this.outputSlot, AEItemFilters.EXTRACT_ONLY));
    private final InternalInventory combinedInv = new CombinedInternalInventory(this.inputSlot, this.outputSlot,
            this.storageSlot);

    private double storedPower = 0;

    public TileImprovedCondenser(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.storageSlot.setMaxStackSize(0, 1);
        this.cm.registerSetting(Settings.CONDENSER_OUTPUT, CondenserOutput.TRASH);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.cm.writeToNBT(data);
        data.putDouble("storedPower", this.storedPower);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.cm.readFromNBT(data);
        this.storedPower = data.getDouble("storedPower");
    }

    public double getStorage() {
        var is = this.storageSlot.getStackInSlot(0);
        if (is.isEmpty()) {
            return 0;
        }
        if (is.getItem() instanceof CrazyStorageComponentItem crazy) {
            return (double) crazy.getBytesLong() * BYTE_MULTIPLIER;
        }
        if (is.getItem() instanceof IStorageComponent sc && sc.isStorageComponent(is)) {
            return (double) sc.getBytes(is) * BYTE_MULTIPLIER;
        }
        return 0;
    }

    public void addPower(double rawPower) {
        this.setStoredPower(Math.max(0.0, Math.min(this.getStorage(), this.storedPower + rawPower)));
        this.fillOutput();
    }

    private void fillOutput() {
        double requiredPower = this.getRequiredPower();
        if (requiredPower <= 0 || this.getOutput().isEmpty()) {
            return;
        }
        while (requiredPower <= this.storedPower && this.canAddOutput()) {
            this.setStoredPower(this.storedPower - requiredPower);
            this.outputSlot.addItems(this.getOutput());
            this.addCompletedOperations();
        }
    }

    boolean canAddOutput() {
        return this.outputSlot.addItems(this.getOutput(), true).isEmpty();
    }

    private ItemStack getOutput() {
        return switch (this.cm.getSetting(Settings.CONDENSER_OUTPUT)) {
            case MATTER_BALLS -> AEItems.MATTER_BALL.stack();
            case SINGULARITY -> AEItems.SINGULARITY.stack();
            default -> ItemStack.EMPTY;
        };
    }

    public double getRequiredPower() {
        return this.cm.getSetting(Settings.CONDENSER_OUTPUT).requiredPower;
    }

    public double getStoredPower() {
        return this.storedPower;
    }

    private void setStoredPower(double storedPower) {
        if (this.storedPower != storedPower) {
            this.storedPower = storedPower;
            this.setChanged();
        }
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.combinedInv;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        if (inv == this.outputSlot) {
            this.fillOutput();
        } else if (inv == this.storageSlot) {
            this.addPower(0);
        }
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.cm;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return (LazyOptional<T>) LazyOptional.of(this.externalInv::toItemHandler);
        } else if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return (LazyOptional<T>) LazyOptional.of(() -> this.fluidHandler);
        } else if (capability == Capabilities.STORAGE) {
            return (LazyOptional<T>) LazyOptional.of(() -> this.meStorage);
        }
        return super.getCapability(capability, facing);
    }

    private class CondenseItemHandler extends BaseInternalInventory {
        @Override
        public int size() {
            return INPUT_SLOTS;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return canAddOutput();
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            if (!stack.isEmpty()) {
                addPower(stack.getCount());
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!canAddOutput()) {
                return stack;
            }
            if (!simulate && !stack.isEmpty()) {
                addPower(stack.getCount());
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    }

    private class FluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? AEFluidKey.AMOUNT_BUCKET : 0;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 && !stack.isEmpty();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !canAddOutput()) {
                return 0;
            }
            int amount = Math.min(resource.getAmount(), AEFluidKey.AMOUNT_BUCKET);
            if (action.execute()) {
                var what = AEFluidKey.of(resource);
                if (what != null) {
                    addPower(amount / (double) what.getAmountPerOperation());
                }
            }
            return amount;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }

    private class CondenserStorage implements MEStorage {
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            MEStorage.checkPreconditions(what, amount, mode, source);
            if (!canAddOutput()) {
                return 0;
            }
            if (mode == Actionable.MODULATE) {
                addPower(amount / (double) what.getAmountPerOperation());
            }
            return amount;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            MEStorage.checkPreconditions(what, amount, mode, source);
            if (!(what instanceof AEItemKey itemKey)) {
                return 0;
            }
            long extracted = 0;
            for (int i = 0; i < outputSlot.size() && extracted < amount; i++) {
                if (itemKey.matches(outputSlot.getStackInSlot(i))) {
                    int count = (int) Math.min(amount - extracted, Integer.MAX_VALUE);
                    extracted += outputSlot.extractItem(i, count, mode == Actionable.SIMULATE).getCount();
                }
            }
            return extracted;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            for (int i = 0; i < outputSlot.size(); i++) {
                var stack = outputSlot.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    out.add(AEItemKey.of(stack), stack.getCount());
                }
            }
        }

        @Override
        public Component getDescription() {
            return getBlockState().getBlock().getName();
        }
    }
}
