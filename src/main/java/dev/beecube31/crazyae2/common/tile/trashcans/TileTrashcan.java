package dev.beecube31.crazyae2.common.tile.trashcans;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigInventory;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.enums.TrashcanType;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkTileOC;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TileTrashcan extends CrazyAENetworkTileOC implements IUpgradeableObject, IGridTickable {
    public static final int CONFIG_SLOTS = 72;

    private final TrashcanType kind;
    private final ConfigInventory config;
    private final IUpgradeInventory upgrades;
    private final IActionSource source = new MachineSource(this);

    public TileTrashcan(BlockEntityType<?> type, BlockPos pos, BlockState state, TrashcanType kind) {
        super(type, pos, state);
        this.kind = kind;
        this.config = ConfigInventory.configTypes(kind::accepts, CONFIG_SLOTS, this::onConfigChanged);
        this.upgrades = UpgradeInventories.forMachine(state.getBlock(), 4, this::saveChanges);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(4)
                .addService(IGridTickable.class, this);
    }

    public TrashcanType getKind() {
        return kind;
    }

    public ConfigInventory getConfig() {
        return config;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    public long getOperationsPerTick() {
        long ops = CrazyAEConfig.COMMON.trashcanBaseOperations.get();
        int stacks = upgrades.getInstalledUpgrades(CrazyItems.STACKS_CARD.get());
        return ops << (2 * Math.min(stacks, 4));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 20, config.isEmpty(), true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!getMainNode().isActive() || config.isEmpty()) {
            return config.isEmpty() ? TickRateModulation.SLEEP : TickRateModulation.SLOWER;
        }

        var grid = getMainNode().getGrid();
        if (grid == null) {
            return TickRateModulation.SLOWER;
        }

        var storage = grid.getStorageService().getInventory();
        var energy = grid.getEnergyService();

        long budget = getOperationsPerTick() * Math.max(1, ticksSinceLastCall);
        boolean didWork = false;
        for (int i = 0; i < config.size() && budget > 0; i++) {
            var key = config.getKey(i);
            if (key == null || !kind.accepts(key)) {
                continue;
            }
            long amount = budget * key.getAmountPerOperation();
            long removed = StorageHelper.poweredExtraction(energy, storage, key, amount, source, Actionable.MODULATE);
            if (removed > 0) {
                didWork = true;
                budget -= Math.max(1, removed / key.getAmountPerOperation());

                this.addCompletedOperations((double) removed / key.getAmountPerOperation());
            }
        }
        return didWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    public void onConfigChanged() {
        saveChanges();
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        config.writeToChildTag(data, "config");
        upgrades.writeToNBT(data, "upgrades");
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        config.readFromChildTag(data, "config");
        upgrades.readFromNBT(data, "upgrades");
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var stack : upgrades) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }
}
