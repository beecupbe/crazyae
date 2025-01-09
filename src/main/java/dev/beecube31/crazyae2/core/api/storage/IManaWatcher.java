package dev.beecube31.crazyae2.core.api.storage;

import appeng.api.storage.data.IAEStack;

/**
 * DO NOT IMPLEMENT.
 * <p>
 * Will be injected when adding an {@link IManaWatcherHost} to a grid.
 */
public interface IManaWatcher {
    boolean add( IAEStack<?> stack );

    boolean remove( IAEStack<?> stack );

    void reset();
}
