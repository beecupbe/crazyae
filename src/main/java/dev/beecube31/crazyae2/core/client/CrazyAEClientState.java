package dev.beecube31.crazyae2.core.client;

import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.common.config.Configuration.CATEGORY_CLIENT;

@SideOnly(Side.CLIENT)
public class CrazyAEClientState {
    public static int lastQCpuPage = 0;

    public static void applyColorizerGui(int red, int green, int blue, @Nullable ComponentHue guiHue) {
        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerColorRed",
                CrazyAEClientConfig.getColorizerColorRed(),
                0,
                255,
                "Text red color for the Gui colorizer.",
                red
        );

        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerColorGreen",
                CrazyAEClientConfig.getColorizerColorGreen(),
                0,
                255,
                "Text green color for the Gui colorizer.",
                green
        );

        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerColorBlue",
                CrazyAEClientConfig.getColorizerColorBlue(),
                0,
                255,
                "Text blue color for the Gui colorizer.",
                blue
        );

        CrazyAEClientConfig.getConfig().save();

        if (guiHue != null) {
            guiHue.setParams(
                (float) red / 255,
                (float) green / 255,
                (float) blue / 255,
                1.0F
            );
        }
    }

    public static void applyColorizerText(int textRed, int textGreen, int textBlue, @Nullable ComponentHue textHue) {
        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerTextColorRed",
                CrazyAEClientConfig.getColorizerTextColorRed(),
                0,
                255,
                "Text red color for the Gui colorizer.",
                textRed
        );

        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerTextColorGreen",
                CrazyAEClientConfig.getColorizerTextColorGreen(),
                0,
                255,
                "Text green color for the Gui colorizer.",
                textGreen
        );

        CrazyAEClientConfig.updateIntKey(
                CATEGORY_CLIENT,
                "colorizerTextColorBlue",
                CrazyAEClientConfig.getColorizerTextColorBlue(),
                0,
                255,
                "Text blue color for the Gui colorizer.",
                textBlue
        );

        CrazyAEClientConfig.getConfig().save();

        if (textHue != null) {
            textHue.setParams(
                    (float) textRed / 255,
                    (float) textGreen / 255,
                    (float) textBlue / 255,
                    1.0F
            );
        }
    }
}
