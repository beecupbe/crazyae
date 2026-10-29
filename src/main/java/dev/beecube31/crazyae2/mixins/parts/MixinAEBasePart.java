package dev.beecube31.crazyae2.mixins.parts;

import appeng.parts.automation.IOBusPart;
import dev.beecube31.crazyae2.common.interfaces.parts.ICrazyPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IOBusPart.class, remap = false)
public abstract class MixinAEBasePart {
    @Inject(method = "onUseWithoutItem", at = @At("HEAD"), cancellable = true)
    private void crazyae$redirectOnCrazyPart(Player player, Vec3 pos, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ICrazyPart cp) {
            cir.setReturnValue(cp.onCrazyPartActivate(player, InteractionHand.MAIN_HAND, pos));
        }
    }
}
