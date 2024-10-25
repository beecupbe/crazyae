package dev.beecube31.crazyae2.mixins.features.wirelessbooster;

import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerWireless;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.AEConfig;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerWireless.class, remap = false)
public abstract class MixinContainerWireless extends AEBaseContainer {

    @Shadow @Final private SlotRestrictedInput boosterSlot;

    public MixinContainerWireless(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Shadow protected abstract void setRange(long range);

    @Shadow protected abstract void setDrain(long drain);

    @Shadow public abstract void detectAndSendChanges();

    @Inject(
            method = "detectAndSendChanges",
            at = @At(value = "HEAD"),
            remap = false,
            cancellable = true
    )
    private void injectQuantumWirelessBoosterContainer(CallbackInfo ci) {
        double boosters;
        int count = this.boosterSlot.getStack().getCount();

        final boolean quantumBooster = CrazyAE.definitions().items().quantumWirelessBooster()
                .isSameAs(this.boosterSlot.getStack());

        boosters = quantumBooster ? Math.pow(2, count)
                : this.boosterSlot.getStack().isEmpty() ? 0 : count;

        this.setRange((long) (quantumBooster ? boosters : 10 * this.wireless_getMaxRange(boosters)));

        this.setDrain((long) (quantumBooster ? count * (count * 16L) : 100 * this.wireless_getPowerDrain(boosters)));

        super.detectAndSendChanges();

        ci.cancel();
    }

    @Unique
    public double wireless_getMaxRange(final double boosters) {
        return AEConfig.instance().getWirelessBaseRange() * (boosters * 8) * AEConfig.instance()
                .getWirelessBoosterRangeMultiplier();
    }

    @Unique
    public double wireless_getPowerDrain(final double boosters) {
        return AEConfig.instance().getWirelessBaseCost() * AEConfig.instance()
                .getWirelessCostMultiplier() + (boosters * 4);
    }
}
