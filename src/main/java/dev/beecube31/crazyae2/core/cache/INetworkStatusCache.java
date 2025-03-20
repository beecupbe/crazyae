package dev.beecube31.crazyae2.core.cache;

import appeng.api.networking.IGridCache;

public interface INetworkStatusCache extends IGridCache {
    default long getMELoadedMillis() {
        return 0;
    }

    default long getUptime() {
        return 0;
    }
}
