package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import dev.beecube31.crazyae2.common.containers.ContainerPatternsInterface;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEPatternsInterface;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiPatternsInterface extends GuiCrazyAEUpgradeable {

    private GuiTabButton priority;
    private GuiToggleButton interfaceMode;

    public GuiPatternsInterface(final InventoryPlayer inventoryPlayer, final ICrazyAEPatternsInterface te) {
        super(new ContainerPatternsInterface(inventoryPlayer, te));
        this.ySize = 256;
    }

    @Override
    protected void addButtons() {
        this.priority = new GuiTabButton(this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender);
        this.buttonList.add(this.priority);

        this.interfaceMode = new GuiToggleButton(this.guiLeft - 18, this.guiTop + 26, 84, 85, GuiText.InterfaceTerminal.getLocal(), GuiText.InterfaceTerminalHint.getLocal());
        this.buttonList.add(this.interfaceMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {

        if (this.interfaceMode != null) {
            this.interfaceMode.setState(((ContainerPatternsInterface) this.cvb).getInterfaceTerminalMode() == YesNo.YES);
        }

        this.fontRenderer.drawString(this.getGuiDisplayName(CrazyAEGuiText.PATTERN_INTERFACE.getLocal()), 8, 6, 4210752);
    }

    @Override
    protected String getBackground() {
        return "guis/patterns_interface.png";
    }

    @Override
    protected boolean drawPatternsInterfaceOutputSlots() {
        return true;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (btn == this.priority) {
            dev.beecube31.crazyae2.common.networking.network.NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.GUI_PRIORITY));
        }

        if (btn == this.interfaceMode) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(Settings.INTERFACE_TERMINAL, backwards));
        }
    }

}
