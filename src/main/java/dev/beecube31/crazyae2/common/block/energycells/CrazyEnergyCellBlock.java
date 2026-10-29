package dev.beecube31.crazyae2.common.block.energycells;

import appeng.block.networking.EnergyCellBlock;

import java.util.function.DoubleSupplier;

public class CrazyEnergyCellBlock extends EnergyCellBlock {
    private final DoubleSupplier maxPower;
    private final int chargeRateDivider;

    public CrazyEnergyCellBlock(DoubleSupplier maxPower, int chargeRateDivider, int priority) {
        super(0, 0, priority);
        this.maxPower = maxPower;
        this.chargeRateDivider = chargeRateDivider;
    }

    @Override
    public double getMaxPower() {
        return this.maxPower.getAsDouble();
    }

    @Override
    public double getChargeRate() {
        return getMaxPower() / this.chargeRateDivider;
    }
}
