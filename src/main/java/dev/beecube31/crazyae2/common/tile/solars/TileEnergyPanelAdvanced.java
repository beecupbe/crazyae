package dev.beecube31.crazyae2.common.tile.solars;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class TileEnergyPanelAdvanced extends TileEnergyPanelBase {
    public TileEnergyPanelAdvanced() {
        super();
    }

    protected double getPowerPerTick() {
        return CrazyAEConfig.advancedSolarPanelGenPerTick;
    }

    protected double getPowerPerTickAtNight() {
        return CrazyAEConfig.advancedSolarPanelGenPerTickNight;
    }

    @Override
    protected double getCapacity() {
        return CrazyAEConfig.advancedSolarPanelCapacity;
    }
}
