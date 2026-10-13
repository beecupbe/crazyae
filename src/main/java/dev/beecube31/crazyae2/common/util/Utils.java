package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;

public class Utils {
    public static boolean canExtractEnergy(IEnergyService cache, double amt, Actionable mode, PowerMultiplier multiplier) {
        return cache.extractAEPower(amt, mode, multiplier) > 0;
    }
}
