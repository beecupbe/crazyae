package dev.beecube31.crazyae2.mixins.cells;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "appeng.me.cells.CreativeCellInventory", remap = false)
public class MixinCreativeCellInventory {
    @ModifyArg(method = "getAvailableStacks", at = @At(value = "INVOKE",
            target = "Lappeng/api/stacks/KeyCounter;add(Lappeng/api/stacks/AEKey;J)V"))
    private long extract(long amount) {
        return Integer.MAX_VALUE * 1024L;
    }
}
