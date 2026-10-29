package dev.beecube31.crazyae2.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class CrazyAEMachinesConfig {
    public static final String CONFIG_FILENAME = "crazyae2-machines-config.toml";

    public static final CommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {

        public final ModConfigSpec.IntValue improvedMolecularAssemblerMaxQueueSize;

        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWithoutUpgrades;
        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith1Upgrade;
        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith2Upgrades;
        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith3Upgrades;
        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith4Upgrades;
        public final ModConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith5Upgrades;

        public CommonConfig(ModConfigSpec.Builder builder) {
            builder.comment("Config").push("config");

            improvedMolecularAssemblerMaxQueueSize = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 256, 1, Integer.MAX_VALUE);

            improvedMolecularAssemblerCraftsPerTickWithoutUpgrades = builder.defineInRange("improvedMolecularAssemblerCraftsPerTickWithoutUpgrades", 8, 1, Integer.MAX_VALUE);
            improvedMolecularAssemblerCraftsPerTickWith1Upgrade = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 16, 1, Integer.MAX_VALUE);
            improvedMolecularAssemblerCraftsPerTickWith2Upgrades = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 32, 1, Integer.MAX_VALUE);
            improvedMolecularAssemblerCraftsPerTickWith3Upgrades = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 64, 1, Integer.MAX_VALUE);
            improvedMolecularAssemblerCraftsPerTickWith4Upgrades = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 96, 1, Integer.MAX_VALUE);
            improvedMolecularAssemblerCraftsPerTickWith5Upgrades = builder.defineInRange("improvedMolecularAssemblerMaxQueueSize", 128, 1, Integer.MAX_VALUE);

            builder.pop();
        }
    }

}