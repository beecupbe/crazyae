package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.inventories.ISegmentedInventory;
import appeng.core.localization.GuiText;
import appeng.menu.guisync.GuiSync;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.slots.SlotOutput;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEProgressProvider;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;

public class ContainerBigCrystalCharger extends ContainerCrazyUpgradeable<TileBigCrystalCharger> implements ICrazyAEProgressProvider {

    private final TileBigCrystalCharger te;

    @GuiSync(4)
    public int craftProgress = 0;

    public ContainerBigCrystalCharger(int id, Inventory ip, TileBigCrystalCharger te) {
        super(CrazyContainers.IMPROVED_CRYSTAL_CHARGER.get(), id, ip, te);
        this.te = te;
    }

    @Override
    protected void setupConfig() {
        final var input = this.getHost().getSubInventory(TileBigCrystalCharger.INV_INPUT);
        final var output = this.getHost().getSubInventory(TileBigCrystalCharger.INV_OUTPUT);
        final var upgrades = this.getHost().getSubInventory(ISegmentedInventory.UPGRADES);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new RestrictedSlot(RestrictedSlot.PlaceableItemType.CERTUS_QUARTZ_CRYSTALS, input,
                        x + y * 9), CrazySemanticsContainer.IMPROVED_CRYSTAL_CHARGER_INPUT);
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new SlotOutput(output, x + y * 9, RestrictedSlot.PlaceableItemType.CHARGED_CERTUS_QUARTZ_CRYSTALS.sprite),
                        CrazySemanticsContainer.IMPROVED_CRYSTAL_CHARGER_OUTPUT);
            }
        }
    }

    @Override
    public void broadcastChanges() {
        this.craftProgress = this.te.getProgress();

        super.broadcastChanges();
    }

    @Override
    public double getCurrentProgress() {
        return this.craftProgress;
    }

    @Override
    public double getMaxProgress() {
        return 100;
    }

    @Override
    public String getTooltip(String title, boolean disableMaxProgress, int tooltipID) {
        return title +
                "\n" +
                this.getCurrentProgress(tooltipID) +
                ' ' +
                GuiText.Of.getLocal() +
                ' ' +
                this.getMaxProgress(tooltipID);
    }
}
