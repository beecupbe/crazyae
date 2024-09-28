package dev.beecube31.crazyae2.common.items;

import appeng.items.AEBaseItem;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class QuantumWirelessBooster extends AEBaseItem {

    public QuantumWirelessBooster() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        lines.add(CrazyAETooltip.QUANTUM_WIRELESS_BOOSTER_DESC.getLocal());
    }
}
