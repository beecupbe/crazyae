package dev.beecube31.crazyae2.client.rendering;

import appeng.bootstrap.IItemRendering;
import appeng.bootstrap.ItemRenderingCustomizer;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.client.rendering.models.PetalEncodedPatternModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PetalEncodedPatternRendering extends ItemRenderingCustomizer {
    private static final ResourceLocation MODEL = new ResourceLocation(Tags.MODID, "builtin/petal_encoded_pattern");

    @Override
    @SideOnly(Side.CLIENT)
    public void customize(IItemRendering rendering) {
        rendering.builtInModel("models/item/builtin/petal_encoded_pattern", new PetalEncodedPatternModel());
        rendering.model(new ModelResourceLocation(MODEL, "inventory")).variants(MODEL);
    }
}
