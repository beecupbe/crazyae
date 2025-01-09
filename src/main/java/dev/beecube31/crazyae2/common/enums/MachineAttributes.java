package dev.beecube31.crazyae2.common.enums;

public class MachineAttributes {
    private double requiredManaPerJob = Double.MIN_VALUE;
    private double requiredManaPerTick = Double.MIN_VALUE;
    private double requiredAEPerTick = Double.MIN_VALUE;
    private double requiredAEPerJob = Double.MIN_VALUE;

    public MachineAttributes setRequiredManaPerJob(double requiredManaPerJob) {
        this.requiredManaPerJob = requiredManaPerJob;
        return this;
    }

    public MachineAttributes setRequiredManaPerTick(double requiredManaPerTick) {
        this.requiredManaPerTick = requiredManaPerTick;
        return this;
    }

    public MachineAttributes setRequiredAEPerTick(double requiredAEPerTick) {
        this.requiredAEPerTick = requiredAEPerTick;
        return this;
    }

    public MachineAttributes setRequiredAEPerJob(double requiredAEPerJob) {
        this.requiredAEPerJob = requiredAEPerJob;
        return this;
    }

    public double getRequiredManaPerJob() {
        return requiredManaPerJob;
    }

    public double getRequiredManaPerTick() {
        return requiredManaPerTick;
    }

    public double getRequiredAEPerTick() {
        return requiredAEPerTick;
    }

    public double getRequiredAEPerJob() {
        return requiredAEPerJob;
    }
}
