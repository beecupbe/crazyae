package dev.beecube31.crazyae2.common.containers;

import appeng.fluids.util.IAEFluidTank;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.CrazyAEPartSharedFluidBus;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerImprovedFluidBuses extends ContainerCrazyAEFluidConfigurable {
    private final CrazyAEPartSharedFluidBus bus;

    public ContainerImprovedFluidBuses(InventoryPlayer ip, CrazyAEPartSharedFluidBus te) {
        super(ip, te);
        this.bus = te;
    }

    @Override
    public IAEFluidTank getFluidConfigInventory() {
        return this.bus.getConfig();
    }

    @Override
    protected void setupConfig() {
        this.setupUpgrades();
    }
}
