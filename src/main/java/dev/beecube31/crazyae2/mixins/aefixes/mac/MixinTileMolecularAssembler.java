package dev.beecube31.crazyae2.mixins.aefixes.mac;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.implementations.tiles.ICraftingMachine;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.tile.crafting.TileMolecularAssembler;
import appeng.tile.grid.AENetworkInvTile;
import appeng.util.IConfigManagerHost;
import dev.beecube31.crazyae2.core.client.CrazyAEClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileMolecularAssembler.class, remap = false)
public abstract class MixinTileMolecularAssembler extends AENetworkInvTile implements IUpgradeableHost, IConfigManagerHost, IGridTickable, ICraftingMachine, IPowerChannelState {

    @Shadow protected abstract void updateSleepiness();

    @Shadow private boolean isAwake;

    @Inject(
            method = "tickingRequest",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;<init>(IDDDD)V", shift = At.Shift.BEFORE),
            remap = false,
            cancellable = true
    )
    private void injectToPacketAnimation(IGridNode node, int ticksSinceLastCall, CallbackInfoReturnable<TickRateModulation> cir) {
        if (CrazyAEClientConfig.aeFixes.disableMolecularAssemblerCraftingAnimation) {
            this.saveChanges();
            this.updateSleepiness();
            cir.setReturnValue(this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP);
        }
    }
}
