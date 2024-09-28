package dev.beecube31.crazyae2.mixins.features.wirelessbooster;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.util.inv.filter.AEItemDefinitionFilter;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEItemDefinitionFilter.class, remap = false)
public abstract class MixinAEItemDefinitionFilter {

    @Shadow @Final private IItemDefinition definition;

    @Inject(
            method = "allowInsert",
            at = @At(value = "RETURN", shift = At.Shift.BEFORE),
            remap = false,
            cancellable = true
    )
    private void injectQuantumBoosterFilter(IItemHandler inv, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final boolean isSame = this.definition == AEApi.instance().definitions()
                .materials().wirelessBooster();

        final boolean isBasicBooster = AEApi.instance().definitions()
                .materials().wirelessBooster().isSameAs(stack);

        final boolean isQuantumBooster = CrazyAE.definitions()
                .items().quantumWirelessBooster().isSameAs(stack);

        cir.setReturnValue(isSame && (isBasicBooster || isQuantumBooster));
    }
}
