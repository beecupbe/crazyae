package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.config.FullnessMode;
import appeng.api.config.OperationMode;
import appeng.api.config.Settings;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.slots.SlotOutput;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;

public class ContainerImprovedIOPort extends ContainerCrazyUpgradeable<TileImprovedIOPort> {

    @GuiSync(2)
    public FullnessMode fMode = FullnessMode.EMPTY;
    @GuiSync(3)
    public OperationMode opMode = OperationMode.EMPTY;

    public ContainerImprovedIOPort(int id, Inventory ip, TileImprovedIOPort host) {
        super(CrazyContainers.IMPROVED_IO_PORT.get(), id, ip, host);
    }

    @Override
    protected void setupConfig() {
        var cells = this.getHost().getSubInventory(ISegmentedInventory.CELLS);

        for (int i = 0; i < 12; i++) {
            this.addSlot(new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_CELLS, cells, i),
                    SlotSemantics.MACHINE_INPUT);
        }

        for (int i = 0; i < 12; i++) {
            this.addSlot(new SlotOutput(cells, 12 + i,
                    RestrictedSlot.PlaceableItemType.STORAGE_CELLS.sprite), SlotSemantics.MACHINE_OUTPUT);
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setOperationMode(cm.getSetting(Settings.OPERATION_MODE));
        this.setFullMode(cm.getSetting(Settings.FULLNESS_MODE));
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_CONTROLLED));
    }

    public FullnessMode getFullMode() {
        return this.fMode;
    }

    private void setFullMode(FullnessMode fMode) {
        this.fMode = fMode;
    }

    public OperationMode getOperationMode() {
        return this.opMode;
    }

    private void setOperationMode(OperationMode opMode) {
        this.opMode = opMode;
    }
}
