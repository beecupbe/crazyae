package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.me.helpers.MachineSource;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

public class TileBigCrystalCharger extends AENetworkedInvBlockEntity implements IUpgradeableObject, IGridTickable {

    public static final ResourceLocation INV_INPUT = CrazyAE.makeId("input");
    public static final ResourceLocation INV_OUTPUT = CrazyAE.makeId("output");

    private static final int NUMBER_OF_UPGRADE_SLOTS = 5;

    private final IUpgradeInventory upgrades;

    private final AppEngInternalInventory inputInv = new CrazyAEInternalInv(this, 18, 64);
    private final AppEngInternalInventory outputInv = new CrazyAEInternalInv(this, 18, 64);

    private int progressPerTick = 1;
    private int progress = 0;
    private int outputItemsPerJob = 0;

    private boolean isPowered = false;
    private final IActionSource actionSource = new MachineSource(this);


    public TileBigCrystalCharger(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);

        this.getMainNode().setIdlePowerUsage(16.0);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().addService(IGridTickable.class, this);

        this.upgrades = UpgradeInventories.forMachine(CrazyBlocks.IMPROVED_CRYSTAL_CHARGER.get(), NUMBER_OF_UPGRADE_SLOTS, this::onUpgradesChanged);
        this.inputInv.setFilter(RestrictedSlot.PlaceableItemType.CERTUS_QUARTZ_CRYSTALS.associatedFilter);
        this.outputInv.setFilter(RestrictedSlot.PlaceableItemType.CHARGED_CERTUS_QUARTZ_CRYSTALS.associatedFilter);
        this.checkUpgrades();
    }

    private void onUpgradesChanged() {
        this.checkUpgrades();
        this.saveChanges();
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        final boolean c = super.readFromStream(data);
        final boolean oldPower = this.isPowered;
        this.isPowered = data.readBoolean();
        return this.isPowered != oldPower || c;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isPowered);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.inputInv.writeToNBT(data, "inputInv", registries);
        this.outputInv.writeToNBT(data, "outputInv", registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        this.inputInv.readFromNBT(data, "inputInv", registries);
        this.outputInv.readFromNBT(data, "outputInv", registries);
        this.checkUpgrades();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inputInv;
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        } else if (id.equals(INV_INPUT)) {
            return this.inputInv;
        } else if (id.equals(INV_OUTPUT)) {
            return this.outputInv;
        }

        return super.getSubInventory(id);
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (this.inputInv == inv) {
            this.checkTasks();
        }

        if (this.upgrades == inv) {
            this.checkUpgrades();
        }
        this.saveChanges();
    }

    private void clearProgress() {
        this.progress = 0;
    }

    private void checkTasks() {
        if (!this.hasWork()) {
            this.clearProgress();
        }
        this.getMainNode().ifPresent((grid, node) -> {
            if (this.hasWork()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        for (int upgradeIndex = 0; upgradeIndex < this.upgrades.size(); upgradeIndex++) {
            final ItemStack stackInSlot = this.upgrades.getStackInSlot(upgradeIndex);

            if (!stackInSlot.isEmpty()) {
                drops.add(stackInSlot);
            }
        }

        for (final ItemStack is : this.inputInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.outputInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    public int getInstalledUpgrades(final ItemLike u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    @Nonnull
    @Override
    public TickingRequest getTickingRequest(@Nonnull IGridNode iGridNode) {
        this.checkUpgrades();
        return new TickingRequest(1, 1, !this.hasWork());
    }

    @Nonnull
    @Override
    public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
        var grid = this.getMainNode().getGrid();
        if (grid == null || !this.hasWork()) {
            return TickRateModulation.SLEEP;
        }
        if (Utils.canExtractEnergy(grid.getEnergyService(), 16, Actionable.MODULATE, PowerMultiplier.CONFIG)) {
            this.progress += this.progressPerTick;

            if (this.progress >= 100) {
                this.progress = 0;
                int itemsLeft = this.outputItemsPerJob;
                for (int j = 0; j < this.inputInv.size(); j++) {
                    if (this.inputInv.getStackInSlot(j).getCount() > 0) {
                        if (!AEItems.CERTUS_QUARTZ_CRYSTAL.isSameAs(this.inputInv.getStackInSlot(j))) {
                            continue;
                        }

                        int amt = Math.min(Math.min(itemsLeft, 64 - this.outputInv.getStackInSlot(j).getCount()), this.inputInv.getStackInSlot(j).getCount());
                        if (amt <= 0) {
                            continue;
                        }
                        var rest = this.outputInv.insertItem(j, AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.stack(amt), false);
                        amt -= rest.getCount();
                        this.inputInv.extractItem(j, amt, false);
                        itemsLeft -= amt;
                        if (itemsLeft <= 0) {
                            break;
                        }
                    }
                }

                this.pushOut();
            }
            return TickRateModulation.SAME;
        }

        return TickRateModulation.IDLE;
    }

    private void pushOut() {
        if (!this.outputInv.isEmpty()) {
            var grid = this.getMainNode().getGrid();
            if (grid == null) {
                return;
            }
            var storage = grid.getStorageService().getInventory();

            for (int j = 0; j < this.outputInv.size(); j++) {
                if (this.outputInv.getStackInSlot(j).getCount() > 0) {
                    int amt = this.outputInv.getStackInSlot(j).getCount();
                    GenericStack copy = GenericStack.fromItemStack(this.outputInv.getStackInSlot(j));
                    if (copy != null) {
                        long inserted = storage.insert(copy.what(), amt, Actionable.MODULATE, this.actionSource);
                        this.outputInv.extractItem(j, (int) inserted, false);
                    }
                }
            }
        }
    }

    private void checkUpgrades() {
        this.progressPerTick = 0;
        this.outputItemsPerJob = 0;

        switch (this.getInstalledUpgrades(CrazyItems.IMPROVED_SPEED_CARD.get())) {
            case 1:
                this.progressPerTick += 10;
                break;
            case 2:
                this.progressPerTick += 15;
                break;
            case 3:
                this.progressPerTick += 25;
                break;
            case 4:
                this.progressPerTick += 40;
                break;
            case 5:
                this.progressPerTick += 60;
                break;
        }

        if (this.getInstalledUpgrades(CrazyItems.ADVANCED_SPEED_CARD.get()) > 0) {
            this.progressPerTick = 100;
        }

        switch (this.getInstalledUpgrades(CrazyItems.STACKS_CARD.get())) {
            case 1:
                this.outputItemsPerJob += 64;
                break;
            case 2:
                this.outputItemsPerJob += 192;
                break;
            case 3:
                this.outputItemsPerJob += 384;
                break;
            case 4:
                this.outputItemsPerJob += 768;
                break;
            case 5:
                this.outputItemsPerJob += 1152;
                break;
        }

        if (this.outputItemsPerJob == 0) this.outputItemsPerJob = 1;
        if (this.progressPerTick == 0) this.progressPerTick = 1;
    }

    public int getProgress() {
        return this.progress;
    }

    private boolean hasWork() {
        return !this.inputInv.isEmpty();
    }

    public void openMenu(Player player) {
        ContainerOpener.open(CrazyContainers.IMPROVED_CRYSTAL_CHARGER.get(), player, MenuLocators.forBlockEntity(this));
    }
}
