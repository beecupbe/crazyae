package dev.beecube31.crazyae2.core.api.storage;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStackWatcher;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public interface IManaWatcherHost {
    void updateWatcher(IStackWatcher var1);

    void onStackChange(IItemList<?> var1, IAEStack<?> var2, IAEStack<?> var3, IActionSource var4, IManaStorageChannel var5);
}
