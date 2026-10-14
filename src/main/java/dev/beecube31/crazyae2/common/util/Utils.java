package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;

import java.util.Locale;

public class Utils {
    public static boolean canExtractEnergy(IEnergyService cache, double amt, Actionable mode, PowerMultiplier multiplier) {
        return cache.extractAEPower(amt, mode, multiplier) > 0;
    }

    public static String formatCount(long value) {
        String[] units = {"", " K", " M", " G", " T", " P", " E"};
        double v = value;
        int i = 0;
        while (Math.abs(v) >= 1000 && i < units.length - 1) {
            v /= 1000;
            i++;
        }
        return trim(v, i == 0) + units[i];
    }

    public static String formatBytes(long bytes) {
        String[] units = {" B", " KB", " MB", " GB", " TB", " PB", " EB"};
        double v = bytes;
        int i = 0;
        while (v >= 1024 && i < units.length - 1) {
            v /= 1024;
            i++;
        }
        return trim(v, i == 0) + units[i];
    }

    private static String trim(double v, boolean integer) {
        if (integer) {
            return String.valueOf((long) v);
        }

        var s = String.format(Locale.ROOT, "%.2f", Math.floor(v * 100) / 100);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }
}
