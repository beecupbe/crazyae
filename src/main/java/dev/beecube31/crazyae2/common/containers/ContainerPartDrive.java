package dev.beecube31.crazyae2.common.containers;

import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.parts.implementations.PartDrive;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPartDrive extends CrazyAEBaseContainer {
    public ContainerPartDrive(final InventoryPlayer ip, final PartDrive drive) {
        super(ip, null, drive, null);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 2; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_CELLS, drive
                        .getInventoryByName("cells"), x + y * 2, 71 + x * 18, 14 + y * 18, this.getInventoryPlayer()));
            }
        }

        this.bindPlayerInventory(ip, 0, 199 - 82);
    }
}
