package dev.beecube31.crazyae2.common.containers;

import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEnergyBus extends ContainerCrazyAEUpgradeable {
    public ContainerEnergyBus(InventoryPlayer ip, IUpgradesInfoProvider te) {
        super(ip, te);
    }
}
