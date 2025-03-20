package dev.beecube31.crazyae2.common.components;

import dev.beecube31.crazyae2.common.components.base.BaseEnergyEUDelegateSource;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyExportBus;
import dev.beecube31.crazyae2.core.CrazyAE;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.util.EnumFacing;

public class ComponentEUEnergySource extends BaseEnergyEUDelegateSource implements IEnergySource {
    private final PartEnergyExportBus part;

    public ComponentEUEnergySource(PartEnergyExportBus part) {
        super(part.getHost().getTile().getWorld(), part.getHost().getTile().getPos(), Double.MAX_VALUE, 14);
        this.part = part;
    }

    @Override
    public double getOfferedEnergy() {
        return this.part.availableEnergy(CrazyAE.definitions().items().EUEnergyAsAeStack());
    }

    @Override
    public void drawEnergy(double amount) {
        this.part.extractEnergy(amount, CrazyAE.definitions().items().EUEnergyAsAeStack());
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return direction == this.part.getSide().getFacing();
    }

    public int getSourceTier() {
        return Integer.MAX_VALUE;
    }
}
