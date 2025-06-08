package dev.beecube31.crazyae2.mixins.aefixes.cells;

import appeng.me.storage.CreativeCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = CreativeCellInventory.class, remap = false)
public class MixinCreativeCellInventory {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/storage/data/IAEItemStack;setStackSize(J)Lappeng/api/storage/data/IAEStack;"
            ),
            index = 0
    )
    private long crazyae$modifyListArg(long originalSize) {
        return Integer.MAX_VALUE * 1024L;
    }
}
