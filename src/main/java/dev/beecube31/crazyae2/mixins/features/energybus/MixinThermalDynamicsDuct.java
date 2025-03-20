package dev.beecube31.crazyae2.mixins.features.energybus;

import appeng.api.parts.IPart;
import appeng.api.util.AEPartLocation;
import appeng.tile.networking.TileCableBus;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.api.IEnergyStorage;
import cofh.thermaldynamics.duct.Duct;
import cofh.thermaldynamics.duct.energy.DuctUnitEnergy;
import cofh.thermaldynamics.duct.energy.GridEnergy;
import cofh.thermaldynamics.duct.tiles.DuctUnit;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import dev.beecube31.crazyae2.common.interfaces.IEnergyBus;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyExportBus;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyImportBus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(value = DuctUnitEnergy.class, remap = false)
public abstract class MixinThermalDynamicsDuct extends DuctUnit<DuctUnitEnergy, GridEnergy, IEnergyReceiver> implements IEnergyStorage {
    public MixinThermalDynamicsDuct(TileGrid parent, Duct duct) {
        super(parent, duct);
    }

    @Inject(
            method = "cacheTile*",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void crazyae$onCacheTile(@Nonnull final TileEntity tile, byte side, CallbackInfoReturnable<DuctUnitEnergy.Cache> cir) {
        if (tile instanceof TileCableBus cb) {
            IPart victim = cb.getCableBus().getPart(AEPartLocation.fromFacing(EnumFacing.VALUES[side ^ 1]));
            if (victim instanceof IEnergyBus s) {
                switch (s.getBusType()) {
                    case IMPORT ->
                            cir.setReturnValue(new DuctUnitEnergy.Cache(((PartEnergyImportBus) s).getRfDelegate()));

                    case EXPORT ->
                            cir.setReturnValue(new DuctUnitEnergy.Cache(((PartEnergyExportBus) s).getRfDelegate()));
                }
            }
        }
    }
}
