package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Tags.MODID)
@Config(modid = Tags.MODID, name = "crazyae")
public final class CrazyAEConfig {
    @Config.Comment("Capacity of the Improved Energy Cell.")
    @Config.Name("impEnergyCellCap")
    @Config.RangeDouble(min = 0)
    public static double impEnergyCellCap = 262144 * 64;
    @Config.Comment("Capacity of the Advanced Energy Cell.")
    @Config.Name("advEnergyCellCap")
    @Config.RangeDouble(min = 0)
    public static double advEnergyCellCap = 262144 * 440;
    @Config.Comment("Capacity of the Perfect Energy Cell.")
    @Config.Name("perEnergyCellCap")
    @Config.RangeDouble(min = 0)
    public static double perEnergyCellCap = 262144 * 3072;

    @Config.Comment("Quantum Channels Multiplier will give extra channels by this value. (For small cables this value will be divided by 4)")
    @Config.Name("QCMBoostAmt")
    @Config.RangeInt(min = 1)
    public static int QCMBoostAmt = 8;

    @Config.Comment("Amount of types for Items Cells")
    @Config.Name("cellItemsTypesAmt")
    @Config.RangeInt(min = 1)
    public static int cellItemsTypesAmt = 63;

    @Config.Comment("Amount of types for Fluid Cells")
    @Config.Name("cellFluidTypesAmt")
    @Config.RangeInt(min = 1)
    public static int cellFluidTypesAmt = 15;


    //SolarPanels
    @Config.RangeDouble(min = 0.0D)
    public static double basicSolarPanelGenPerTick = 128;
    @Config.RangeDouble(min = 0.0D)
    public static double basicSolarPanelGenPerTickNight = 64;
    @Config.RangeDouble(min = 0.0D)
    public static double basicSolarPanelCapacity = 100000;

    @Config.RangeDouble(min = 0.0D)
    public static double improvedSolarPanelGenPerTick = 512;
    @Config.RangeDouble(min = 0.0D)
    public static double improvedSolarPanelGenPerTickNight = 256;
    @Config.RangeDouble(min = 0.0D)
    public static double improvedSolarPanelCapacity = 400000;

    @Config.RangeDouble(min = 0.0D)
    public static double advancedSolarPanelGenPerTick = 6144;
    @Config.RangeDouble(min = 0.0D)
    public static double advancedSolarPanelGenPerTickNight = 3072;
    @Config.RangeDouble(min = 0.0D)
    public static double advancedSolarPanelCapacity = 16000000;

    @Config.RangeDouble(min = 0.0D)
    public static double perfectSolarPanelGenPerTick = 32768;
    @Config.RangeDouble(min = 0.0D)
    public static double perfectSolarPanelGenPerTickNight = 16384;
    @Config.RangeDouble(min = 0.0D)
    public static double perfectSolarPanelCapacity = 64000000;

    @Config.Name("disableUpdatesCheck")
    @Config.Comment("Disables updates checking.")
    public static boolean disableUpdatesCheck = false;

    static {
        ConfigManager.sync("crazyae", Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals("crazyae")) ConfigManager.sync("crazyae", Config.Type.INSTANCE);
    }
}
