package dev.beecube31.crazyae2.common.blocks.energycells;

import appeng.block.networking.BlockEnergyCell;
import dev.beecube31.crazyae2.core.CrazyAEConfig;

public class BlockPerfectEnergyCell extends BlockEnergyCell {

    public BlockPerfectEnergyCell() {}

    @Override
    public double getMaxPower() {
        return CrazyAEConfig.perEnergyCellCap;
    }
}
