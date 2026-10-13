package dev.beecube31.crazyae2.mixins.parts;

import appeng.parts.AEBasePart;
import dev.beecube31.crazyae2.common.interfaces.parts.ICrazyPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AEBasePart.class, remap = false)
public abstract class MixinAEBasePart {
    @Redirect(method = "onActivate", at = @At(value = "INVOKE", target = "Lappeng/parts/AEBasePart;onPartActivate(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Z"))
    private boolean crazyae$redirectOnCrazyPart(AEBasePart instance, Player player, InteractionHand hand, Vec3 pos) {
        if (instance instanceof ICrazyPart cp) {
            return cp.onCrazyPartActivate(player, hand, pos);
        }

        return instance.onPartActivate(player, hand, pos);
    }
}
