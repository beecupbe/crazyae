package dev.beecube31.crazyae2.common.tile.solars;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

public class TileEnergyPanelBasic extends TileEnergyPanelBase {
    public TileEnergyPanelBasic() {
        super();
    }

    protected double getPowerPerTick() {
        return CrazyAEConfig.basicSolarPanelGenPerTick;
    }

    protected double getPowerPerTickAtNight() {
        return CrazyAEConfig.basicSolarPanelGenPerTickNight;
    }

    @Override
    protected double getCapacity() {
        return CrazyAEConfig.basicSolarPanelCapacity;
    }
}
