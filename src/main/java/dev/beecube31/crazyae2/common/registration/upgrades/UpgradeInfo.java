package dev.beecube31.crazyae2.common.registration.upgrades;

import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UpgradeInfo {
    private final List<UpgradeLevel> mapLevels = new ArrayList<>();
    private final Upgrades.UpgradeType type;
    private final LevelInfo defaultUpgradeTypeValue;

    public UpgradeInfo
    (
            LevelInfo[] levels,
            Upgrades.UpgradeType type,
            LevelInfo defaultUpgradeTypeValue
    ) {
        Preconditions.checkNotNull(levels);
        Preconditions.checkNotNull(type);

        this.type = type;
        this.defaultUpgradeTypeValue = defaultUpgradeTypeValue;

        for (int i = 0; i < levels.length; i++) {
            this.mapLevels.add(new UpgradeLevel(i, levels[i]));
        }
    }

    public List<UpgradeLevel> getLevelsInfo() {
        return this.mapLevels;
    }

    public Upgrades.UpgradeType getUpgradeType() {
        return this.type;
    }

    public static class UpgradeLevel {
        private final int level;
        private final UpgradeInfo.LevelInfo levelInfo;

        public UpgradeLevel(int forLevel, UpgradeInfo.LevelInfo lvl) {
            this.level = forLevel;
            this.levelInfo = lvl;
        }

        public int getLevel() {
            return this.level;
        }

        public LevelInfo getLevelInfo() {
            return this.levelInfo;
        }
    }

    public static class LevelInfo {
        private final UpgradeType[] forTypes;
        private final double[] info;

        public LevelInfo(UpgradeType[] types, double[] levelInfos) {
            this.forTypes = types;
            this.info = levelInfos;
        }

        public UpgradeType[] getLevelTypes() {
            return this.forTypes;
        }

        public double[] getLevelBoostingInfo() {
            return this.info;
        }
    }

    public enum UpgradeType {
        WORK_SPEED,
        MORE_ITEMS_PER_TICK,
        MORE_FLUIDS_PER_TICK,
        MORE_MANA_PER_TICK,
        TASKS_STORAGE_CAPACITY;
    }
}
