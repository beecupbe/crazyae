package dev.beecube31.crazyae2.mixins.features.qcm.top;

import appeng.api.parts.IPart;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.integration.modules.theoneprobe.TheOneProbeText;
import appeng.integration.modules.theoneprobe.part.ChannelInfoProvider;
import appeng.parts.networking.PartCableSmart;
import appeng.parts.networking.PartDenseCableSmart;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChannelInfoProvider.class, remap = false)
public abstract class MixinChannelsTOPDataProvider {

    /**
     * @author Beecube31
     * @reason Patch for Quantum Channels Multiplier
     */
    @Overwrite
    public void addProbeInfo(IPart part, ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (!AEConfig.instance().isFeatureEnabled(AEFeature.CHANNELS)) {
            return;
        }
        if (part instanceof PartCableSmart || part instanceof PartDenseCableSmart) {
            IGridChannelBoostersCache cache = part.getGridNode().getGrid().getCache(IGridChannelBoostersCache.class);
            final int usedChannels;
            final int boostChannels = cache.getChannels();
            final int maxChannels = cache.isForcingCreativeMultiplier() ? Integer.MAX_VALUE
                    : ((part instanceof PartDenseCableSmart) ? AEConfig.instance().getDenseChannelCapacity() + boostChannels : AEConfig.instance().getNormalChannelCapacity() + boostChannels / 4);

            if (part.getGridNode().isActive()) {
                final NBTTagCompound tmp = new NBTTagCompound();
                part.writeToNBT(tmp);
                usedChannels = tmp.getInteger("usedChannels");
            } else {
                usedChannels = 0;
            }

            final String formattedChannelString = String.format(TheOneProbeText.CHANNELS.getLocal(), usedChannels, maxChannels);

            probeInfo.text(formattedChannelString);
        }
    }
}
