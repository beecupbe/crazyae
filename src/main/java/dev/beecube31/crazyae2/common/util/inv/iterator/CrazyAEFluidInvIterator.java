package dev.beecube31.crazyae2.common.util.inv.iterator;

import appeng.api.storage.data.IAEFluidStack;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalFluidInv;

import java.util.Iterator;

public final class CrazyAEFluidInvIterator implements Iterator<IAEFluidStack> {
    private final CrazyAEInternalFluidInv inventory;
    private final int size;

    private int counter = 0;

    public CrazyAEFluidInvIterator(final CrazyAEInternalFluidInv inventory) {
        this.inventory = inventory;
        this.size = this.inventory.getSlots();
    }

    @Override
    public boolean hasNext() {
        return this.counter < this.size;
    }

    @Override
    public IAEFluidStack next() {
        final IAEFluidStack result = this.inventory.getFluidInSlot(this.counter);

        this.counter++;

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
