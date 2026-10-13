package dev.beecube31.crazyae2.mixins.crafting;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCalculator;
import dev.beecube31.crazyae2.common.block.crafting.BlockCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingBlockEntity.class, remap = false)
public abstract class MixinCraftingBlockEntity {

    @Redirect(method = "onReady", at = @At(value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCalculator;calculateMultiblock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"))
    private void crazyae$onReady(CraftingCPUCalculator calc, ServerLevel level, BlockPos pos) {
        if ((Object) this instanceof TileCraftingUnitsCombiner combiner) {
            combiner.rebuildCluster(false);
        } else {
            calc.calculateMultiblock(level, pos);
        }
    }

    @Redirect(method = "updateMultiBlock", at = @At(value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCalculator;updateMultiblockAfterNeighborUpdate(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V"))
    private void crazyae$onNeighbor(CraftingCPUCalculator calc, ServerLevel level, BlockPos pos, BlockPos changed) {
        if ((Object) this instanceof TileCraftingUnitsCombiner combiner) {
            combiner.rebuildCluster(false);
        } else {
            calc.updateMultiblockAfterNeighborUpdate(level, pos, changed);
        }
    }

    @Inject(method = "isConnected", at = @At("HEAD"), cancellable = true)
    private void crazyae$notConnectedToCombiner(BlockGetter level, BlockPos pos, Direction side,
                                                CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.relative(side)).getBlock() instanceof BlockCraftingUnitsCombiner) {
            cir.setReturnValue(false);
        }
    }
}
