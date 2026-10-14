package dev.beecube31.crazyae2.common.interfaces.crafting;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;

public interface IQuantumCraftingProvider extends ICraftingProvider {
    boolean pushBatch(IPatternDetails details, long crafts, KeyCounter expectedOutputs, KeyCounter expectedContainerItems);

    void flushOutputs();
}
