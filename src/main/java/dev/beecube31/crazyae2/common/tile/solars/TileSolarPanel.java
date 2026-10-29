package dev.beecube31.crazyae2.common.tile.solars;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.GridPowerStorageStateChanged;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import dev.beecube31.crazyae2.common.block.solars.SolarPanelBlock;
import dev.beecube31.crazyae2.common.enums.SolarPanelTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileSolarPanel extends AENetworkedBlockEntity implements IAEPowerStorage, IGridTickable {
    private static final int TICK_RATE = 20;
    private static final String NBT_STORED = "storedEnergy";

    private final SolarPanelTier tier;
    private double stored;
    private boolean wasEmpty = true;

    public TileSolarPanel(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.tier = state.getBlock() instanceof SolarPanelBlock b ? b.getTier() : SolarPanelTier.BASIC;
        this.getMainNode()
                .setVisualRepresentation(state.getBlock())
                .setFlags()
                .setIdlePowerUsage(0)
                .addService(IAEPowerStorage.class, this)
                .addService(IGridTickable.class, this);
    }

    public SolarPanelTier getTier() {
        return tier;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TICK_RATE, TICK_RATE, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var level = getLevel();
        if (level == null || !level.canSeeSky(getBlockPos().above())) {
            return TickRateModulation.SAME;
        }

        double perTick = level.isBrightOutside() ? tier.getGenPerTickDay() : tier.getGenPerTickNight();
        double before = this.stored;
        this.stored = Math.min(getAEMaxPower(), this.stored + perTick * ticksSinceLastCall);

        if (this.stored != before) {
            setChanged();
            if (this.wasEmpty && this.stored > 0) {
                this.wasEmpty = false;
                node.getGrid().postEvent(new GridPowerStorageStateChanged(this,
                        GridPowerStorageStateChanged.PowerEventType.PROVIDE_POWER));
            }
        }
        return TickRateModulation.SAME;
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        data.putDouble(NBT_STORED, this.stored);
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        this.stored = data.getDoubleOr(NBT_STORED, 0);
        this.wasEmpty = this.stored <= 0;
    }

    @Override
    public double injectAEPower(double amt, Actionable mode) {
        return amt;
    }

    @Override
    public double getAEMaxPower() {
        return tier.getCapacity();
    }

    @Override
    public double getAECurrentPower() {
        return this.stored;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
        double needed = usePowerMultiplier.multiply(amt);
        double extracted = Math.min(needed, this.stored);
        if (mode == Actionable.MODULATE && extracted > 0) {
            this.stored -= extracted;
            if (this.stored <= 0) {
                this.stored = 0;
                this.wasEmpty = true;
            }
            setChanged();
        }
        return usePowerMultiplier.divide(extracted);
    }
}
