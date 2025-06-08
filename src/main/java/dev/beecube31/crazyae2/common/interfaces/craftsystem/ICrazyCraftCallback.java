package dev.beecube31.crazyae2.common.interfaces.craftsystem;

import appeng.api.networking.crafting.ICraftingPatternDetails;

public interface ICrazyCraftCallback {
    void onCraftSentCallback(ICraftingPatternDetails details, long batchSize);

    void onCraftBatchCompletedCallback(ICraftingPatternDetails details, long batchSize);
}
