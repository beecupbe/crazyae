package dev.beecube31.crazyae2.common.items.cells.handlers;

import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import dev.beecube31.crazyae2.common.items.cells.energy.MultiEnergyItemCreativeCell;
import dev.beecube31.crazyae2.common.items.cells.inv.CreativeEnergyCellInv;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import net.minecraft.item.ItemStack;

public class CreativeEnergyCellHandler implements ICellHandler {
    @Override
    public boolean isCell(ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.getItem() instanceof MultiEnergyItemCreativeCell;
    }

    @Override
    public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(ItemStack itemStack, ISaveProvider iSaveProvider, IStorageChannel<T> iStorageChannel) {
        if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class) && !itemStack.isEmpty() && itemStack
                .getItem() instanceof MultiEnergyItemCreativeCell) {
            return CreativeEnergyCellInv.getCell(itemStack);
        }
        return null;
    }

    @Override
    public int getStatusForCell(final ItemStack is, final ICellInventoryHandler handler) {
        return 2;
    }

    @Override
    public double cellIdleDrain(final ItemStack is, final ICellInventoryHandler handler) {
        return 4;
    }
}
