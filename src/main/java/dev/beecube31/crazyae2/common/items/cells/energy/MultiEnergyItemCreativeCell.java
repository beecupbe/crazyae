package dev.beecube31.crazyae2.common.items.cells.energy;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.api.storage.IMEInventoryHandler;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class MultiEnergyItemCreativeCell extends AEBaseItem implements ICellWorkbenchItem {
    @Override
    public boolean isEditable(ItemStack itemStack) {
        return true;
    }

    @Override
    public IItemHandler getUpgradesInventory(ItemStack itemStack) {
        return null;
    }

    @Override
    public IItemHandler getConfigInventory(ItemStack itemStack) {
        return new CellConfig(itemStack);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return FuzzyMode.IGNORE_ALL;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {}

    @SideOnly(Side.CLIENT)
    @Override
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        lines.add(Utils.writeSpriteFlag(Sprite.INFO) + String.format(
                CrazyAEGuiTooltip.ENERGY_CELL_FORMATTING_HINT.getLocal()
        ));
        
        final IMEInventoryHandler<?> inventory = AEApi.instance()
                .registries()
                .cell()
                .getCellInventory(stack, null,
                        AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

        if (inventory instanceof ICellInventoryHandler) {
            final CellConfig cc = new CellConfig(stack);

            boolean addedEmpty = false;
            for (final ItemStack is : cc) {
                if (!is.isEmpty()) {
                    if (!addedEmpty) {
                        lines.add("");
                        addedEmpty = true;
                    }

                    lines.add(is.getDisplayName());
                }
            }
        }
    }
}
