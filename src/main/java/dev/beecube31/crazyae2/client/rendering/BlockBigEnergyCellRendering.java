package dev.beecube31.crazyae2.client.rendering;

import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.bootstrap.BlockRenderingCustomizer;
import appeng.bootstrap.IBlockRendering;
import appeng.bootstrap.IItemRendering;
import appeng.tile.networking.TileEnergyCell;
import dev.beecube31.crazyae2.common.items.ItemEnergyCells;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BlockBigEnergyCellRendering extends BlockRenderingCustomizer {

    private final ResourceLocation baseModel;

    public BlockBigEnergyCellRendering(ResourceLocation baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public void customize(IBlockRendering rendering, IItemRendering itemRendering) {
        itemRendering.meshDefinition(this::getItemModel);
    }

    private ModelResourceLocation getItemModel(ItemStack is) {
        double fillFactor = getFillFactor(is);

        int storageLevel = TileEnergyCell.getStorageLevelFromFillFactor(fillFactor);
        return new ModelResourceLocation(this.baseModel, "fullness=" + storageLevel);
    }

    private static double getFillFactor(ItemStack is) {
        if (!(is.getItem() instanceof IAEItemPowerStorage)) {
            return 0;
        }

        ItemEnergyCells itemChargeable = (ItemEnergyCells) is.getItem();
        double curPower = itemChargeable.getAECurrentPower(is);
        double maxPower = itemChargeable.getAEMaxPower(is);

        return curPower / maxPower;
    }
}
