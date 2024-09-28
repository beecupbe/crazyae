package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.SlotOutput;
import appeng.container.slot.SlotRestrictedInput;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class ContainerIOPortImproved extends ContainerCrazyAEUpgradeable {

    @GuiSync(2)
    public FullnessMode fMode = FullnessMode.EMPTY;

    @GuiSync(3)
    public OperationMode opMode = OperationMode.EMPTY;

    public ContainerIOPortImproved(InventoryPlayer ip, TileImprovedIOPort te) {
        super(ip, te);
    }

    @Override
    protected int getHeight() {
        return 175;
    }

    @Override
    protected void setupConfig() {
        int offX = 8;
        int offY = 7;

        final IItemHandler cells = this.getUpgradeable().getInventoryByName("cells");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlotToContainer(
                        new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.STORAGE_CELLS, cells, x + y * 3, offX + x * 18, offY + y * 18, this
                                .getInventoryPlayer()));
            }
        }

        offX = 116;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlotToContainer(
                        new SlotOutput(cells, 12 + x + y * 3, offX + x * 18, offY + y * 18, SlotRestrictedInput.PlacableItemType.STORAGE_CELLS.IIcon));
            }
        }

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlacableItemType.UPGRADES, upgrades, u, 187, 8 + u * 18, this.getInventoryPlayer())).setNotDraggable());
        }

    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public int availableUpgrades() {
        return 3;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.setOperationMode((OperationMode) this.getUpgradeable().getConfigManager().getSetting(Settings.OPERATION_MODE));
            this.setFullMode((FullnessMode) this.getUpgradeable().getConfigManager().getSetting(Settings.FULLNESS_MODE));
            this.setRedStoneMode((RedstoneMode) this.getUpgradeable().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
        }

        this.standardDetectAndSendChanges();
    }

    public FullnessMode getFullMode() {
        return this.fMode;
    }

    private void setFullMode(final FullnessMode fMode) {
        this.fMode = fMode;
    }

    public OperationMode getOperationMode() {
        return this.opMode;
    }

    private void setOperationMode(final OperationMode opMode) {
        this.opMode = opMode;
    }
}
