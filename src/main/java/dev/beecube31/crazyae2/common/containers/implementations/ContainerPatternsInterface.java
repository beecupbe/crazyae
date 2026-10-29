package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerPatternsInterface extends ContainerPatternProvider {
    public ContainerPatternsInterface(MenuType<ContainerPatternsInterface> type, int id, Inventory ip, PatternProviderLogicHost host) {
        super(type, id, ip, host);
    }
}
