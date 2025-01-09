package dev.beecube31.crazyae2.common.interfaces.gui;

public interface ICrazyAEProgressProvider {
    int getCurrentProgress();

    default int getCurrentProgress(int index) {
        return getCurrentProgress();
    }

    int getMaxProgress();

    default int getMaxProgress(int index) {
        return getMaxProgress();
    }
}
