package dev.beecube31.crazyae2.mixins.core;

import appeng.me.helpers.AENetworkProxy;
import appeng.parts.AEBasePart;
import dev.beecube31.crazyae2.common.interfaces.IPartActivationOverrider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AEBasePart.class, remap = false)
public abstract class MixinAEBasePart {

    @Shadow protected abstract boolean useMemoryCard(EntityPlayer player);

    @Shadow protected abstract boolean useRenamer(EntityPlayer player);

    @Shadow public abstract boolean onPartActivate(EntityPlayer player, EnumHand hand, Vec3d pos);

    @Shadow public abstract AENetworkProxy getProxy();

    /**
     * @author Beecube31
     * @reason why this method is FINAL ???
     */
    @Overwrite
    public final boolean onActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (!(this.getProxy().getMachine() instanceof IPartActivationOverrider)) {
            if (this.useMemoryCard(player) || this.useRenamer(player)) {
                return true;
            }
        }

        return this.onPartActivate(player, hand, pos);
    }
}
