package dev.beecube31.crazyae2.common.interfaces.gui;

public interface ICheckboxProvider {
    boolean getCheckboxCurrentState();

    default boolean getCheckboxCurrentState(int index) {
        return getCheckboxCurrentState();
    }
}
