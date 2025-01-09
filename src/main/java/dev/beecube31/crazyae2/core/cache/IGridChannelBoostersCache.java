package dev.beecube31.crazyae2.core.cache;

import appeng.api.networking.IGridCache;

public interface IGridChannelBoostersCache extends IGridCache {
    default int getChannels() {
        return 0;
    }

    default int getActiveBoosters() {
        return 0;
    }

    default boolean isForcingCreativeMultiplier() {
        return false;
    }
}
