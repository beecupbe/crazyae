package dev.beecube31.crazyae2.common.blocks.solars;

import appeng.block.AEBaseTileBlock;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockPanelImproved extends AEBaseTileBlock {

    public BlockPanelImproved() {
        super(Material.IRON);
        this.setHardness(6F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(CrazyAETooltip.PASSIVE_GENERATION_DAY.getLocalWithSpaceAtEnd() + CrazyAEConfig.improvedSolarPanelGenPerTick + " AE/t");
        tooltip.add(CrazyAETooltip.PASSIVE_GENERATION_NIGHT.getLocalWithSpaceAtEnd() + CrazyAEConfig.improvedSolarPanelGenPerTickNight + " AE/t");
        tooltip.add(CrazyAETooltip.MAX_SOLAR_CAPACITY.getLocalWithSpaceAtEnd() + CrazyAEConfig.improvedSolarPanelCapacity + " AE");
        super.addInformation(stack, player, tooltip, advanced);
    }
}
