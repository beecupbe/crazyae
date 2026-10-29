package dev.beecube31.crazyae2.common.containers.implementations;

import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;

public class ContainerDriveImproved extends CrazyAEBaseContainer {

    public ContainerDriveImproved(int id, Inventory ip, TileImprovedDrive drive) {
        super(CrazyContainers.IMPROVED_DRIVE.get(), id, ip, drive);

        var driveInventory = drive.getInternalInventory();

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 3; x++) {
                int slotIndex = x + y * 7;
                var slot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_CELLS, driveInventory, slotIndex);

                this.addSlot(slot, CrazySemanticsContainer.IMPROVED_DRIVE_GRID_0);

            }
        }

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 2; x++) {
                int slotIndex = 3 + x + y * 7;
                var slot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_CELLS, driveInventory, slotIndex);

                this.addSlot(slot, CrazySemanticsContainer.IMPROVED_DRIVE_GRID_1);

            }
        }

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 2; x++) {
                int slotIndex = 5 + x + y * 7;
                var slot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_CELLS, driveInventory, slotIndex);

                this.addSlot(slot, CrazySemanticsContainer.IMPROVED_DRIVE_GRID_2);

            }
        }

        this.createPlayerInventorySlots(ip);
    }

}
