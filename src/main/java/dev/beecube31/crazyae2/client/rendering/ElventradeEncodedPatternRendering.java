package dev.beecube31.crazyae2.client.rendering;

import appeng.bootstrap.IItemRendering;
import appeng.bootstrap.ItemRenderingCustomizer;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.client.rendering.models.ElventradeEncodedPatternModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElventradeEncodedPatternRendering extends ItemRenderingCustomizer {
    private static final ResourceLocation MODEL = new ResourceLocation(Tags.MODID, "builtin/elventrade_encoded_pattern");

    @Override
    @SideOnly(Side.CLIENT)
    public void customize(IItemRendering rendering) {
        rendering.builtInModel("models/item/builtin/elventrade_encoded_pattern", new ElventradeEncodedPatternModel());
        rendering.model(new ModelResourceLocation(MODEL, "inventory")).variants(MODEL);
    }

}
