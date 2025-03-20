package dev.beecube31.crazyae2.client.rendering;

import appeng.bootstrap.IItemRendering;
import appeng.bootstrap.ItemRenderingCustomizer;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.client.rendering.models.BreweryEncodedPatternModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BreweryEncodedPatternRendering extends ItemRenderingCustomizer {
    private static final ResourceLocation MODEL = new ResourceLocation(Tags.MODID, "builtin/brewery_encoded_pattern");

    @Override
    @SideOnly(Side.CLIENT)
    public void customize(IItemRendering rendering) {
        rendering.builtInModel("models/item/builtin/brewery_encoded_pattern", new BreweryEncodedPatternModel());
        rendering.model(new ModelResourceLocation(MODEL, "inventory")).variants(MODEL);
    }
}
