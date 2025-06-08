package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerFastMAC extends ContainerCrazyAEUpgradeable {

    public ContainerFastMAC(final InventoryPlayer ip, final TileImprovedMAC te) {
        super(ip, te);
    }

    public boolean isValidItemForSlot(final int slotIndex, final ItemStack i) {
        return true;
    }

    @Override
    protected int getHeight() {
        return 197;
    }

    @Override
    protected void setupConfig() {
        final IItemHandler patternsHandler = this.getUpgradeable().getInventoryByName("patterns");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.ENCODED_PATTERN, patternsHandler, x + y * 9, 8 + x * 18, 18 + y * 18, this.getInventoryPlayer()).setStackLimit(1));
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
    public int availableUpgrades() {
        return 5;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.setRedStoneMode((RedstoneMode) this.getUpgradeable().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
        }

        this.standardDetectAndSendChanges();
    }
}
