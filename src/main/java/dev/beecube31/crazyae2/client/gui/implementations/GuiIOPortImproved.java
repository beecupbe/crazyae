package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.AEApi;
import appeng.api.config.FullnessMode;
import appeng.api.config.OperationMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.definitions.IDefinitions;
import appeng.client.gui.widgets.GuiImgButton;
import dev.beecube31.crazyae2.common.containers.ContainerIOPortImproved;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.orig.PacketConfigButton;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiIOPortImproved extends GuiCrazyAEUpgradeable {

    private GuiImgButton fullMode;
    private GuiImgButton operationMode;

    public GuiIOPortImproved(final InventoryPlayer inventoryPlayer, final TileImprovedIOPort te) {
        super(new ContainerIOPortImproved(inventoryPlayer, te));
        this.ySize = 175;
        this.setDisableDrawTileName(true);
    }

    @Override
    protected void addButtons() {
        this.redstoneMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.fullMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.FULLNESS_MODE, FullnessMode.EMPTY);
        this.operationMode = new GuiImgButton(this.guiLeft + 80, this.guiTop + 17, Settings.OPERATION_MODE, OperationMode.EMPTY);

        this.buttonList.add(this.operationMode);
        this.buttonList.add(this.redstoneMode);
        this.buttonList.add(this.fullMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (this.redstoneMode != null) {
            this.redstoneMode.set(this.cvb.getRedStoneMode());
        }

        if (this.operationMode != null) {
            this.operationMode.set(((ContainerIOPortImproved) this.cvb).getOperationMode());
        }

        if (this.fullMode != null) {
            this.fullMode.set(((ContainerIOPortImproved) this.cvb).getFullMode());
        }
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);

        final IDefinitions definitions = AEApi.instance().definitions();

        definitions.items().cell1k().maybeStack(1).ifPresent(cell1kStack -> this.drawItem(offsetX + 66 - 5, offsetY + 17, cell1kStack));

        definitions.blocks().drive().maybeStack(1).ifPresent(driveStack -> this.drawItem(offsetX + 94 + 5, offsetY + 17, driveStack));
    }

    @Override
    protected String getBackground() {
        return "guis/improved_io_port.png";
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (btn == this.fullMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.fullMode.getSetting(), backwards));
        }

        if (btn == this.operationMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(this.operationMode.getSetting(), backwards));
        }
    }
}
