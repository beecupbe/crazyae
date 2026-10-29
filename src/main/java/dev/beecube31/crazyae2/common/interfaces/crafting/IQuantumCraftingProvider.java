package dev.beecube31.crazyae2.common.interfaces.crafting;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;

public interface IQuantumCraftingProvider extends ICraftingProvider {
    /**
     * Accepts a batch whose inputs have already been extracted by the CPU. The counters contain the total outputs and
     * remaining container items, not per-craft amounts. Returning false must not accept any part of the batch.
     *
     * Outputs must only be queued here: the CPU registers them in waitingFor after this call returns. Inserting them into
     * the grid synchronously here would bypass the job's output accounting. Do not mutate the supplied counters.
     */
    boolean pushBatch(IPatternDetails details, long crafts, KeyCounter outputs, KeyCounter containerItems);

    /**
     * Tries to insert queued outputs into the grid, retaining any unaccepted remainder. Called after the CPU has updated
     * all task counts and expected outputs; providers must also retry pending outputs on later ticks.
     */
    void flushOutputs();
}
