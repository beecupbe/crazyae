package dev.beecube31.crazyae2.common.block.solars;

import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.util.CrazyTooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class SolarPanelBlockItem extends BlockItem {
    private final SolarPanelBlock panel;

    public SolarPanelBlockItem(SolarPanelBlock block, Properties properties) {
        super(block, properties);
        this.panel = block;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        var tier = panel.getTier();
        tooltip.accept(CrazyAETooltip.PASSIVE_GENERATION_DAY.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getGenPerTickDay()) + " AE/t").withStyle(ChatFormatting.GRAY));
        tooltip.accept(CrazyAETooltip.PASSIVE_GENERATION_NIGHT.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getGenPerTickNight()) + " AE/t").withStyle(ChatFormatting.GRAY));
        tooltip.accept(CrazyAETooltip.MAX_SOLAR_CAPACITY.getComponentWithSpaceAtEnd()
                .append(CrazyTooltips.format(tier.getCapacity()) + " AE").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
    }
}
