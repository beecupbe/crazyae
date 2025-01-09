package dev.beecube31.crazyae2.common.interfaces.jei;

import mezz.jei.api.gui.IGhostIngredientHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IJEIGhostIngredients {
    List<IGhostIngredientHandler.Target<?>> getPhantomTargets(Object var1);

    default Map<IGhostIngredientHandler.Target<?>, Object> getFakeSlotTargetMap() {
        return new HashMap<>();
    }
}
