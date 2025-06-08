package dev.beecube31.crazyae2.core.cache;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridCache;
import appeng.api.networking.crafting.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import com.google.common.collect.ImmutableCollection;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyInterfaceHost;
import net.minecraft.world.World;

import java.util.Set;
import java.util.concurrent.Future;

public interface ICrazyAutocraftingSystem extends IGridCache {
    ImmutableCollection<ICraftingPatternDetails> getCraftingFor(IAEItemStack var1, ICraftingPatternDetails var2, int var3, World var4);

    boolean canEmitFor(IAEItemStack var1);

    ICraftingLink submitCraftingJob(ICraftingJob job, ICraftingRequester requester, final ICrazyCraftHost host, IActionSource src);

    Future<ICraftingJob> beginCraftingJob(World world, IGrid grid, IActionSource source, IAEItemStack ais, ICraftingCallback callback);

    Set<ICrazyCraftHost> getFreeWorkers();

    Set<ICrazyCraftHost> getBusyWorkers();

    Set<ICrazyCraftHost> getWorkers();

    Set<ICrazyInterfaceHost> findInterfaceByDetails(ICraftingPatternDetails details);

    boolean containsCraftingItem(IAEItemStack req);
}
