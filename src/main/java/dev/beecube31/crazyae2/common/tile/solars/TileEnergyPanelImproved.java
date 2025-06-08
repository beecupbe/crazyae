package dev.beecube31.crazyae2.common.tile.solars;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class TileEnergyPanelImproved extends TileEnergyPanelBase {
    public TileEnergyPanelImproved() {
        super();
    }

    protected double getPowerPerTick() {
        return CrazyAEConfig.improvedSolarPanelGenPerTick;
    }

    protected double getPowerPerTickAtNight() {
        return CrazyAEConfig.improvedSolarPanelGenPerTickNight;
    }

    @Override
    protected double getCapacity() {
        return CrazyAEConfig.improvedSolarPanelCapacity;
    }
}
