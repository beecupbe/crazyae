package dev.beecube31.crazyae2.common.blocks.energycells;

import appeng.block.networking.BlockEnergyCell;
import dev.beecube31.crazyae2.common.interfaces.IDenseEnergyCell;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class BlockPerfectEnergyCell extends BlockEnergyCell implements IDenseEnergyCell {

    public BlockPerfectEnergyCell() {}

    @Override
    public double getMaxPower() {
        return CrazyAEConfig.perEnergyCellCap;
    }
}
