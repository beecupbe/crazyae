package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.core.definitions.AEItems;
import appeng.parts.automation.IOBusPart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerIOBus extends ContainerCrazyUpgradeable<IOBusPart> {

    public ContainerIOBus(MenuType<?> menuType, int id, Inventory ip, IOBusPart host) {
        super(menuType, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 7, 9, 0);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);

        return upgrades > idx;
    }
}
