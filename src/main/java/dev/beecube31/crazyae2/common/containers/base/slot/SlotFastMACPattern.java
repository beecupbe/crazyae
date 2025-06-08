package dev.beecube31.crazyae2.common.containers.base.slot;

import dev.beecube31.crazyae2.common.containers.ContainerFastMAC;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotFastMACPattern extends CrazyAESlot {
    private final ContainerFastMAC mac;

    public SlotFastMACPattern(ContainerFastMAC mac, IItemHandler i, int slotIdx, int x, int y) {
        super(i, slotIdx, x, y);
        this.mac = mac;
    }

    public boolean isItemValid(@NotNull ItemStack i) {
        return this.mac.isValidItemForSlot(this.getSlotIndex(), i);
    }
}
