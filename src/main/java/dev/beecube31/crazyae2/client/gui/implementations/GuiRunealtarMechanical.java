package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.common.containers.ContainerRunealtarMechanical;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalRunealtar;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiRunealtarMechanical extends GuiBotaniaDeviceBase {

    public GuiRunealtarMechanical(final InventoryPlayer inventoryPlayer, final TileMechanicalRunealtar te) {
        super(inventoryPlayer, te, new ContainerRunealtarMechanical(inventoryPlayer, te));
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
        return "guis/runealtar_mechanical.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.MECHANICAL_RUNEALTAR;
    }
}
