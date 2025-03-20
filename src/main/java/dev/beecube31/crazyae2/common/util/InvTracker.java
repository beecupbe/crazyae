package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Upgrades;
import appeng.helpers.DualityInterface;
import appeng.tile.inventory.AppEngInternalInventory;
import dev.beecube31.crazyae2.common.duality.PatternsInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.IGridHostMonitorable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class InvTracker {
    public static long autoBase = Long.MIN_VALUE;
    public final long sortBy;
    public final long which = autoBase++;
    public final String unlocalizedName;
    public final IItemHandler client;
    public final IItemHandler server;
    public final BlockPos pos;
    public final int dim;
    public final int numUpgrades;
    public int forceSlots = 0;


    public InvTracker(final PatternsInterfaceDuality dual, final IItemHandler patterns, final String unlocalizedName) {
        this.server = patterns;
        this.client = new AppEngInternalInventory(null, this.server.getSlots());
        this.unlocalizedName = unlocalizedName;
        this.sortBy = dual.getSortValue();
        this.pos = dual.getLocation().getPos();
        this.dim = dual.getLocation().getWorld().provider.getDimension();
        this.numUpgrades = dual.getInstalledUpgrades(Upgrades.PATTERN_EXPANSION);
    }

    public InvTracker(final DualityInterface dual, final IItemHandler patterns, final String unlocalizedName) {
        this.server = patterns;
        this.client = new AppEngInternalInventory(null, this.server.getSlots());
        this.unlocalizedName = unlocalizedName;
        this.sortBy = dual.getSortValue();
        this.pos = dual.getLocation().getPos();
        this.dim = dual.getLocation().getWorld().provider.getDimension();
        this.numUpgrades = dual.getInstalledUpgrades(Upgrades.PATTERN_EXPANSION);
    }

    public InvTracker(final DualityInterface dual, int forceSlots, final IItemHandler patterns, final String unlocalizedName) {
        this.server = patterns;
        this.client = new AppEngInternalInventory(null, this.server.getSlots());
        this.unlocalizedName = unlocalizedName;
        this.sortBy = dual.getSortValue();
        this.pos = dual.getLocation().getPos();
        this.dim = dual.getLocation().getWorld().provider.getDimension();
        this.numUpgrades = dual.getInstalledUpgrades(Upgrades.PATTERN_EXPANSION);
        this.forceSlots = forceSlots;
    }

    public InvTracker(final IGridHostMonitorable mon, final boolean isMac, final IItemHandler patterns, final String unlocalizedName) {
        this.server = patterns;
        this.client = new AppEngInternalInventory(null, this.server.getSlots());
        this.unlocalizedName = unlocalizedName;
        this.sortBy = mon.getSortValue();
        this.pos = mon.getTEPos();
        this.dim = mon.getDim();
        this.numUpgrades = 0;
    }
}
