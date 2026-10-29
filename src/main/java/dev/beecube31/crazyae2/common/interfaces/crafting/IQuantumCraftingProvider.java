package dev.beecube31.crazyae2.common.interfaces.crafting;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;

/**
 * A crafting provider that can perform any number of crafts of a pattern instantly.
 * Used by {@link dev.beecube31.crazyae2.common.crafting.QuantumBatchExecutor} to push whole batches at once.
 */
public interface IQuantumCraftingProvider extends ICraftingProvider {
    /**
     * Accepts {@code crafts} crafts of the given pattern. The inputs have already been consumed by the CPU.
     *
     * @return true if the batch was accepted; the outputs will be inserted into the network later
     */
    boolean pushBatch(IPatternDetails details, long crafts, KeyCounter expectedOutputs, KeyCounter expectedContainerItems);

    /**
     * Inserts accumulated outputs into the network storage.
     */
    void flushOutputs();
}
