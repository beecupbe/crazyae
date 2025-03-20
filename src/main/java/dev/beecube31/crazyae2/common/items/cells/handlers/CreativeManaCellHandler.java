package dev.beecube31.crazyae2.common.items.cells.handlers;

import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import dev.beecube31.crazyae2.common.items.cells.inv.CreativeManaCellInv;
import dev.beecube31.crazyae2.common.items.cells.storage.ManaItemCreativeCell;
import dev.beecube31.crazyae2.core.api.storage.IManaStorageChannel;
import net.minecraft.item.ItemStack;

public class CreativeManaCellHandler implements ICellHandler {
    @Override
    public boolean isCell(ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.getItem() instanceof ManaItemCreativeCell;
    }


    @Override
    public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(ItemStack itemStack, ISaveProvider iSaveProvider, IStorageChannel<T> iStorageChannel) {
        if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class) && !itemStack.isEmpty() && itemStack
                .getItem() instanceof ManaItemCreativeCell) {
            return CreativeManaCellInv.getCell();
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
