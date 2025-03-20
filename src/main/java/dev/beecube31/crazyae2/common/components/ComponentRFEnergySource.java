package dev.beecube31.crazyae2.common.components;

import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyRFDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyExportBus;
import net.minecraft.util.EnumFacing;

public class ComponentRFEnergySource extends BaseEnergyRFDelegate implements IEnergyHandler, IEnergyProvider {

    private final PartEnergyExportBus part;

    public ComponentRFEnergySource(PartEnergyExportBus part) {
        super();
        this.part = part;
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

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return this.part.extractEnergy(maxExtract, simulate);
    }
}
