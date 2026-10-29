package dev.beecube31.crazyae2.common.util;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.FriendlyByteBuf;

public final class ThroughputTracker {
    public enum Unit {
        TICK("t", 1.0 / 20.0, 0.05), SECOND("s", 1.0, 1.0), MINUTE("m", 60.0, 1.0 / 60.0), HOUR("h", 3600.0, 1.0 / 3600.0);

        final String suffix;
        final double multiplier;
        final double alpha;

        Unit(String suffix, double multiplier, double alpha) {
            this.suffix = suffix;
            this.multiplier = multiplier;
            this.alpha = alpha;
        }
    }

    private long lastAmount = -1;
    private double emaRatePerSec;
    private double lastSyncedRate;
    private Unit unit = Unit.SECOND;

    public void reset() {
        lastAmount = -1;
        emaRatePerSec = 0;
    }

    public boolean sample(long amount, int ticksSinceLast) {
        if (lastAmount == -1) {
            lastAmount = amount;
            return false;
        }
        long delta = amount - lastAmount;
        lastAmount = amount;
        double seconds = ticksSinceLast / 20.0;
        double sample = delta / (seconds > 0 ? seconds : 1.0);
        emaRatePerSec = unit.alpha * sample + (1.0 - unit.alpha) * emaRatePerSec;
        if (Math.abs(emaRatePerSec - lastSyncedRate) > 0.5) {
            lastSyncedRate = emaRatePerSec;
            return true;
        }
        return false;
    }

    public Unit cycleUnit() {
        unit = Unit.values()[(unit.ordinal() + 1) % Unit.values().length];
        return unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void write(FriendlyByteBuf data) {
        data.writeDouble(emaRatePerSec);
        data.writeByte(unit.ordinal());
    }

    public boolean read(FriendlyByteBuf data) {
        double newRate = data.readDouble();
        Unit newUnit = Unit.values()[data.readByte() % Unit.values().length];
        boolean changed = newRate != emaRatePerSec || newUnit != unit;
        emaRatePerSec = newRate;
        unit = newUnit;
        return changed;
    }

    public void save(ValueOutput tag) {
        tag.putByte("throughputUnit", (byte) unit.ordinal());
    }

    public void load(ValueInput tag) {
        unit = Unit.values()[Math.floorMod(tag.getByteOr("throughputUnit", (byte) 0), Unit.values().length)];
    }

    private long roundedRate() {
        return Math.round(emaRatePerSec * unit.multiplier);
    }

    public String formatRate() {
        long r = roundedRate();
        String s;
        if (Math.abs(r) >= 1_000_000) {
            s = String.format(java.util.Locale.ROOT, "%.1fM", r / 1_000_000.0);
        } else if (Math.abs(r) >= 1000) {
            s = String.format(java.util.Locale.ROOT, "%.1fK", r / 1000.0);
        } else {
            s = String.valueOf(r);
        }
        if (r > 0) {
            s = "+" + s;
        }
        return s + "/" + unit.suffix;
    }

    public int rateColor() {
        long r = roundedRate();
        return r > 0 ? 0x22DD22 : r < 0 ? 0xDD2222 : 0x555555;
    }

    public static String formatAmount(AEKey what, long amount) {
        return what.formatAmount(amount, AmountFormat.SLOT);
    }
}
