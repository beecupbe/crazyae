package dev.beecube31.crazyae2.core.cache.impl;

import appeng.api.networking.*;
import dev.beecube31.crazyae2.core.cache.INetworkStatusCache;
import org.jetbrains.annotations.NotNull;

public class NetworkStatusCache implements IGridCache, INetworkStatusCache {

    private final IGrid grid;

    private long meLoadedMillis;

    public NetworkStatusCache(final IGrid grid) {
        this.grid = grid;
    }

    @Override
    public long getMELoadedMillis() {
        return this.meLoadedMillis;
    }

    public void setMeLoadedMillis(long m) {
        this.meLoadedMillis = m;
    }

    @Override
    public long getUptime() {
        return System.currentTimeMillis() - this.meLoadedMillis;
    }

    @Override
    public void onUpdateTick() {

    }

    @Override
    public void removeNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
    }

    @Override
    public void addNode(@NotNull IGridNode iGridNode, @NotNull IGridHost iGridHost) {
    }

    @Override
    public void onSplit(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void onJoin(@NotNull IGridStorage iGridStorage) {}

    @Override
    public void populateGridStorage(@NotNull IGridStorage iGridStorage) {}
}
