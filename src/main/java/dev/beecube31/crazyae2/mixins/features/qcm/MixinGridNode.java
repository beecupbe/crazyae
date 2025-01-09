package dev.beecube31.crazyae2.mixins.features.qcm;

import appeng.api.networking.IGrid;
import appeng.core.AEConfig;
import appeng.me.GridNode;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GridNode.class, remap = false)
public abstract class MixinGridNode {

    @Shadow
    private int compressedData;

    @Shadow public abstract IGrid getGrid();

    /**
     * @author Beecube31
     * @reason Patch for Quantum Channels Multiplier
     */
    @Overwrite
    private int getMaxChannels() {
        return crazyae$calculateChannels();
    }

    @Unique
    private int crazyae$calculateChannels() {
        IGridChannelBoostersCache cache = this.getGrid().getCache(IGridChannelBoostersCache.class);
        final int boost = cache.getChannels();
        final int[] channel_count = new int[]{
                0,
                cache.isForcingCreativeMultiplier() ? Integer.MAX_VALUE : AEConfig.instance().getNormalChannelCapacity() + boost / 4,
                cache.isForcingCreativeMultiplier() ? Integer.MAX_VALUE : AEConfig.instance().getDenseChannelCapacity() + boost
        };

        return channel_count[this.compressedData & 0x3];
    }
}
