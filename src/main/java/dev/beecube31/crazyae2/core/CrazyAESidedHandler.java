package dev.beecube31.crazyae2.core;

import appeng.api.definitions.IItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class CrazyAESidedHandler {
    public static final List<IItemDefinition> availableEnergyTypes = new ArrayList<>();

    public static void checkAvailableEnergyTypes(List<IItemDefinition> candidates) {
        if (availableEnergyTypes.isEmpty()) {
            for (IItemDefinition candidate : candidates) {
                if (candidate.isEnabled()) {
                    availableEnergyTypes.add(candidate);
                }
            }
        }
    }
}
