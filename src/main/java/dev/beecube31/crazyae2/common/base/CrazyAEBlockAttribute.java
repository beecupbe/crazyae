package dev.beecube31.crazyae2.common.base;

import appeng.block.AEBaseTileBlock;
import dev.beecube31.crazyae2.common.enums.MachineAttributes;
import dev.beecube31.crazyae2.common.interfaces.attrib.IMachineAttributeProvider;
import dev.beecube31.crazyae2.common.util.Utils;
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
                Utils.addReqAePerTick(tooltip, attrib.getRequiredAEPerTick());
            }

            if (attrib.getRequiredAEPerJob() != Double.MIN_VALUE) {
                Utils.addReqAePerJob(tooltip, attrib.getRequiredAEPerJob());
            }

            if (attrib.getRequiredManaPerTick() != Double.MIN_VALUE) {
                Utils.addReqManaPerTick(tooltip, attrib.getRequiredManaPerTick());
            }

            if (attrib.getRequiredManaPerJob() != Double.MIN_VALUE) {
                Utils.addReqManaPerJob(tooltip, attrib.getRequiredManaPerJob());
            }

            if (attrib.isRequireChannel()) {
                Utils.addReqChannelTooltip(tooltip);
            }
        }

        super.addInformation(stack, player, tooltip, advanced);
    }
}
