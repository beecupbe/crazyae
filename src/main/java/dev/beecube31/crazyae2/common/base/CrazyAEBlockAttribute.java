package dev.beecube31.crazyae2.common.base;

import appeng.block.AEBaseTileBlock;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.interfaces.attrib.IMachineAttributeProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class CrazyAEBlockAttribute extends AEBaseTileBlock implements IMachineAttributeProvider {
    public CrazyAEBlockAttribute(Material mat) {
        super(mat);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        final MachineAttributes attrib = this.getAttributes();

        if (attrib != null) {
            if (attrib.getRequiredAEPerTick() != Double.MIN_VALUE) {
                tooltip.add(String.format(
                        CrazyAETooltip.REQUIRE_AE_PER_TICK.getLocalWithSpaceAtEnd(),
                        attrib.getRequiredAEPerTick()
                ));
            }

            if (attrib.getRequiredAEPerJob() != Double.MIN_VALUE) {
                tooltip.add(String.format(
                        CrazyAETooltip.REQUIRE_AE_PER_JOB.getLocalWithSpaceAtEnd(),
                        attrib.getRequiredAEPerJob()
                ));
            }

            if (attrib.getRequiredManaPerTick() != Double.MIN_VALUE) {
                tooltip.add(String.format(
                        CrazyAETooltip.REQUIRE_MANA_PER_TICK.getLocalWithSpaceAtEnd(),
                        attrib.getRequiredManaPerTick()
                ));
            }

            if (attrib.getRequiredManaPerJob() != Double.MIN_VALUE) {
                tooltip.add(String.format(
                        CrazyAETooltip.REQUIRE_MANA_PER_JOB.getLocalWithSpaceAtEnd(),
                        attrib.getRequiredManaPerJob()
                ));
            }
        }

        super.addInformation(stack, player, tooltip, advanced);
    }
}
