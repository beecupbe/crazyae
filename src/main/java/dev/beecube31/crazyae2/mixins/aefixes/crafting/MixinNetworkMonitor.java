package dev.beecube31.crazyae2.mixins.aefixes.crafting;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.me.cache.GridStorageCache;
import appeng.me.cache.NetworkMonitor;
import appeng.me.storage.ItemWatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

@Mixin(value = NetworkMonitor.class, remap = false)
public abstract class MixinNetworkMonitor<T extends IAEStack<T>> implements IMEMonitor<T> {

    @Shadow @Final @Nonnull private static HashMap<IActionSource, LinkedList<NetworkMonitor<?>>> src2MonitorsMap;

    @Shadow @Final private static Set<IActionSource> nestingSources;

    @Shadow @Final @Nonnull private IStorageChannel<T> myChannel;

    @Shadow @Final @Nonnull private IItemList<T> cachedList;

    @Shadow @Final @Nonnull private GridStorageCache myGridCache;

    @Shadow protected abstract void notifyListenersOfChange(Iterable<T> diff, IActionSource src);

    @Shadow private boolean sendEvent;


    @Shadow public abstract void incGridCurrentCount(long count);

    /**
     * @author Beecube31
     * @reason Autocrafting optimizations
     */
    @Overwrite
    protected void postChange(final boolean add, final Iterable<T> changes, final IActionSource src) {
        src2MonitorsMap.computeIfAbsent(src, k -> new LinkedList<>());

        LinkedList<NetworkMonitor<?>> monitors = src2MonitorsMap.computeIfAbsent(src, key -> new LinkedList<>());
        if (monitors.contains(this)) {
            nestingSources.add(src);
            return;
        }

        monitors.add((NetworkMonitor<?>) (Object) this);

        this.sendEvent = true;

        for (T change : changes) {
            if (!add) {
                change.setStackSize(-change.getStackSize());
            }

            incGridCurrentCount(change.getStackSize());
            this.cachedList.addStorage(change);

            final Collection<ItemWatcher> watchers = this.myGridCache.getInterestManager().get(change);
            if (watchers != null && !watchers.isEmpty()) {
                IAEStack<T> fullStack = this.cachedList.findPrecise(change);
                if (fullStack == null) {
                    fullStack = change.copy();
                    fullStack.setStackSize(0);
                }

                this.myGridCache.getInterestManager().enableTransactions();
                for (ItemWatcher watcher : watchers) {
                    watcher.getHost().onStackChange(this.cachedList, fullStack, change, src, this.myChannel);
                }
                this.myGridCache.getInterestManager().disableTransactions();
            }
        }

        this.notifyListenersOfChange(changes, src);

        if (monitors.getFirst().equals(this)) {
            for (NetworkMonitor<?> monitor : monitors) {
                monitor.setForceUpdate(true);
            }
            monitors.clear();
            nestingSources.remove(src);
        }

    }

}
