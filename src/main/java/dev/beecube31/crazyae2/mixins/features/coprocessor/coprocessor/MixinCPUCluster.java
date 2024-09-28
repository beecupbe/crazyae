package dev.beecube31.crazyae2.mixins.features.coprocessor.coprocessor;

import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;
import dev.beecube31.crazyae2.common.interfaces.IDenseCoProcessor;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCPUCluster {
	@Shadow
	private int accelerator;

	@Shadow private long availableStorage;

	@Inject(method = "addTile", at = @At(
		"RETURN"
	))
	public void addTile(TileCraftingTile te, CallbackInfo ci) {
		if (te instanceof IDenseCoProcessor denseCoProcessor) {
			this.accelerator += denseCoProcessor.getAccelerationFactor();
		}

		if (te instanceof TileCraftingUnitsCombiner combiner) {
			this.accelerator += combiner.getAcceleratorAmt();
			this.availableStorage += combiner.getStorageAmt();
		}
	}
}
