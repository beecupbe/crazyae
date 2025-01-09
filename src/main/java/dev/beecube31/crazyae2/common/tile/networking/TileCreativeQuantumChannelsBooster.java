package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.networking.GridFlags;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;

public class TileCreativeQuantumChannelsBooster extends TileQuantumChannelsBooster {
    public TileCreativeQuantumChannelsBooster() {
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setValidSides(EnumSet.noneOf(EnumFacing.class));
        this.getProxy().setIdlePowerUsage(1);
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
