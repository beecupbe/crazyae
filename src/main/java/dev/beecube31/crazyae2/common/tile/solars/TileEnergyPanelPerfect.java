package dev.beecube31.crazyae2.common.tile.solars;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class TileEnergyPanelPerfect extends TileEnergyPanelBase {
    public TileEnergyPanelPerfect() {
        super();
    }

    protected double getPowerPerTick() {
        return CrazyAEConfig.perfectSolarPanelGenPerTick;
    }

    protected double getPowerPerTickAtNight() {
        return CrazyAEConfig.perfectSolarPanelGenPerTickNight;
    }

    @Override
    protected double getCapacity() {
        return CrazyAEConfig.perfectSolarPanelCapacity;
    }
}
