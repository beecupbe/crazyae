package dev.beecube31.crazyae2.common.util.inv.iterator;

import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalAEInv;

import java.util.Iterator;

public final class CrazyAEInvIterator implements Iterator<IAEItemStack> {
    private final CrazyAEInternalAEInv inventory;
    private final int size;

    private int counter = 0;

    public CrazyAEInvIterator(final CrazyAEInternalAEInv inventory) {
        this.inventory = inventory;
        this.size = this.inventory.getSlots();
    }

    @Override
    public boolean hasNext() {
        return this.counter < this.size;
    }

    @Override
    public IAEItemStack next() {
        final IAEItemStack result = this.inventory.getAEStackInSlot(this.counter);

        this.counter++;

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
