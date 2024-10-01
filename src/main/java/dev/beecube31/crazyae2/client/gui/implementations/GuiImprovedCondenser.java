package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.Settings;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiProgressBar;
import appeng.client.gui.widgets.GuiProgressBar.Direction;
import appeng.container.implementations.ContainerCondenser;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.tile.misc.TileCondenser;
import dev.beecube31.crazyae2.client.gui.widgets.ProgressBar;
import dev.beecube31.crazyae2.common.containers.ContainerImprovedCondenser;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiImprovedCondenser extends AEBaseGui {

    private final ContainerImprovedCondenser cvc;
    private ProgressBar pb;
    private GuiImgButton mode;

    public GuiImprovedCondenser(final InventoryPlayer inventoryPlayer, final TileImprovedCondenser te) {
        super(new ContainerImprovedCondenser(inventoryPlayer, te));
        this.cvc = (ContainerImprovedCondenser) this.inventorySlots;
        this.ySize = 197;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (this.mode == btn) {
            NetworkHandler.instance().sendToServer(new PacketConfigButton(Settings.CONDENSER_OUTPUT, backwards));
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pb = new ProgressBar(this.cvc, "guis/condenser.png", 120 + this.guiLeft, 25 + this.guiTop, 178, 25, 6, 18, ProgressBar.Direction.VERTICAL, GuiText.StoredEnergy
                .getLocal());

        this.mode = new GuiImgButton(128 + this.guiLeft, 52 + this.guiTop, Settings.CONDENSER_OUTPUT, this.cvc.getOutput());

        this.buttonList.add(this.pb);
        this.buttonList.add(this.mode);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.Condenser.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);

        this.mode.set(this.cvc.getOutput());
        this.mode.setFillVar(String.valueOf(this.cvc.getOutput().requiredPower));
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.bindTexture("guis/condenser.png");

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    public void bindTexture(String file) {
        ResourceLocation loc = new ResourceLocation("crazyae", "textures/" + file);
        this.mc.getTextureManager().bindTexture(loc);
    }
}
