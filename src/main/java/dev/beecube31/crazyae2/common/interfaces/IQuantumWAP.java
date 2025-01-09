package dev.beecube31.crazyae2.common.interfaces;

import appeng.api.implementations.tiles.IWirelessAccessPoint;

public interface IQuantumWAP extends IWirelessAccessPoint {
    default boolean canOpenInAnyDimensions() {
        return false;
    }
}
