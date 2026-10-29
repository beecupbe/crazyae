package dev.beecube31.crazyae2.common.block.solars;

import appeng.block.AEBaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
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

    public SolarPanelBlock(Properties props, SolarPanelTier tier) {
        super(metalProps(props).noOcclusion());
        this.tier = tier;
    }

    public SolarPanelTier getTier() {
        return tier;
    }
}
