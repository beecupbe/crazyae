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
import appeng.me.storage.BasicCellInventoryHandler;
import appeng.util.item.AEItemStack;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.api.storage.IManaStorageChannel;
import net.minecraft.item.ItemStack;

public class CreativeManaCellInv implements IMEInventoryHandler<IAEItemStack> {

    protected CreativeManaCellInv() {}

    public static <T extends IAEStack<T>> ICellInventoryHandler<T> getCell() {
        return new BasicCellInventoryHandler(new CreativeManaCellInv(), AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class));
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode, final IActionSource src) {
        if (!CrazyAE.definitions().items().manaAsAEStack().isSameAs(input.asItemStackRepresentation())) {
            return input;
        }

        return null;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        if (!CrazyAE.definitions().items().manaAsAEStack().isSameAs(request.asItemStackRepresentation())) {
            return null;
        }

        return request.copy();
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList out) {
        out.add(AEItemStack.fromItemStack(CrazyAE.definitions().items().manaAsAEStack().maybeStack(Integer.MAX_VALUE).orElse(ItemStack.EMPTY)));

        return out;
    }

    @Override
    public IStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(final IAEItemStack input) {
        return CrazyAE.definitions().items().manaAsAEStack().isSameAs(input.asItemStackRepresentation());
    }

    @Override
    public boolean canAccept(final IAEItemStack input) {
        return CrazyAE.definitions().items().manaAsAEStack().isSameAs(input.asItemStackRepresentation());
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
