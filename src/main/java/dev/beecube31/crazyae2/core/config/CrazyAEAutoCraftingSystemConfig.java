package dev.beecube31.crazyae2.core.config;

import dev.beecube31.crazyae2.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Tags.MODID)
@Config(modid = Tags.MODID, name = "crazyae-autocrafting-system")
public final class CrazyAEAutoCraftingSystemConfig extends Configuration {
    @Config.Comment("The maximum batch size per one interface")
    @Config.Name("maxDelegationSizePerInterface")
    @Config.RangeInt(min = 1)
    public static int maxDelegationSizePerInterface = 65536;

    @Config.Comment("The maximum number of Pattern executions that the interface will attempt to send ingredients for per tick.")
    @Config.Name("maxPatternPushExecutionsPerActiveCraftTick")
    @Config.RangeInt(min = 1)
    public static int maxPatternPushExecutionsPerActiveCraftTick = 65536;

    @Config.Comment("Maximum number of Crafting Tasks per Quantum Interface.")
    @Config.Name("maxCraftingTasksSizePerQuantumInterface")
    @Config.RangeInt(min = 1)
    public static int maxCraftingTasksSizePerQuantumInterface = 1;

    static {
        ConfigManager.sync("crazyae", Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID))
            ConfigManager.sync(Tags.MODID, Config.Type.INSTANCE);
    }
}
