package dev.beecube31.crazyae2.craftsystem;

import appeng.api.networking.crafting.ICraftingWatcher;
import appeng.api.networking.crafting.ICraftingWatcherHost;
import appeng.api.storage.data.IAEStack;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CrazyCraftingWatcher implements ICraftingWatcher {

    private final CrazyAutocraftingSystem sys;
    private final ICraftingWatcherHost host;
    private final Set<IAEStack> myInterests = new HashSet<>();

    public CrazyCraftingWatcher(final CrazyAutocraftingSystem cache, final ICraftingWatcherHost host) {
        this.sys = cache;
        this.host = host;
    }

    public ICraftingWatcherHost getHost() {
        return this.host;
    }

    @Override
    public boolean add(final IAEStack e) {
        if (this.myInterests.contains(e)) {
            return false;
        }

        return this.myInterests.add(e.copy()) && this.sys.getInterestManager().put(e, this);
    }

    @Override
    public boolean remove(final IAEStack o) {
        return this.myInterests.remove(o) && this.sys.getInterestManager().remove(o, this);
    }

    @Override
    public void reset() {
        final Iterator<IAEStack> i = this.myInterests.iterator();

        while (i.hasNext()) {
            this.sys.getInterestManager().remove(i.next(), this);
            i.remove();
        }
    }
}
