package dev.beecube31.crazyae2.common.interfaces;

import dev.beecube31.crazyae2.common.enums.EnergyBusType;
import net.minecraft.item.ItemStack;

public interface IEnergyBus {
    long getMaxConfigEnergy();

    void setMaxConfigEnergy(long amt);

    ItemStack getItemStackRepresentation();

    EnergyBusType getBusType();
}
