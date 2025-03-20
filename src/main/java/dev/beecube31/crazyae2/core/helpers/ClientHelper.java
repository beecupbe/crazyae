package dev.beecube31.crazyae2.core.helpers;

import dev.beecube31.crazyae2.client.rendering.models.baked.*;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientHelper implements Helper {

    public void preinit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init() {
        CrazyAE.definitions().items().elventradeEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ElventradeEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().manapoolEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ManapoolEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().runealtarEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(RunealtarEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().petalEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(PetalEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().puredaisyEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(PuredaisyEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().teraplateEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(TeraplateEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });

        CrazyAE.definitions().items().breweryEncodedPattern().maybeItem().ifPresent(pattern -> {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(BreweryEncodedPatternBakedModel.PATTERN_ITEM_COLOR_HANDLER, pattern);
        });
    }

    public void postinit() {

    }
}
