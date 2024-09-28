package dev.beecube31.crazyae2.mixins.features.wirelessbooster;

import appeng.api.networking.IGrid;
import appeng.core.AEConfig;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.networking.TileWireless;
import dev.beecube31.crazyae2.core.CrazyAE;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileWireless.class, remap = false)
public abstract class MixinTileWireless {

    @Shadow protected abstract int getBoosters();

    @Shadow @Final private AppEngInternalInventory inv;

    @Shadow public abstract IGrid getGrid();

    @Inject(
            method = "getRange",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/core/AEConfig;instance()Lappeng/core/AEConfig;",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            remap = false,
            cancellable = true
    )
    private void injectQuantumWirelessBoosterTileGetRange(CallbackInfoReturnable<Double> cir) {
        double boosters;
        int count = this.getBoosters();

        final boolean quantumBooster = CrazyAE.definitions().items().quantumWirelessBooster()
                .isSameAs(this.inv.getStackInSlot(0));

        boosters = quantumBooster ? Math.pow(2, count)
                : this.inv.getStackInSlot(0).isEmpty() ? 0 : count;

        cir.setReturnValue(quantumBooster ? boosters : 10 * this.wireless_getMaxRange(boosters));
    }

    @Unique
    public double wireless_getMaxRange(final double boosters) {
        return AEConfig.instance().getWirelessBaseRange() * (boosters * 8) * AEConfig.instance()
                .getWirelessBoosterRangeMultiplier();
    }
}
