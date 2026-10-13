package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerImprovedMAC;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiImprovedMAC extends GuiCrazyUpgradeable<ContainerImprovedMAC> {
    public GuiImprovedMAC(ContainerImprovedMAC menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
