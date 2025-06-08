package dev.beecube31.crazyae2.common.blocks.energycells;

import appeng.block.networking.BlockEnergyCell;
import dev.beecube31.crazyae2.common.interfaces.IDenseEnergyCell;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class BlockImprovedEnergyCell extends BlockEnergyCell implements IDenseEnergyCell {

    public BlockImprovedEnergyCell() {}

    @Override
    public double getMaxPower() {
        return CrazyAEConfig.impEnergyCellCap;
    }
}
