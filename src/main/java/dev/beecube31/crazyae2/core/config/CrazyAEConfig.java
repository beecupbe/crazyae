package dev.beecube31.crazyae2.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = "crazyae", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrazyAEConfig {
    public static final String CONFIG_FILENAME = "crazyae2-common.toml";

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {

        public final ForgeConfigSpec.BooleanValue disableUpdateChecking;

        public final ForgeConfigSpec.DoubleValue basicSolarPanelGenPerTick;
        public final ForgeConfigSpec.DoubleValue basicSolarPanelGenPerTickNight;
        public final ForgeConfigSpec.DoubleValue basicSolarPanelCapacity;

        public final ForgeConfigSpec.DoubleValue improvedSolarPanelGenPerTick;
        public final ForgeConfigSpec.DoubleValue improvedSolarPanelGenPerTickNight;
        public final ForgeConfigSpec.DoubleValue improvedSolarPanelCapacity;

        public final ForgeConfigSpec.DoubleValue advancedSolarPanelGenPerTick;
        public final ForgeConfigSpec.DoubleValue advancedSolarPanelGenPerTickNight;
        public final ForgeConfigSpec.DoubleValue advancedSolarPanelCapacity;

        public final ForgeConfigSpec.DoubleValue perfectSolarPanelGenPerTick;
        public final ForgeConfigSpec.DoubleValue perfectSolarPanelGenPerTickNight;
        public final ForgeConfigSpec.DoubleValue perfectSolarPanelCapacity;
        public final ForgeConfigSpec.DoubleValue quantumSolarPanelGenPerTick;
        public final ForgeConfigSpec.DoubleValue quantumSolarPanelGenPerTickNight;
        public final ForgeConfigSpec.DoubleValue quantumSolarPanelCapacity;


        public final ForgeConfigSpec.DoubleValue improvedEnergyCellCapacity;
        public final ForgeConfigSpec.DoubleValue advancedEnergyCellCapacity;
        public final ForgeConfigSpec.DoubleValue perfectEnergyCellCapacity;
        public final ForgeConfigSpec.DoubleValue quantumEnergyCellCapacity;
        public final ForgeConfigSpec.IntValue trashcanBaseOperations;

        public final ForgeConfigSpec.IntValue maxItemCellTypes;
        public final ForgeConfigSpec.IntValue maxFluidCellTypes;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Main").push("main");

            disableUpdateChecking = builder.define("disableUpdateChecking", false);

            builder.pop();

            builder.comment("Cells config").push("cells");

            maxItemCellTypes = builder
                    .defineInRange("maxItemCellTypes", 63, 0, Integer.MAX_VALUE);

            maxFluidCellTypes = builder
                    .defineInRange("maxFluidCellTypes", 18, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.comment("Solar Panels config").push("solars");

            basicSolarPanelGenPerTick = builder
                    .defineInRange("basicSolarPanelGenPerTick", 128, 0.0, Double.MAX_VALUE);

            basicSolarPanelGenPerTickNight = builder
                    .defineInRange("basicSolarPanelGenPerTickNight", 64, 0.0, Double.MAX_VALUE);

            basicSolarPanelCapacity = builder
                    .defineInRange("basicSolarPanelCapacity", 100000, 0.0, Double.MAX_VALUE);


            improvedSolarPanelGenPerTick = builder
                    .defineInRange("improvedSolarPanelGenPerTick", 512, 0.0, Double.MAX_VALUE);

            improvedSolarPanelGenPerTickNight = builder
                    .defineInRange("improvedSolarPanelGenPerTickNight", 256, 0.0, Double.MAX_VALUE);

            improvedSolarPanelCapacity = builder
                    .defineInRange("improvedSolarPanelCapacity", 400000, 0.0, Double.MAX_VALUE);


            advancedSolarPanelGenPerTick = builder
                    .defineInRange("advancedSolarPanelGenPerTick", 6144, 0.0, Double.MAX_VALUE);

            advancedSolarPanelGenPerTickNight = builder
                    .defineInRange("advancedSolarPanelGenPerTickNight", 3072, 0.0, Double.MAX_VALUE);

            advancedSolarPanelCapacity = builder
                    .defineInRange("advancedSolarPanelCapacity", 16000000, 0.0, Double.MAX_VALUE);


            perfectSolarPanelGenPerTick = builder
                    .defineInRange("perfectSolarPanelGenPerTick", 32768, 0.0, Double.MAX_VALUE);

            perfectSolarPanelGenPerTickNight = builder
                    .defineInRange("perfectSolarPanelGenPerTickNight", 16384, 0.0, Double.MAX_VALUE);

            perfectSolarPanelCapacity = builder
                    .defineInRange("perfectSolarPanelCapacity", 64000000, 0.0, Double.MAX_VALUE);
            quantumSolarPanelGenPerTick = builder
                    .defineInRange("quantumSolarPanelGenPerTick", 524288, 0.0, Double.MAX_VALUE);
            quantumSolarPanelGenPerTickNight = builder
                    .defineInRange("quantumSolarPanelGenPerTickNight", 262144, 0.0, Double.MAX_VALUE);
            quantumSolarPanelCapacity = builder
                    .defineInRange("quantumSolarPanelCapacity", 256000000, 0.0, Double.MAX_VALUE);

            builder.pop();

            builder.comment("Energy Cells config").push("energycells");

            improvedEnergyCellCapacity = builder
                    .defineInRange("improvedEnergyCellCapacity", 262144 * 64, 0.0, Double.MAX_VALUE);

            advancedEnergyCellCapacity = builder
                    .defineInRange("advancedEnergyCellCapacity", 262144 * 512, 0.0, Double.MAX_VALUE);

            perfectEnergyCellCapacity = builder
                    .defineInRange("perfectEnergyCellCapacity", 262144 * 3072, 0.0, Double.MAX_VALUE);
            quantumEnergyCellCapacity = builder
                    .defineInRange("quantumEnergyCellCapacity", 262144D * 65536D, 0.0, Double.MAX_VALUE);

            builder.pop();

            builder.comment("ME Trashcans config").push("trashcans");
            trashcanBaseOperations = builder
                    .comment("Operations per tick without upgrades (1 op = 1 item or 1 bucket). Each stacks card multiplies it by 4.")
                    .defineInRange("trashcanBaseOperations", 32, 1, 1 << 20);
            builder.pop();
        }
    }

    public static void register(net.minecraftforge.fml.ModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, COMMON_SPEC, CONFIG_FILENAME);
    }
}