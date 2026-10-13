package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.AppEngSlot;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerCraftingUnitsCombiner extends CrazyAEBaseContainer {
    private final TileCraftingUnitsCombiner host;

    @GuiSync(0)
    public long storageBytes;
    @GuiSync(1)
    public int threads;

    public ContainerCraftingUnitsCombiner(MenuType<ContainerCraftingUnitsCombiner> type, int id, Inventory ip, TileCraftingUnitsCombiner host) {
        super(type, id, ip, host);
        this.host = host;
        for (int i = 0; i < TileCraftingUnitsCombiner.SLOTS; i++) {
            this.addSlot(new AppEngSlot(host.getAcceleratorsInv(), i), CrazySemanticsContainer.COMBINER_ACCELERATORS);
        }
        for (int i = 0; i < TileCraftingUnitsCombiner.SLOTS; i++) {
            this.addSlot(new AppEngSlot(host.getStorageInv(), i), CrazySemanticsContainer.COMBINER_STORAGE);
        }
        this.createPlayerInventorySlots(ip);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            this.storageBytes = host.getStorageBytes();
            this.threads = host.getAcceleratorThreads();
        }
        super.broadcastChanges();
    }
}
