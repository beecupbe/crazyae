package dev.beecube31.crazyae2.common.util;

import appeng.api.networking.*;
import dev.beecube31.crazyae2.common.tile.networking.TileQuantumChannelsBooster;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GridChannelBoostersCache implements IGridCache, IGridChannelBoostersCache {

    private static final HashMap<TileQuantumChannelsBooster, Integer> boosters = new HashMap<>();
    private int channels = 0;

    public GridChannelBoostersCache(final IGrid grid) {}

    @Override
    public void onUpdateTick() {}

    @Override
    public void removeNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof final TileQuantumChannelsBooster booster && boosters.containsKey(iGridHost)) {
            int amt = CrazyAEConfig.QCMBoostAmt;
            boosters.remove(booster);
            this.channels -= amt;
            CachedValues.instance().removeCachedChannelsBoost(amt);
        }
    }

    @Override
    public void addNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof final TileQuantumChannelsBooster booster && !boosters.containsKey(iGridHost)) {
            int amt = CrazyAEConfig.QCMBoostAmt;
            boosters.put(booster, amt);
            this.channels += amt;
            CachedValues.instance().addCachedChannelsBoost(amt);
        }
    }

    @Override
    public void onSplit(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void onJoin(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void populateGridStorage(@NotNull IGridStorage iGridStorage) {}

    @Override
    public int getChannels() {
        return this.channels;
    }

    @Override
    public int getActiveBoosters() {
        return boosters.size();
    }
}
