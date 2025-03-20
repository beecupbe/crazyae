package dev.beecube31.crazyae2.core.client;

import dev.beecube31.crazyae2.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Tags.MODID)
@Config(modid = Tags.MODID, name = "crazyae-client", category = "client")
public final class CrazyAEClientConfig extends Configuration {

    private static Configuration config;

    private static int colorizerColorRed = 255;
    private static int colorizerColorGreen = 255;
    private static int colorizerColorBlue = 255;
    private static int colorizerTextColorRed = 0;
    private static int colorizerTextColorGreen = 0;
    private static int colorizerTextColorBlue = 0;

    private static boolean isColorizingEnabled = true;
    //private static boolean isAdvancedTooltipsEnabled = true;


    public static void init(Configuration configuration) {
        if (config == null) {
            config = configuration;
            updateConfig();
        }
    }

    private static void updateConfig() {
        if (config == null) {
            throw new IllegalStateException("null config");
        }

        colorizerColorRed = getIntKey(CATEGORY_CLIENT, "colorizerColorRed", colorizerColorRed, 0, 255, "Red color for the Gui colorizer.").getInt();
        colorizerColorGreen = getIntKey(CATEGORY_CLIENT, "colorizerColorGreen", colorizerColorGreen, 0, 255, "Green color for the Gui colorizer.").getInt();
        colorizerColorBlue = getIntKey(CATEGORY_CLIENT, "colorizerColorBlue", colorizerColorBlue, 0, 255, "Blue color for the Gui colorizer.").getInt();
        colorizerTextColorRed = getIntKey(CATEGORY_CLIENT, "colorizerTextColorRed", colorizerTextColorRed, 0, 255, "Text red color for the Gui colorizer.").getInt();
        colorizerTextColorGreen = getIntKey(CATEGORY_CLIENT, "colorizerTextColorGreen", colorizerTextColorGreen, 0, 255, "Text green color for the Gui colorizer.").getInt();
        colorizerTextColorBlue = getIntKey(CATEGORY_CLIENT, "colorizerTextColorBlue", colorizerTextColorBlue, 0, 255, "Text blue color for the Gui colorizer.").getInt();
        isColorizingEnabled = getBooleanKey(CATEGORY_CLIENT, "isColorizingEnabled", isColorizingEnabled, "Enable GUI & Text colorizing?").getBoolean();
        //isAdvancedTooltipsEnabled = getBooleanKey(CATEGORY_CLIENT, "isAdvancedTooltipsEnabled", isAdvancedTooltipsEnabled, "Enable advanced tooltips with icons for AE2 and CrazyAE?").getBoolean();

        config.save();
    }

    public static Property getIntKey(String category, String key, int defaultValue, int min, int max, String comment) {
        Property property = config.get(category, key, defaultValue);
        property.setDefaultValue(defaultValue);
        property.setMinValue(min);
        property.setMaxValue(max);
        property.setComment(comment);
        return property;
    }

    public static Property getBooleanKey(String category, String key, boolean defaultValue, String comment) {
        Property property = config.get(category, key, defaultValue);
        property.setDefaultValue(defaultValue);
        property.setComment(comment);
        return property;
    }

    static void updateIntKey(String category, String key, int defaultValue, int min, int max, String comment, int newValue) {
        getIntKey(category, key, defaultValue, min, max, comment).set(newValue);
        updateConfig();
    }

    static void updateBooleanKey(String category, String key, boolean defaultValue, String comment, boolean newValue) {
        getBooleanKey(category, key, defaultValue, comment).set(newValue);
        updateConfig();
    }

    static Configuration getConfig() {
        return config;
    }

    public static boolean isColorizingEnabled() {
        return isColorizingEnabled;
    }

//    public static boolean isAdvancedTooltipsEnabled() {
//        return isAdvancedTooltipsEnabled;
//    }

    public static int getColorizerColorRed() {
        return colorizerColorRed;
    }

    public static int getColorizerColorGreen() {
        return colorizerColorGreen;
    }

    public static int getColorizerColorBlue() {
        return colorizerColorBlue;
    }

    public static int getColorizerTextColorRed() {
        return colorizerTextColorRed;
    }

    public static int getColorizerTextColorGreen() {
        return colorizerTextColorGreen;
    }

    public static int getColorizerTextColorBlue() {
        return colorizerTextColorBlue;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID)) {
            updateConfig();
        }
    }

    public static final CrazyAEFixesConfig aeFixes = new CrazyAEFixesConfig();

    public static class CrazyAEFixesConfig {
        @Config.Comment("Enable Molecular Assembler Crafting Animation? (disabling this may increase your FPS!)")
        @Config.RequiresMcRestart
        public boolean disableMolecularAssemblerCraftingAnimation = false;
    }
}
