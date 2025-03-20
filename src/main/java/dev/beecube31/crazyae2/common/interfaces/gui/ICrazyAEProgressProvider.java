package dev.beecube31.crazyae2.common.interfaces.gui;

public interface ICrazyAEProgressProvider {
    double getCurrentProgress();

    default double getCurrentProgress(int index) {
        return getCurrentProgress();
    }

    double getMaxProgress();

    default double getMaxProgress(int index) {
        return getMaxProgress();
    }

    String getTooltip(String title, boolean disableMaxProgress, int tooltipID);
}
