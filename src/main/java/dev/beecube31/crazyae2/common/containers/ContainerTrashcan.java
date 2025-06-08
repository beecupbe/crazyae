package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.util.IConfigManager;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class ContainerTrashcan extends ContainerCrazyAEUpgradeable {

    public ContainerTrashcan(final InventoryPlayer ip, final TileTrashcanBase te) {
        super(ip, te);
    }

    @Override
    protected int getHeight() {
        return 256;
    }

    @Override
    protected void setupConfig() {
        final IItemHandler config = this.getUpgradeable().getInventoryByName("inv");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new SlotFake(config, x + y * 9, 8 + x * 18, 23 + y * 18, true).setStackLimit(1));
            }
        }

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, u, 187, 8 + u * 18, this.getInventoryPlayer())).setStackLimit(1).setNotDraggable());
        }
    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            final IConfigManager cm = this.getUpgradeable().getConfigManager();
            this.loadSettingsFromHost(cm);
        }

        this.checkToolbox();

        this.standardDetectAndSendChanges();
    }
}
