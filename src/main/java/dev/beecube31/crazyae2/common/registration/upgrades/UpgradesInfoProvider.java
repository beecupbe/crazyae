package dev.beecube31.crazyae2.common.registration.upgrades;

import appeng.api.definitions.IItemDefinition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UpgradesInfoProvider {
    private static final Multimap<IItemDefinition, Upgrades.UpgradesFeatureSetParser.FeatureEntry> UPGRADES_MAP = ArrayListMultimap.create();

    public static void addUpgradeInfo(@NotNull IItemDefinition block, @NotNull Upgrades.UpgradesFeatureSetParser.FeatureEntry inf) {
        Preconditions.checkNotNull(block, "Trying to set UpgradeInfo for null block");
        Preconditions.checkNotNull(inf, "Trying to set null UpgradeInfo for block");

        if (!UPGRADES_MAP.containsEntry(block, inf)) {
            UPGRADES_MAP.put(block, inf);
        }
    }

    public static List<Upgrades.UpgradesFeatureSetParser.FeatureEntry> getUpgradeInfo(@NotNull IItemDefinition block) {
        Preconditions.checkNotNull(block, "Trying to get UpgradeInfo for null block");
        List<Upgrades.UpgradesFeatureSetParser.FeatureEntry> ret = new ArrayList<>();

        UPGRADES_MAP.forEach((iItemDefinition, featureEntry) -> {
            if (block.equals(iItemDefinition)) {
                ret.add(featureEntry);
            }
        });

        return ret;
    }

    public static Multimap<IItemDefinition, Upgrades.UpgradesFeatureSetParser.FeatureEntry> getUpgradesMap() {
        return UPGRADES_MAP;
    }
}
