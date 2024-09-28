package dev.beecube31.crazyae2.common.containers.slot;

import appeng.container.slot.AppEngSlot;
import dev.beecube31.crazyae2.common.containers.ContainerFastMAC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotFastMACPattern extends AppEngSlot {
    private final ContainerFastMAC mac;

    public SlotFastMACPattern(ContainerFastMAC mac, IItemHandler i, int slotIdx, int x, int y) {
        super(i, slotIdx, x, y);
        this.mac = mac;
    }

    public boolean isItemValid(ItemStack i) {
        return this.mac.isValidItemForSlot(this.getSlotIndex(), i);
    }
}
