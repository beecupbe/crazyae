package dev.beecube31.crazyae2.common.interfaces.craftsystem;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ICrazyInterfaceHost extends ICrazyCraftingMethod {
    TileEntity getTile();

    boolean isBusy();

    boolean pushDetails(ICraftingPatternDetails details, long batchSize, ICrazyCraftHost who);

    void cancelCraftingForPattern(ICraftingPatternDetails details, ICrazyCraftHost requestingCpu);

    void tickInterfaceHost(IGrid grid, CrazyAutocraftingSystem cache);

    boolean canAcceptPattern(ICraftingPatternDetails details);

    IGridNode getNode();

    IAEItemStack injectCraftedItems(ICraftingLink link, IAEItemStack aeStack, Actionable mode);

    long estimatePushableBatchSize(ICraftingPatternDetails details, long desiredBatchSize, ICrazyCraftHost requestingCpu, World world);
}
