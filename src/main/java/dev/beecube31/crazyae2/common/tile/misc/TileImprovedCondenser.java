package dev.beecube31.crazyae2.common.tile.misc;

import appeng.api.config.Actionable;
import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.inventories.BaseInternalInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.*;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigManager;
import appeng.util.InsertionOnlyResourceHandlerWithJournal;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import dev.beecube31.crazyae2.common.item.material.CrazyStorageComponentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class TileImprovedCondenser extends AEBaseInvBlockEntity implements IConfigurableObject {
    public static final int BYTE_MULTIPLIER = 8;
    public static final int INPUT_SLOTS = 25;
    public static final int OUTPUT_SLOTS = 4;

    private final ConfigManager cm = new ConfigManager(() -> {
        saveChanges();
        addPower(0);
    });

    private final AppEngInternalInventory outputSlot = new AppEngInternalInventory(this, OUTPUT_SLOTS);
    private final AppEngInternalInventory storageSlot = new AppEngInternalInventory(this, 1);
    private final InternalInventory inputSlot = new CondenseItemHandler();
    private final ResourceHandler<FluidResource> fluidHandler = new CondenseResourceHandler<>(
            FluidResource.EMPTY,
            1.0 / AEKeyType.fluids().getAmountPerOperation(),
            AEFluidKey.AMOUNT_BUCKET);
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
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        this.cm.writeToNBT(data);
        data.putDouble("storedPower", this.storedPower);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.cm.readFromNBT(data);
        this.storedPower = data.getDoubleOr("storedPower", 0);
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
        }
    }

    boolean canAddOutput() {
        return this.outputSlot.addItems(this.getOutput(), true).isEmpty();
    }

    InternalInventory getOutputSlot() {
        return this.outputSlot;
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
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
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

    public ResourceHandler<ItemResource> getExternalItemHandler() {
        return this.externalInv.toResourceHandler();
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.fluidHandler;
    }

    public MEStorage getMEStorage() {
        return this.meStorage;
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

    private class CondenseResourceHandler<T extends Resource>
            extends InsertionOnlyResourceHandlerWithJournal<T, Double> {
        private final double energyFactor;
        private final int maxAmountPerOperation;

        CondenseResourceHandler(T emptyResource, double energyFactor, int maxAmountPerOperation) {
            super(emptyResource);
            this.energyFactor = energyFactor;
            this.maxAmountPerOperation = maxAmountPerOperation;
            this.pendingSideEffect = 0D;
        }

        @Override
        public int insert(T resource, int maxAmount, TransactionContext transaction) {
            if (resource.isEmpty() || !canAddOutput()) {
                return 0;
            }
            var amount = Math.min(maxAmountPerOperation, maxAmount);
            updateSnapshots(transaction);
            pendingSideEffect += amount * energyFactor;
            return amount;
        }

        @Override
        protected void onRootCommit(Double originalState) {
            addPower(pendingSideEffect);
            pendingSideEffect = 0.0;
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
