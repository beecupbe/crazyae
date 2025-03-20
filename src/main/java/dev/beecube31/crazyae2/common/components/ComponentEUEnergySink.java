package dev.beecube31.crazyae2.common.components;

import dev.beecube31.crazyae2.common.components.base.BaseEnergyEUDelegateSink;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyImportBus;
import dev.beecube31.crazyae2.core.CrazyAE;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.util.EnumFacing;

public class ComponentEUEnergySink extends BaseEnergyEUDelegateSink implements IEnergySink {
    private final PartEnergyImportBus part;

    public ComponentEUEnergySink(PartEnergyImportBus part) {
        super(part.getHost().getTile().getWorld(), part.getHost().getTile().getPos(), Double.MAX_VALUE, 14);
        this.part = part;
    }

    @Override
    public double getDemandedEnergy() {
        return this.part.getDemandedEnergy(CrazyAE.definitions().items().EUEnergyAsAeStack());
    }

    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        return this.part.receiveEnergy(amount, CrazyAE.definitions().items().EUEnergyAsAeStack());
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
        return enumFacing == this.part.getSide().getFacing();
    }
}
