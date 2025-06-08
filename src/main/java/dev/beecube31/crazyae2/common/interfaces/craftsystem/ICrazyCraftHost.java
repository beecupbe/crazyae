package dev.beecube31.crazyae2.common.interfaces.craftsystem;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingLink;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

public interface ICrazyCraftHost extends ICraftingCPU {
    long getAcceleratorCount();

    double getStorageCount();

    TileEntity getTile();

    boolean isBusy();

    boolean pushDetails(ICraftingPatternDetails details, long crafts);

    ICraftingLink pushJob(ICraftingJob job, ICraftingRequester requester, IActionSource src);

    void cancel(IActionSource src);

    void tickCraftHost(IGrid grid, CrazyAutocraftingSystem cache);

    CrazyCraftingLink getLastLink();

    IGridNode getNode();

    void jobStateChange(ICraftingLink link);

    IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack aeStack, Actionable mode);

    IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource);

    boolean canAccept(IAEItemStack ais);

    IItemHandler getAcceleratorsInv();

    IItemHandler getStoragesInv();

    void setCpuName(String name);

    void getListOfItem(final IItemList<IAEItemStack> list, final CraftingItemList whichList);

    long getElapsedTime();

    IAEItemStack getItemStack(final IAEItemStack what, final CraftingItemList storage2);
}
