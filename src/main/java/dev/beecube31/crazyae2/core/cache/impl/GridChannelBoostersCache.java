package dev.beecube31.crazyae2.core.cache.impl;

import appeng.api.networking.*;
import dev.beecube31.crazyae2.common.interfaces.IChannelsMultiplier;
import dev.beecube31.crazyae2.common.tile.networking.TileCreativeQuantumChannelsBooster;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GridChannelBoostersCache implements IGridCache, IGridChannelBoostersCache {

    private static final HashMap<IChannelsMultiplier, Integer> boosters = new HashMap<>();
    private int channels = 0;
    private int creativeBoostersAmt;

    private final IGrid grid;

    public GridChannelBoostersCache(final IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void onUpdateTick() {}

    @Override
    public void removeNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof final IChannelsMultiplier r && boosters.containsKey(iGridHost)) {
            if (r.isCreative()) {
                boosters.remove(r, Integer.MAX_VALUE);
                if (this.creativeBoostersAmt == 1) {
                    this.channels = 0;
                }
                this.creativeBoostersAmt--;
                return;
            }

            final int channelsAmt = CrazyAEConfig.QCMBoostAmt;
            boosters.remove(r, channelsAmt);
            this.channels -= channelsAmt;
        }
    }

    @Override
    public void addNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
        if (iGridHost instanceof final IChannelsMultiplier r && !boosters.containsKey(iGridHost)) {
            if (r.isCreative()) {
                this.channels = Integer.MAX_VALUE;
                this.creativeBoostersAmt++;
                boosters.put(r, Integer.MAX_VALUE);
                return;
            }

            if (this.channels < Integer.MAX_VALUE) {
                final int channelsAmt = CrazyAEConfig.QCMBoostAmt;
                boosters.put(r, channelsAmt);
                this.channels += channelsAmt;
            }
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
    public boolean isForcingCreativeMultiplier() {
        return this.creativeBoostersAmt > 0;
    }

    @Override
    public int getActiveBoosters() {
        return boosters.size();
    }
}
