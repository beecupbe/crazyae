package dev.beecube31.crazyae2.common.tile.crafting;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import appeng.util.inv.AppEngInternalInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.HolderLookup;
import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.me.helpers.MachineSource;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.config.CrazyAEMachinesConfig;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class TileImprovedMAC extends AENetworkedInvBlockEntity implements IUpgradeableObject, IGridTickable, ICraftingProvider {

    public static final Identifier INV_MAIN = CrazyAE.makeId("imp_mac_patterns");

    private final int maxQueueSize = 160;
    private int itemsToCraftPerTick = 8;

    private final CrazyAEInternalInv patternsInv = new CrazyAEInternalInv(this, 45).setItemFilter(RestrictedSlot.PlaceableItemType.ENCODED_CRAFTING_PATTERN.associatedFilter);
    private final IUpgradeInventory upgrades;
    private final IActionSource actionSource = new MachineSource(this);

    private final List<IPatternDetails> activeJobs = new ArrayList<>();
    private List<IPatternDetails> cachedPatterns = new ArrayList<>();

    private boolean isActive = false;

    public TileImprovedMAC(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode().setIdlePowerUsage(32.0)
                .addService(IGridTickable.class, this)
                .addService(ICraftingProvider.class, this)
                .setFlags(GridFlags.REQUIRE_CHANNEL);
        this.upgrades = UpgradeInventories.forMachine(CrazyBlocks.IMPROVED_MAC.get(), getUpgradeSlots(), this::checkUpgrades);
    }

    private int getUpgradeSlots() {
        return 5;
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return this.cachedPatterns;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!isBusy()) {
            this.activeJobs.add(patternDetails);
            getMainNode().getGrid().getTickManager().wakeDevice(getMainNode().getNode());
            return true;
        }
        return false;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public boolean isBusy() {
        return this.activeJobs.size() >= this.maxQueueSize;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 20, activeJobs.isEmpty());
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, int ticksSinceLastCall) {
        if (!this.activeJobs.isEmpty()) {
            processJobs();
        }

        return this.activeJobs.isEmpty() ? TickRateModulation.SLEEP : TickRateModulation.URGENT;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isActive());
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean ret = super.readFromStream(data);

        final boolean isActive = data.readBoolean();
        ret = isActive != this.isActive || ret;
        this.isActive = isActive;

        return ret;
    }

    public boolean isActive() {
        if (level != null && !level.isClientSide()) {
            return this.getMainNode().isOnline();
        } else {
            return this.isActive;
        }
    }

    private void processJobs() {
        IStorageService storage = getMainNode().getGrid().getService(IStorageService.class);
        if (storage == null) {
            return;
        }
        var monitor = storage.getInventory();

        int processedThisTick = 0;
        Iterator<IPatternDetails> iterator = this.activeJobs.iterator();

        while (iterator.hasNext() && processedThisTick < this.itemsToCraftPerTick) {
            IPatternDetails job = iterator.next();

            var outputs = job.getOutputs();

            boolean canInsertAll = true;
            for (var stack : outputs) {
                long inserted = monitor.insert(stack.what(), stack.amount(), Actionable.SIMULATE, actionSource);
                if (inserted != stack.amount()) {
                    canInsertAll = false;
                    break;
                }
            }

            if (canInsertAll) {
                for (var stack : outputs) {
                    monitor.insert(stack.what(), stack.amount(), Actionable.MODULATE, actionSource);
                }
                iterator.remove();
                processedThisTick++;
            } else {
                break;
            }
        }
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        upgrades.writeToNBT(data, "upgrades");
        patternsInv.writeToNBT(data, "patterns");

        var jobList = data.list("activeJobs", ItemStack.OPTIONAL_CODEC);
        for (IPatternDetails job : activeJobs) {
            jobList.add(job.getDefinition().toStack());
        }
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        upgrades.readFromNBT(data, "upgrades");
        patternsInv.readFromNBT(data, "patterns");

        activeJobs.clear();
        for (ItemStack patternStack : data.listOrEmpty("activeJobs", ItemStack.OPTIONAL_CODEC)) {
            IPatternDetails details = PatternDetailsHelper.decodePattern(patternStack, this.level);
            if (details != null) {
                activeJobs.add(details);
            }
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (inv == this.patternsInv) {
            updateCraftingList();
        }
        if (inv == this.upgrades) {
            checkUpgrades();
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        if (this.level != null && !this.level.isClientSide()) {
            this.checkUpgrades();
            this.updateCraftingList();
            this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        }
    }

    private void updateCraftingList() {
        this.cachedPatterns.clear();
        for (int i = 0; i < patternsInv.size(); i++) {
            ItemStack stack = patternsInv.getStackInSlot(i);
            if (AEItems.CRAFTING_PATTERN.is(stack)) {
                IPatternDetails details = PatternDetailsHelper.decodePattern(stack, this.level);
                if (details != null) {
                    this.cachedPatterns.add(details);
                }
            }
        }

        ICraftingProvider.requestUpdate(getMainNode());
    }

    private void checkUpgrades() {
        switch (upgrades.getInstalledUpgrades(CrazyItems.STACKS_CARD.get())) {
            case 1:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWith1Upgrade.get();
                break;
            case 2:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWith2Upgrades.get();
                break;
            case 3:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWith3Upgrades.get();
                break;
            case 4:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWith4Upgrades.get();
                break;
            case 5:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWith5Upgrades.get();
                break;
            default:
                this.itemsToCraftPerTick = CrazyAEMachinesConfig.COMMON.improvedMolecularAssemblerCraftsPerTickWithoutUpgrades.get();
                break;
        }
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        } else if (id.equals(INV_MAIN)) {
            return this.patternsInv;
        }

        return super.getSubInventory(id);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.patternsInv;
    }

    public void openMenu(Player player) {
        ContainerOpener.open(CrazyContainers.IMPROVED_MAC.get(), player, MenuLocators.forBlockEntity(this));
    }
}
