package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCraftingBlockList extends CrazyAEBaseContainer {

    public ContainerCraftingBlockList(final InventoryPlayer ip, final CrazyCraftContainer te) {
        super(ip, te);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CRAFTING_ACCELERATORS, te
                        .getAcceleratorsInv(), x + y * 9, 8 + x * 18, 12 + y * (18 + y), this.getInventoryPlayer()));
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CRAFTING_STORAGES, te
                        .getStoragesInv(), x + y * 9, 8 + x * 18, 68 + y * (18 + y), this.getInventoryPlayer()));
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
