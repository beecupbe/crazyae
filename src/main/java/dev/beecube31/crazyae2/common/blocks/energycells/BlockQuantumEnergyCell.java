package dev.beecube31.crazyae2.common.blocks.energycells;

import appeng.block.networking.BlockEnergyCell;
import dev.beecube31.crazyae2.common.interfaces.IDenseEnergyCell;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class BlockQuantumEnergyCell extends BlockEnergyCell implements IDenseEnergyCell {

    public BlockQuantumEnergyCell() {}

    @Override
    public double getMaxPower() {
        return CrazyAEConfig.quantumEnergyCellCap;
    }
}
