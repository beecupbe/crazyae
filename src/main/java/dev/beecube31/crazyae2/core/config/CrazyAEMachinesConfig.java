package dev.beecube31.crazyae2.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = "crazyae", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrazyAEMachinesConfig {
    public static final String CONFIG_FILENAME = "crazyae2-machines-config.toml";

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {

        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerMaxQueueSize;

        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWithoutUpgrades;
        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith1Upgrade;
        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith2Upgrades;
        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith3Upgrades;
        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith4Upgrades;
        public final ForgeConfigSpec.IntValue improvedMolecularAssemblerCraftsPerTickWith5Upgrades;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
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

    public static void register(net.minecraftforge.fml.ModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, COMMON_SPEC, "crazyae2-machines-config.toml");
    }
}