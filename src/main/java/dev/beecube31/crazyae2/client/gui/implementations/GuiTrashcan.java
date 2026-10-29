package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerTrashcan;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiTrashcan extends GuiCrazyUpgradeable<ContainerTrashcan> implements ICrazyGui {
    public GuiTrashcan(ContainerTrashcan menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
