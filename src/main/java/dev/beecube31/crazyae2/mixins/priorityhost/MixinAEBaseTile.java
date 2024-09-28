package dev.beecube31.crazyae2.mixins.priorityhost;

import appeng.tile.AEBaseTile;
import appeng.util.SettingsFrom;
import dev.beecube31.crazyae2.common.interfaces.IChangeablePriorityHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEBaseTile.class, remap = false)
public abstract class MixinAEBaseTile {
    @Inject(method = "uploadSettings", at = @At("TAIL"), remap = false)
    private void addOwnHostForUpload(SettingsFrom from, NBTTagCompound compound, EntityPlayer player, CallbackInfo ci) {
        if (this instanceof final IChangeablePriorityHost pOwnHost) {
            pOwnHost.setPriority(compound.getInteger("priority"));
        }
    }

    @Inject(method = "downloadSettings", at = @At("HEAD"), remap = false, cancellable = true)
    private void addOwnHostForDownload(SettingsFrom from, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this instanceof final IChangeablePriorityHost pOwnHost) {
            NBTTagCompound finalOut = cir.getReturnValue();
            finalOut.setInteger("priority", pOwnHost.getPriority());
            cir.setReturnValue(finalOut);
        }
    }
}
