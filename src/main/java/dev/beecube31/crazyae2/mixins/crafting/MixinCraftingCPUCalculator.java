package dev.beecube31.crazyae2.mixins.crafting;

import appeng.me.cluster.implementations.CraftingCPUCalculator;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingCPUCalculator.class, remap = false)
public abstract class MixinCraftingCPUCalculator {
    @Inject(method = "isValidBlockEntity", at = @At("HEAD"), cancellable = true)
    private void crazyae$excludeCombiner(BlockEntity te, CallbackInfoReturnable<Boolean> cir) {
        if (te instanceof TileCraftingUnitsCombiner) {
            cir.setReturnValue(false);
        }
    }
}
