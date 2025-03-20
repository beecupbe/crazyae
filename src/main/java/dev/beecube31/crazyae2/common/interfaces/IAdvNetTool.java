package dev.beecube31.crazyae2.common.interfaces;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.networking.IGridHost;
import appeng.tile.grid.AENetworkPowerTile;
import net.minecraftforge.items.IItemHandler;

public interface IAdvNetTool extends IGuiItemObject {
    IGridHost getGridHost();

    IItemHandler getInventory();

    AENetworkPowerTile getTargetMETile();
}
