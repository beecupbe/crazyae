package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.guisync.GuiSync;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.slots.CrazyAESlot;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/**
 * The Quantum CPU has more unit slots than the regular combiner. Build its menu from the actual
 * inventories rather than using the regular combiner menu's fixed slot count.
 */
public class ContainerQuantumCPUCraftingBlocks extends CrazyAEBaseContainer implements ISubMenu {
    private final TileQuantumCPU quantumCpu;

    @GuiSync(0)
    public long storageBytes;
    @GuiSync(1)
    public int threads;

    public ContainerQuantumCPUCraftingBlocks(MenuType<ContainerQuantumCPUCraftingBlocks> type, int id, Inventory ip,
                                             TileQuantumCPU host) {
        super(type, id, ip, host);
        this.quantumCpu = host;

        var accelerators = host.getAcceleratorsInv();
        for (int i = 0; i < accelerators.size(); i++) {
            this.addSlot(new CrazyAESlot(accelerators, i), CrazySemanticsContainer.COMBINER_ACCELERATORS);
        }
        var storage = host.getStorageInv();
        for (int i = 0; i < storage.size(); i++) {
            this.addSlot(new CrazyAESlot(storage, i), CrazySemanticsContainer.COMBINER_STORAGE);
        }
        this.createPlayerInventorySlots(ip);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            this.storageBytes = quantumCpu.getStorageBytes();
            this.threads = quantumCpu.getAcceleratorThreads();
        }
        super.broadcastChanges();
    }

    @Override
    public ISubMenuHost getHost() {
        return quantumCpu;
    }
}
