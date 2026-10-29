package dev.beecube31.crazyae2.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


public class CrazyAEConfig {
    public static final String CONFIG_FILENAME = "crazyae2-common.toml";

    public static final CommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {

        public final ModConfigSpec.BooleanValue disableUpdateChecking;

        public final ModConfigSpec.DoubleValue basicSolarPanelGenPerTick;
        public final ModConfigSpec.DoubleValue basicSolarPanelGenPerTickNight;
        public final ModConfigSpec.DoubleValue basicSolarPanelCapacity;

        public final ModConfigSpec.DoubleValue improvedSolarPanelGenPerTick;
        public final ModConfigSpec.DoubleValue improvedSolarPanelGenPerTickNight;
        public final ModConfigSpec.DoubleValue improvedSolarPanelCapacity;

        public final ModConfigSpec.DoubleValue advancedSolarPanelGenPerTick;
        public final ModConfigSpec.DoubleValue advancedSolarPanelGenPerTickNight;
        public final ModConfigSpec.DoubleValue advancedSolarPanelCapacity;

        public final ModConfigSpec.DoubleValue perfectSolarPanelGenPerTick;
        public final ModConfigSpec.DoubleValue perfectSolarPanelGenPerTickNight;
        public final ModConfigSpec.DoubleValue perfectSolarPanelCapacity;
        public final ModConfigSpec.DoubleValue quantumSolarPanelGenPerTick;
        public final ModConfigSpec.DoubleValue quantumSolarPanelGenPerTickNight;
        public final ModConfigSpec.DoubleValue quantumSolarPanelCapacity;


        public final ModConfigSpec.DoubleValue improvedEnergyCellCapacity;
        public final ModConfigSpec.DoubleValue advancedEnergyCellCapacity;
        public final ModConfigSpec.DoubleValue perfectEnergyCellCapacity;
        public final ModConfigSpec.DoubleValue quantumEnergyCellCapacity;
        public final ModConfigSpec.IntValue trashcanBaseOperations;

        public final ModConfigSpec.BooleanValue quantumCpuBatchToPatternProviders;
        public final ModConfigSpec.IntValue quantumCpuMaxBatchPerProvider;
        public final ModConfigSpec.IntValue quantumCpuMaxDropItemsOnBreak;

        public final ModConfigSpec.IntValue maxItemCellTypes;
        public final ModConfigSpec.IntValue maxFluidCellTypes;

        public CommonConfig(ModConfigSpec.Builder builder) {
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

            builder.comment("Quantum CPU config").push("quantumCpu");
            quantumCpuBatchToPatternProviders = builder
                    .comment("Jobs running on a Quantum CPU send processing patterns to pattern providers in batches",
                            "(several crafts per push, like the old Quantum Interface did).",
                            "Only for providers without blocking mode and without crafting lock.",
                            "Disable if some machine behind a pattern provider must receive exactly one craft per push.")
                    .define("batchToPatternProviders", true);
            quantumCpuMaxBatchPerProvider = builder
                    .comment("Max crafts of one processing pattern pushed into one pattern provider at once (old maxDelegationSizePerInterface).")
                    .defineInRange("maxBatchPerProvider", 65536, 1, Integer.MAX_VALUE);
            quantumCpuMaxDropItemsOnBreak = builder
                    .comment("Maximum individual item units dropped after Quantum CPU recovery tries ME storage.",
                            "Shared by crafting contents, pending outputs, patterns and modules; the block item is separate.",
                            "Excess items and non-item resources that ME cannot accept are deliberately discarded; fluids do not use this cap.")
                    .defineInRange("maxDropItemsOnBreak", 1000, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

}