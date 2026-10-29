package dev.beecube31.crazyae2.common.enums;

import dev.beecube31.crazyae2.core.config.CrazyAEConfig;

import java.util.function.Supplier;

import net.neoforged.neoforge.common.ModConfigSpec;

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

    private final Supplier<ModConfigSpec.DoubleValue> day;
    private final Supplier<ModConfigSpec.DoubleValue> night;
    private final Supplier<ModConfigSpec.DoubleValue> capacity;

    SolarPanelTier(Supplier<ModConfigSpec.DoubleValue> day, Supplier<ModConfigSpec.DoubleValue> night,
                   Supplier<ModConfigSpec.DoubleValue> capacity) {
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
