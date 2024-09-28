package dev.beecube31.crazyae2.common.blocks.energycells;

import appeng.block.networking.BlockEnergyCell;
import dev.beecube31.crazyae2.core.CrazyAEConfig;

public class BlockImprovedEnergyCell extends BlockEnergyCell {

    public BlockImprovedEnergyCell() {}

    @Override
    public double getMaxPower() {
        return CrazyAEConfig.impEnergyCellCap;
    }
}
