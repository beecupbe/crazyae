package dev.beecube31.crazyae2.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record CrazyTargetPoint(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {

    public CrazyTargetPoint(double x, double y, double z, double r2, Level level) {
        this(null, x, y, z, r2, level);
    }

    public static CrazyTargetPoint at(double x, double y, double z, double r2, Level level) {
        return new CrazyTargetPoint(x, y, z, r2, level);
    }

    public static CrazyTargetPoint at(ServerPlayer excluded, double x, double y, double z, double r2, Level level) {
        return new CrazyTargetPoint(excluded, x, y, z, r2, level);
    }
}
