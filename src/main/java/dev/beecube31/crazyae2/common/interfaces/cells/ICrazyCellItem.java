package dev.beecube31.crazyae2.common.interfaces.cells;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.ICellWorkbenchItem;
import dev.beecube31.crazyae2.common.item.cell.inv.CrazyCellHandler;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface ICrazyCellItem extends ICellWorkbenchItem {
    /**
     * Basic cell items are limited to a single {@link AEKeyType}.
     */
    AEKeyType getKeyType();

    /**
     * The number of bytes that can be stored on this type of storage cell.
     * <p/>
     *
     * @param cellItem item
     * @return number of bytes
     */
    long getBytes(ItemStack cellItem);

    /**
     * Determines the number of bytes used for any type included on the cell.
     *
     * @param cellItem item
     * @return number of bytes
     */
    long getBytesPerType(ItemStack cellItem);

    /**
     * Indicates how many types can be stored on this type of storage cell.
     *
     * @param cellItem item
     * @return number of types
     */
    int getTotalTypes(ItemStack cellItem);

    /**
     * Allows you to fine tune which items are allowed on a given cell, if you don't care, just return false; As the
     * handler for this type of cell is still the default cells, the normal AE black list is also applied.
     *
     * @param cellItem          item
     * @param requestedAddition requested addition
     * @return true to preventAdditionOfItem
     */
    default boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return false;
    }

    /**
     * Allows you to specify if this storage cell can be stored inside other storage cells, only set this for special
     * items like the matter cannon that are not general purpose storage.
     *
     * @return true if the storage cell can be stored inside other storage cells, this is generally false, except for
     *         certain situations such as the matter cannon.
     */
    default boolean storableInStorageCell() {
        return false;
    }

    /**
     * Allows an item to selectively enable or disable its status as a storage cell.
     *
     * @param i item
     * @return if the ItemStack should currently be usable as a storage cell.
     */
    default boolean isStorageCell(ItemStack i) {
        return true;
    }

    /**
     * @return drain in ae/t this storage cell will use.
     */
    double getIdleDrain();

    /**
     * Convenient helper to append useful tooltip information.
     */
    default void addCellInformationToTooltip(ItemStack is, java.util.function.Consumer<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        CrazyCellHandler.INSTANCE.addCellInformationToTooltip(is, lines);
    }

    /**
     * Helper to get the additional tooltip image line showing the content/filter/upgrades.
     */
    default Optional<TooltipComponent> getCellTooltipImage(ItemStack is) {
        Preconditions.checkArgument(is.getItem() == this);
        return CrazyCellHandler.INSTANCE.getTooltipImage(is);
    }
}
