package dev.beecube31.crazyae2.common.interfaces;

public interface IOperationsCounterTile {
    double getCompletedOperations();

    void addCompletedOperations();

    void addCompletedOperations(double amt);
}
