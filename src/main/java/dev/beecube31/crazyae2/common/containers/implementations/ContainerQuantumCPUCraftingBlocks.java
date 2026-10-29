package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.ISubMenu;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerQuantumCPUCraftingBlocks extends ContainerCraftingUnitsCombiner implements ISubMenu {
    private final TileQuantumCPU quantumCpu;

    public ContainerQuantumCPUCraftingBlocks(MenuType<ContainerQuantumCPUCraftingBlocks> type, int id, Inventory ip,
                                             TileQuantumCPU host) {
        super(type, id, ip, host);
        this.quantumCpu = host;
    }

    @Override
    public ISubMenuHost getHost() {
        return quantumCpu;
    }
}
