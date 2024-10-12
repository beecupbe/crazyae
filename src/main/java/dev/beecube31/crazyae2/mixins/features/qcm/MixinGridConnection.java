package dev.beecube31.crazyae2.mixins.features.qcm;

import appeng.me.GridConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GridConnection.class, remap = false)
public abstract class MixinGridConnection {
    @Shadow protected abstract int getLastUsedChannels();

    @Inject(
            method = "canSupportMoreChannels",
            at = @At("HEAD"),
            cancellable = true
    )
    public void canSupportMoreChannels(CallbackInfoReturnable<Boolean> channel) {
        channel.setReturnValue(getLastUsedChannels() < Integer.MAX_VALUE);
    }
}
