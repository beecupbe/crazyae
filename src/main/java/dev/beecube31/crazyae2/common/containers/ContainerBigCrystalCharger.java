package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOutput;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.gui.ICrazyAEProgressProvider;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.IItemHandler;

public class ContainerBigCrystalCharger extends ContainerCrazyAEUpgradeable implements ICrazyAEProgressProvider {

    private final TileBigCrystalCharger te;
    @GuiSync(4)
    public int craftProgress = 0;

    public ContainerBigCrystalCharger(InventoryPlayer ip, TileBigCrystalCharger te) {
        super(ip, te);
        this.te = te;
    }

    @Override
    protected void setupConfig() {
        final IItemHandler input = this.getUpgradeable().getInventoryByName("input");
        final IItemHandler output = this.getUpgradeable().getInventoryByName("output");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CERTUS_QUARTZ_CRYSTALS, input,
                        x + y * 9, 8 + x * 18, 18 + y * 18, this.getInventoryPlayer()));
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new SlotOutput(output, x + y * 9, 8 + x * 18, 74 + y * 18,
                        RestrictedSlot.PlaceableItemType.CHARGED_CERTUS_QUARTZ_CRYSTALS.IIcon));
            }
        }

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, u, 187, 8 + u * 18, this.getInventoryPlayer())).setStackLimit(1).setNotDraggable());
        }
    }

    @Override
    protected int getHeight() {
        return 197;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.setRedStoneMode((RedstoneMode) this.getUpgradeable().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
        }

        this.craftProgress = this.te.getProgress();

        super.detectAndSendChanges();
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
    public int getCurrentProgress() {
        return this.craftProgress;
    }

    @Override
    public int getMaxProgress() {
        return 100;
    }
}
