package dev.beecube31.crazyae2.common.registration.upgrades;

import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.util.FeatureSet;

@SuppressWarnings("unused")
public class UpgradeInfo {
    private final FeatureSet providedSet;

    public UpgradeInfo
    (
            FeatureSet set
    ) {
        Preconditions.checkNotNull(set);
        this.providedSet = set;
    }

    public FeatureSet getProvidedSet() {
        return providedSet;
    }
}
