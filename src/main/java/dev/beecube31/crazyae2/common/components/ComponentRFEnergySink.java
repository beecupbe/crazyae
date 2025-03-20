package dev.beecube31.crazyae2.common.components;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyReceiver;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyRFDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyImportBus;
import net.minecraft.util.EnumFacing;

public class ComponentRFEnergySink extends BaseEnergyRFDelegate implements IEnergyHandler, IEnergyReceiver {

    private final PartEnergyImportBus part;

    public ComponentRFEnergySink(PartEnergyImportBus part) {
        super();
        this.part = part;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return this.part.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from == this.part.getSide().getFacing();
    }
}
