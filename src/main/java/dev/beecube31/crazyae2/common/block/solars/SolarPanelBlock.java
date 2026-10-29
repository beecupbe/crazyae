package dev.beecube31.crazyae2.common.block.solars;

import appeng.block.AEBaseEntityBlock;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.enums.SolarPanelTier;
import dev.beecube31.crazyae2.common.tile.solars.TileSolarPanel;
import dev.beecube31.crazyae2.common.util.CrazyTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends AEBaseEntityBlock<TileSolarPanel> {
    private final SolarPanelTier tier;

    public SolarPanelBlock(SolarPanelTier tier) {
        super(metalProps().noOcclusion());
        this.tier = tier;
    }

    public SolarPanelTier getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(CrazyAETooltip.PASSIVE_GENERATION_DAY.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getGenPerTickDay()) + " AE/t").withStyle(ChatFormatting.GRAY));
        tooltip.add(CrazyAETooltip.PASSIVE_GENERATION_NIGHT.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getGenPerTickNight()) + " AE/t").withStyle(ChatFormatting.GRAY));
        tooltip.add(CrazyAETooltip.MAX_SOLAR_CAPACITY.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getCapacity()) + " AE").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
