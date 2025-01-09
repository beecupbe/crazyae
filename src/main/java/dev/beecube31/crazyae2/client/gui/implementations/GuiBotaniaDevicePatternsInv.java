package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.widgets.GuiImgButton;
import dev.beecube31.crazyae2.common.containers.ContainerBotaniaDevicePatternsInv;
import dev.beecube31.crazyae2.common.containers.ContainerFastMAC;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBotaniaDevicePatternsInv extends GuiCrazyAEUpgradeable {

    private final ContainerBotaniaDevicePatternsInv container;
    private final BotaniaMechanicalDeviceType type;

    public GuiBotaniaDevicePatternsInv(final InventoryPlayer inventoryPlayer, final TileBotaniaMechanicalMachineBase te) {
        super(new ContainerBotaniaDevicePatternsInv(inventoryPlayer, te));
        this.type = te.getType();
        this.ySize = 197;
        this.container = (ContainerBotaniaDevicePatternsInv) this.inventorySlots;
        this.setDisableDrawInventoryString(true);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void addButtons() {
        this.redstoneMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.buttonList.add(this.redstoneMode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        this.drawString(this.getGuiDisplayName(this.getName().getLocal()), 8, 6);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
    }

    @Override
    protected String getBackground() {
        return "guis/botania_mechanical_inv.png";
    }

    @Override
    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.PATTERNS_INV_GUI;
    }
}
