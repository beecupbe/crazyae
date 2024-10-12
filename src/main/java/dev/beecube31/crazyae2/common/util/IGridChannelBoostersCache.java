package dev.beecube31.crazyae2.common.util;

import appeng.api.networking.IGridCache;

public interface IGridChannelBoostersCache extends IGridCache {
    default int getChannels() {
        return 0;
    }

    default int getActiveBoosters() {
        return 0;
    }
}
