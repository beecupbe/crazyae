package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.menu.slot.AppEngSlot;
import appeng.menu.MenuOpener;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerQuantumCPU extends CrazyAEBaseContainer {
    public static final int COLS = 8;
    public static final int ROWS = 8;
    public static final int SLOTS_PER_PAGE = COLS * ROWS;
    public static final int PAGES = (TileQuantumCPU.PATTERN_SLOTS + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE;

    private static final String ACTION_OPEN_CRAFTING_BLOCKS = "openCraftingBlocks";
    private static final String ACTION_OPEN_CRAFTING_STATUS = "openCraftingStatus";

    private final TileQuantumCPU host;

    public ContainerQuantumCPU(MenuType<ContainerQuantumCPU> type, int id, Inventory ip, TileQuantumCPU host) {
        super(type, id, ip, host);
        this.host = host;

        var patterns = host.getPatternsInv();
        for (int i = 0; i < patterns.size(); i++) {
            this.addSlot(new AppEngSlot(patterns, i), CrazySemanticsContainer.QUANTUM_CPU_PATTERNS);
        }

        this.createPlayerInventorySlots(ip);

        registerClientAction(ACTION_OPEN_CRAFTING_BLOCKS, this::openCraftingBlocks);
        registerClientAction(ACTION_OPEN_CRAFTING_STATUS, this::openCraftingStatus);
    }

    public TileQuantumCPU getQuantumCpu() {
        return host;
    }

    public void openCraftingBlocks() {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_CRAFTING_BLOCKS);
            return;
        }
        var locator = getLocator();
        if (locator != null) {
            ContainerOpener.open(CrazyContainers.QUANTUM_CPU_CRAFTING_BLOCKS.get(), getPlayer(), locator, true);
        }
    }

    public void openCraftingStatus() {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_CRAFTING_STATUS);
            return;
        }

        if (!isValidMenu() || getPlayer().containerMenu != this
                || host.getLevel() != getPlayer().level()
                || host.isRemoved()
                || host.getCluster() == null
                || host.getLevel().getBlockEntity(host.getBlockPos()) != host
                || !stillValid(getPlayer())
                || !getPlayer().level().mayInteract(getPlayer(), host.getBlockPos())) {
            return;
        }
        MenuOpener.open(CraftingCPUMenu.TYPE, getPlayer(), MenuLocators.forBlockEntity(host));
    }
}
