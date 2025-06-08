package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.common.containers.ContainerPetalMechanical;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalPetal;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiPetalMechanical extends GuiBotaniaDeviceBase {

    public GuiPetalMechanical(final InventoryPlayer inventoryPlayer, final TileMechanicalPetal te) {
        super(inventoryPlayer, te, new ContainerPetalMechanical(inventoryPlayer, te));
    }

    @Override
    public void initGui() {
        super.initGui();
        try {
            NetworkHandler.instance().sendToServer(new PacketToggleGuiObject("CRAZYAE.GUI.encoder.syncRecipe", ""));
        } catch (IOException ignored) {}
    }

    @Override
    protected String getBackground() {
        return "guis/petal_mechanical.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.MECHANICAL_PETAL;
    }
}
