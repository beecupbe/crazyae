package dev.beecube31.crazyae2.common.blocks.solars;

import appeng.block.AEBaseTileBlock;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockPanelAdvanced extends AEBaseTileBlock {

    public BlockPanelAdvanced() {
        super(Material.IRON);
        this.setHardness(6F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(Utils.writeSpriteFlag(Sprite.SUN) + CrazyAETooltip.PASSIVE_GENERATION_DAY.getLocalWithSpaceAtEnd() + CrazyAEConfig.advancedSolarPanelGenPerTick + " AE/t");
        tooltip.add(Utils.writeSpriteFlag(Sprite.MOON) + CrazyAETooltip.PASSIVE_GENERATION_NIGHT.getLocalWithSpaceAtEnd() + CrazyAEConfig.advancedSolarPanelGenPerTickNight + " AE/t");
        tooltip.add(Utils.writeSpriteFlag(Sprite.CAPACITY) + CrazyAETooltip.MAX_SOLAR_CAPACITY.getLocalWithSpaceAtEnd() + CrazyAEConfig.advancedSolarPanelCapacity + " AE");
        super.addInformation(stack, player, tooltip, advanced);
    }
}
