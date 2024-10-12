package dev.beecube31.crazyae2.mixins.features.qcm;

import appeng.api.networking.IGrid;
import appeng.core.AEConfig;
import appeng.me.GridNode;
import dev.beecube31.crazyae2.common.util.IGridChannelBoostersCache;
import org.spongepowered.asm.mixin.*;

@Mixin(value = GridNode.class, remap = false)
public abstract class MixinGridNode {

    @Shadow private int compressedData;

    @Shadow public abstract IGrid getGrid();

    /**
     * @author Beecube31
     * @reason Patch for Quantum Wireless Booster
     */
    @Overwrite
    private int getMaxChannels() {
        return calculateChannels();
    }

    @Unique
    private int calculateChannels() {
        final int boost = this.getGrid().<IGridChannelBoostersCache>getCache(IGridChannelBoostersCache.class).getChannels();
        final int[] channel_count = new int[]{
                0,
                AEConfig.instance().getNormalChannelCapacity() + boost / 4,
                AEConfig.instance().getDenseChannelCapacity() + boost
        };

        return channel_count[this.compressedData & 0x3];
    }
}
