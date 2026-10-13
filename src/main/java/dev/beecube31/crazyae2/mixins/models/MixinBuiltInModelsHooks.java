package dev.beecube31.crazyae2.mixins.models;

import appeng.hooks.BuiltInModelHooks;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = BuiltInModelHooks.class, remap = false)
public class MixinBuiltInModelsHooks {
    @Shadow @Final private static Map<ResourceLocation, UnbakedModel> builtInModels;

    @Inject(
            method = "getBuiltInModel",
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    private static void crazyae$forceReturnMyModels(ResourceLocation variantId, CallbackInfoReturnable<UnbakedModel> cir) {
        if ("crazyae".equals(variantId.getNamespace()))
            cir.setReturnValue(builtInModels.get(variantId));
    }
}
