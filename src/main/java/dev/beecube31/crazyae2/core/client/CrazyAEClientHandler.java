package dev.beecube31.crazyae2.core.client;

import appeng.api.definitions.IItemDefinition;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class CrazyAEClientHandler {
    public static List<String> onTooltipDrawing(CrazyAEUpgradeModule stack, ItemStack is, IItemDefinition block) {
        return null;
    }
}
