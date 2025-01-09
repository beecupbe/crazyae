package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.common.containers.ContainerElventradeMechanical;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalElventrade;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiElventradeMechanical extends GuiBotaniaDeviceBase {

    public GuiElventradeMechanical(final InventoryPlayer inventoryPlayer, final TileMechanicalElventrade te) {
        super(inventoryPlayer, te, new ContainerElventradeMechanical(inventoryPlayer, te));
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
        return "guis/elventrade_mechanical.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.MECHANICAL_ELVENTRADE;
    }
}
