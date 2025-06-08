package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.common.containers.ContainerPuredaisyMechanical;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalPuredaisy;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiPuredaisyMechanical extends GuiBotaniaDeviceBase {

    public GuiPuredaisyMechanical(final InventoryPlayer inventoryPlayer, final TileMechanicalPuredaisy te) {
        super(inventoryPlayer, te, new ContainerPuredaisyMechanical(inventoryPlayer, te));
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
        return "guis/puredaisy_mechanical.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.MECHANICAL_PUREDAISY;
    }
}
