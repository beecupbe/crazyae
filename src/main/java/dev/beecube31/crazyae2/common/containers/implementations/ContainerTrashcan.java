package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.menu.SlotSemantics;
import dev.beecube31.crazyae2.common.containers.slots.FakeSlot;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcan;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerTrashcan extends ContainerCrazyUpgradeable<TileTrashcan> {
    public ContainerTrashcan(MenuType<ContainerTrashcan> type, int id, Inventory ip, TileTrashcan host) {
        super(type, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        var inv = getHost().getConfig().createMenuWrapper();
        for (int i = 0; i < TileTrashcan.CONFIG_SLOTS; i++) {
            this.addSlot(new FakeSlot(inv, i), SlotSemantics.CONFIG);
        }
    }
}
