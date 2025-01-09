package dev.beecube31.crazyae2.common.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public interface IGridHostMonitorable {
    long getSortValue();

    BlockPos getTEPos();

    int getDim();

    IItemHandler getPatternsInv();

    String getName();
}
