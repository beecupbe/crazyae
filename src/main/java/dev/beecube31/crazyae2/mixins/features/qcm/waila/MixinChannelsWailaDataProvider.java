package dev.beecube31.crazyae2.mixins.features.qcm.waila;

import appeng.api.parts.IPart;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.core.localization.WailaText;
import appeng.integration.modules.waila.part.ChannelWailaDataProvider;
import appeng.parts.networking.PartCableSmart;
import appeng.parts.networking.PartDenseCableSmart;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = ChannelWailaDataProvider.class, remap = false)
public abstract class MixinChannelsWailaDataProvider {

    @Shadow
    protected abstract int getUsedChannels(IPart part, NBTTagCompound tag, Object2IntMap<IPart> cache);

    @Shadow
    @Final
    private Object2IntMap<IPart> cache;

    /**
     * @author Beecube31
     * @reason Patch for Quantum Channels Multiplier
     */
    @Overwrite
    public List<String> getWailaBody(final IPart part, final List<String> currentToolTip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!AEConfig.instance().isFeatureEnabled(AEFeature.CHANNELS)) {
            return currentToolTip;
        }
        if (part instanceof PartCableSmart || part instanceof PartDenseCableSmart) {
            final NBTTagCompound tag = accessor.getNBTData();

            final int usedChannels = this.getUsedChannels(part, tag, this.cache);

            if (usedChannels >= 0) {
                IGridChannelBoostersCache cache = part.getGridNode().getGrid().getCache(IGridChannelBoostersCache.class);
                final int boostChannels = cache.getChannels();
                final int channels = cache.isForcingCreativeMultiplier() ? Integer.MAX_VALUE
                        : ((part instanceof PartDenseCableSmart) ? AEConfig.instance().getDenseChannelCapacity() + boostChannels : AEConfig.instance().getNormalChannelCapacity() + boostChannels / 4);

                currentToolTip.add(String.format(WailaText.Channels.getLocal(), usedChannels, channels));
            }
        }
        return currentToolTip;
    }
}
