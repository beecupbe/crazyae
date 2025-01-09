package dev.beecube31.crazyae2.mixins.features.botania.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.integration.jei.runicaltar.RunicAltarRecipeWrapper;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
@Mixin(value = RunicAltarRecipeWrapper.class, remap = false)
public abstract class MixinRuneAltarPoolRecipeWrapper implements IRecipeWrapper {

    @Shadow @Final private int manaUsage;

    /**
     * @author Beecube31
     * @reason Add required mana amount to craft in JEI.
     */
    @Overwrite
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        GlStateManager.enableAlpha();
        HUDHandler.renderManaBar(6, 98, 255, 0.75F, this.manaUsage, 100000);
        minecraft.fontRenderer.drawString(String.valueOf(this.manaUsage), 6, 89, 0, false);
        GlStateManager.disableAlpha();
    }
}
