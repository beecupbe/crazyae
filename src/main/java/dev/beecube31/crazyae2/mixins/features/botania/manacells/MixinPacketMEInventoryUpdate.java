package dev.beecube31.crazyae2.mixins.features.botania.manacells;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import dev.beecube31.crazyae2.common.containers.ContainerManaTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = PacketMEInventoryUpdate.class, remap = false)
public abstract class MixinPacketMEInventoryUpdate extends AppEngPacket {

    @Shadow @Final @Nullable private List<IAEItemStack> list;

    @Inject(
            method = "clientPacketData",
            at = @At("RETURN"),
            remap = false
    )
    private void patchForManaTerm(INetworkInfo network, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        final Container c = player.openContainer;

        if (c instanceof ContainerManaTerminal) {
            ((ContainerManaTerminal) c).postUpdate(this.list);
        }
    }
}
