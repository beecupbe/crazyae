package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCraftingUnitsCombiner extends CrazyAEBaseContainer {

    public ContainerCraftingUnitsCombiner(final InventoryPlayer ip, final TileCraftingUnitsCombiner te) {
        super(ip, te);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 6; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CRAFTING_ACCELERATORS, te
                        .getAcceleratorsInv(), x + y * 6, 62 + x * 18, 12 + y * (18 + y), this.getInventoryPlayer()));
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 6; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CRAFTING_STORAGES, te
                        .getStorageInv(), x + y * 6, 62 + x * 18, 68 + y * (18 + y), this.getInventoryPlayer()));
            }
        }

        this.bindPlayerInventory(ip, 0, 197 - /* height of player inventory */82);
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);
        super.detectAndSendChanges();
    }
}
