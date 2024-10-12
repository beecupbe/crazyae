package dev.beecube31.crazyae2.common.util;

public class CachedValues {
    private static CachedValues INSTANCE;

    private static int cachedChannelsBoost;


    public void setCachedChannelsBoost(int amt) {
        cachedChannelsBoost = amt;
    }

    public void addCachedChannelsBoost(int amt) {
        cachedChannelsBoost += amt;
    }

    public void removeCachedChannelsBoost(int amt) {
        cachedChannelsBoost -= amt;
    }

    public int getGetCachedChannelsBoost() {
        return cachedChannelsBoost;
    }

    public static CachedValues instance() {
        if (INSTANCE == null) {
            INSTANCE = new CachedValues();
        }

        return INSTANCE;
    }
}
