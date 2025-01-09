package dev.beecube31.crazyae2.core.storage;

import appeng.api.storage.data.IAEStack;
import dev.beecube31.crazyae2.core.api.storage.IManaWatcher;

import java.util.HashSet;
import java.util.Set;

public class ManaWatcher implements IManaWatcher {

    private final Set<IAEStack> myInterests = new HashSet<>();

    @Override
    public boolean add(IAEStack<?> stack) {
        return false;
    }

    @Override
    public boolean remove(IAEStack<?> stack) {
        return false;
    }

    @Override
    public void reset() {

    }
}
