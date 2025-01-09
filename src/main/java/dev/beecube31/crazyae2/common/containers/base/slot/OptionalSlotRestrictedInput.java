package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.container.slot.IOptionalSlotHost;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class OptionalSlotRestrictedInput extends RestrictedSlot {

    private final int groupNum;
    private final IOptionalSlotHost host;

    public OptionalSlotRestrictedInput(final PlaceableItemType valid, final IItemHandler i, final IOptionalSlotHost host, final int slotIndex, final int x, final int y, final int grpNum, final InventoryPlayer invPlayer) {
        super(valid, i, slotIndex, x, y, invPlayer);
        this.groupNum = grpNum;
        this.host = host;
    }

    @Override
    public boolean isSlotEnabled() {
        if (this.host == null) {
            return false;
        }

        return this.host.isSlotEnabled(this.groupNum);
    }
}
