package dev.beecube31.crazyae2.common.enums;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;

import java.util.function.Predicate;

public enum TrashcanType {
    ITEMS(k -> k instanceof AEItemKey),
    FLUIDS(k -> k instanceof AEFluidKey);

    private final Predicate<AEKey> filter;

    TrashcanType(Predicate<AEKey> filter) {
        this.filter = filter;
    }

    public boolean accepts(AEKey key) {
        return filter.test(key);
    }
}
