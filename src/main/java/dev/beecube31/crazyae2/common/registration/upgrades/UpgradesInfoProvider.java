package dev.beecube31.crazyae2.common.registration.upgrades;

import appeng.api.definitions.IItemDefinition;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public  class UpgradesInfoProvider {
    private static final Map<IItemDefinition, UpgradeInfo> UPGRADES_MAP = new HashMap<>();

    public static void addUpgradeInfo(@NotNull IItemDefinition block, @NotNull UpgradeInfo inf) {
        Preconditions.checkNotNull(block, "Trying to set UpgradeInfo for null block");
        Preconditions.checkNotNull(inf, "Trying to set null UpgradeInfo for block");

        if (!UPGRADES_MAP.containsKey(block)) {
            UPGRADES_MAP.put(block, inf);
        }
    }

    public static UpgradeInfo getUpgradeInfo(@NotNull IItemDefinition block) {
        Preconditions.checkNotNull(block, "Trying to get UpgradeInfo for null block");
        return UPGRADES_MAP.getOrDefault(block, null);
    }

    public static Map<IItemDefinition, UpgradeInfo> getUpgradesMap() {
        return UPGRADES_MAP;
    }
}
