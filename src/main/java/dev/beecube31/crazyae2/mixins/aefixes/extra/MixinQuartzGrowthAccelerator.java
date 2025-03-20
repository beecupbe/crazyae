package dev.beecube31.crazyae2.mixins.aefixes.extra;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.tiles.ICrystalGrowthAccelerator;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.misc.TileQuartzGrowthAccelerator;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.EnumSet;

@Mixin(value = TileQuartzGrowthAccelerator.class, remap = false)
public abstract class MixinQuartzGrowthAccelerator extends AENetworkTile implements IPowerChannelState, ICrystalGrowthAccelerator {

    /**
     * @author Beecube31
     * @reason why the valid sides is only up and down ?
     */
    @Overwrite
    public void setOrientation(final EnumFacing inForward, final EnumFacing inUp) {
        super.setOrientation(inForward, inUp);
        this.getProxy().setValidSides(EnumSet.allOf(EnumFacing.class));
    }
}
