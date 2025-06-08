package dev.beecube31.crazyae2.mixins.features.expcells;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageHelper;
import appeng.api.storage.data.IAEStack;
import appeng.core.api.ApiStorage;
import dev.beecube31.crazyae2.core.api.storage.exp.ExperienceStorageChannel;
import dev.beecube31.crazyae2.core.api.storage.exp.IExperienceStorageChannel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ApiStorage.class, remap = false)
public abstract class MixinApiStorages implements IStorageHelper {

    @Shadow
    public abstract <T extends IAEStack<T>, C extends IStorageChannel<T>> void registerStorageChannel(@NotNull Class<C> channel, @NotNull C factory);

    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    private void addEnergyStorageChannels(CallbackInfo ci) {
       this.registerStorageChannel(IExperienceStorageChannel.class, new ExperienceStorageChannel());
    }

}
