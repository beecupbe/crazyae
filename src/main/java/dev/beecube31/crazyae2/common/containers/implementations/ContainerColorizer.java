package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

public class ContainerColorizer extends CrazyAEBaseContainer {
    public ContainerColorizer(int id, Inventory playerInv, ItemMenuHost host) {
        super(CrazyContainers.COLORIZER.get(), id, playerInv, host);
        if (host.getItemStack().isEmpty()) {
            this.setValidMenu(false);
        }
    }

    public enum ColorizerType {
        GUI, TEXT;

        public MutableComponent toLocal() {
            return this == GUI
                    ? CrazyAEGuiText.COLORIZER_GUI_GUI_TO_TEXT.getComponent()
                    : CrazyAEGuiText.COLORIZER_GUI_TEXT_TO_GUI.getComponent();
        }

        public ColorizerType other() {
            return this == GUI ? TEXT : GUI;
        }
    }
}
