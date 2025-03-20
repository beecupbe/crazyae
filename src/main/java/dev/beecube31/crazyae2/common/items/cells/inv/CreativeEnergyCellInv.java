package dev.beecube31.crazyae2.common.items.cells.inv;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.items.contents.CellConfig;
import appeng.me.storage.BasicCellInventoryHandler;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import net.minecraft.item.ItemStack;

public class CreativeEnergyCellInv implements IMEInventoryHandler<IAEItemStack> {

    private final IItemList<IAEItemStack> itemListCache = AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class).createList();

    protected CreativeEnergyCellInv(final ItemStack o) {
        final CellConfig cc = new CellConfig(o);
        for (final ItemStack is : cc) {
            if (!is.isEmpty()) {
                final IAEItemStack i = AEItemStack.fromItemStack(is);
                i.setStackSize(Integer.MAX_VALUE);
                this.itemListCache.add(i);
            }
        }
    }

    public static <T extends IAEStack<T>> ICellInventoryHandler<T> getCell(final ItemStack o) {
        return new BasicCellInventoryHandler(new CreativeEnergyCellInv(o), AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode, final IActionSource src) {
        final IAEItemStack local = this.itemListCache.findPrecise(input);
        if (local == null) {
            return input;
        }

        return null;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        final IAEItemStack local = this.itemListCache.findPrecise(request);
        if (local == null) {
            return null;
        }

        return request.copy();
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList out) {
        for (final IAEItemStack ais : this.itemListCache) {
            out.add(ais);
        }
        return out;
    }


    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack input) {
        return this.itemListCache.findPrecise(input) != null;
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        return this.itemListCache.findPrecise(input) != null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(final int i) {
        return true;
    }
}
