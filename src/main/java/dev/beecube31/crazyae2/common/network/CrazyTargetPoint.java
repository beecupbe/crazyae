package dev.beecube31.crazyae2.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class CrazyTargetPoint {

    public final ServerPlayer excluded;
    public final double x;
    public final double y;
    public final double z;
    public final double r2;
    public final Level level;

    public CrazyTargetPoint(double x, double y, double z, double r2, Level level) {
        this(null, x, y, z, r2, level);
    }

    public CrazyTargetPoint(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {
        this.excluded = excluded;
        this.x = x;
        this.y = y;
        this.z = z;
        this.r2 = r2;
        this.level = level;
    }

    public static CrazyTargetPoint at(double x, double y, double z, double r2, Level level) {
        return new CrazyTargetPoint(x, y, z, r2, level);
    }

    public static CrazyTargetPoint at(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {
        return new CrazyTargetPoint(excluded, x, y, z, r2, level);
    }
}
