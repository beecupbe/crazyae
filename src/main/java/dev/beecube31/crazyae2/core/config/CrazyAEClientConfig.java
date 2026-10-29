package dev.beecube31.crazyae2.core.config;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CrazyAEClientConfig {
    public static final ClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static class ClientConfig {

        public final ModConfigSpec.IntValue colorizerColorRed;
        public final ModConfigSpec.IntValue colorizerColorGreen;
        public final ModConfigSpec.IntValue colorizerColorBlue;

        public final ModConfigSpec.IntValue colorizerTextColorRed;
        public final ModConfigSpec.IntValue colorizerTextColorGreen;
        public final ModConfigSpec.IntValue colorizerTextColorBlue;

        public final ModConfigSpec.BooleanValue isColorizingEnabled;

        public ClientConfig(ModConfigSpec.Builder builder) {
            builder.comment("Colorizing options").push("colorizing");

            colorizerColorRed = builder
                    .defineInRange("colorizerColorRed", 255, 0, 255);

            colorizerColorGreen = builder
                    .defineInRange("colorizerColorGreen", 255, 0, 255);

            colorizerColorBlue = builder
                    .defineInRange("colorizerColorBlue", 255, 0, 255);

            colorizerTextColorRed = builder
                    .defineInRange("colorizerTextColorRed", 0, 0, 255);

            colorizerTextColorGreen = builder
                    .defineInRange("colorizerTextColorGreen", 0, 0, 255);

            colorizerTextColorBlue = builder
                    .defineInRange("colorizerTextColorBlue", 0, 0, 255);

            isColorizingEnabled = builder
                    .define("isColorizingEnabled", true);

            builder.pop();
        }

        public void setGuiColorizerColors(int r, int g, int b) {
            colorizerColorRed.set(r);
            colorizerColorGreen.set(g);
            colorizerColorBlue.set(b);

            if (Minecraft.getInstance().screen instanceof CrazyAEBaseGui<?> gui) {
                gui.updateColorizingSettings();
            }
        }

        public void setTextColorizerColors(int r, int g, int b) {
            colorizerTextColorRed.set(r);
            colorizerTextColorGreen.set(g);
            colorizerTextColorBlue.set(b);

            if (Minecraft.getInstance().screen instanceof CrazyAEBaseGui<?> gui) {
                gui.updateColorizingSettings();
            }
        }
    }
}