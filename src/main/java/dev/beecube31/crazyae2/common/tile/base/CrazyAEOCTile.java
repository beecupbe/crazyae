package dev.beecube31.crazyae2.common.tile.base;

import appeng.tile.AEBaseTile;
import dev.beecube31.crazyae2.common.interfaces.IOperationsCounterTile;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CrazyAEOCTile extends AEBaseTile implements IOperationsCounterTile {

    protected double completedOperations;

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("completedOperations", this.completedOperations);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.completedOperations = data.getDouble("completedOperations");
    }


    @Override
    public double getCompletedOperations() {
        return this.completedOperations;
    }

    @Override
    public void addCompletedOperations() {
        this.completedOperations++;
    }

    @Override
    public void addCompletedOperations(double amt) {
        this.completedOperations += amt;
    }
}
