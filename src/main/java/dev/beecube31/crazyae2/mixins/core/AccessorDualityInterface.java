package dev.beecube31.crazyae2.mixins.core;

import appeng.api.networking.IGrid;
import appeng.helpers.DualityInterface;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = DualityInterface.class, remap = false)
public interface AccessorDualityInterface {
    @Accessor
    AENetworkProxy getGridProxy();

    @Invoker("sameGrid")
    boolean sameGrid(IGrid grid) throws GridAccessException;
}
