package dev.beecube31.crazyae2.common.enums;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public enum SolarPanelTier {
    BASIC(() -> CrazyAEConfig.COMMON.basicSolarPanelGenPerTick, () -> CrazyAEConfig.COMMON.basicSolarPanelGenPerTickNight,
            () -> CrazyAEConfig.COMMON.basicSolarPanelCapacity),
    IMPROVED(() -> CrazyAEConfig.COMMON.improvedSolarPanelGenPerTick, () -> CrazyAEConfig.COMMON.improvedSolarPanelGenPerTickNight,
            () -> CrazyAEConfig.COMMON.improvedSolarPanelCapacity),
    ADVANCED(() -> CrazyAEConfig.COMMON.advancedSolarPanelGenPerTick, () -> CrazyAEConfig.COMMON.advancedSolarPanelGenPerTickNight,
            () -> CrazyAEConfig.COMMON.advancedSolarPanelCapacity),
    PERFECT(() -> CrazyAEConfig.COMMON.perfectSolarPanelGenPerTick, () -> CrazyAEConfig.COMMON.perfectSolarPanelGenPerTickNight,
            () -> CrazyAEConfig.COMMON.perfectSolarPanelCapacity),
    QUANTUM(() -> CrazyAEConfig.COMMON.quantumSolarPanelGenPerTick, () -> CrazyAEConfig.COMMON.quantumSolarPanelGenPerTickNight,
            () -> CrazyAEConfig.COMMON.quantumSolarPanelCapacity);

    private final Supplier<ForgeConfigSpec.DoubleValue> day;
    private final Supplier<ForgeConfigSpec.DoubleValue> night;
    private final Supplier<ForgeConfigSpec.DoubleValue> capacity;

    SolarPanelTier(Supplier<ForgeConfigSpec.DoubleValue> day, Supplier<ForgeConfigSpec.DoubleValue> night,
                   Supplier<ForgeConfigSpec.DoubleValue> capacity) {
        this.day = day;
        this.night = night;
        this.capacity = capacity;
    }

    public double getGenPerTickDay() {
        return this.day.get().get();
    }

    public double getGenPerTickNight() {
        return this.night.get().get();
    }

    public double getCapacity() {
        return this.capacity.get().get();
    }
}
