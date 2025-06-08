package dev.beecube31.crazyae2.client.gui.implementations;


import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.BasicButton;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerQuantumCPU;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.core.client.CrazyAEClientState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.io.IOException;


public class GuiQuantumCPU extends CrazyAEBaseGui {

    private final ContainerQuantumCPU container;
    private OptionSideButton craftingBlockList;
    private BasicButton pageUpButton;
    private BasicButton pageDownButton;

    public GuiQuantumCPU(final InventoryPlayer inventoryPlayer, final TileQuantumCPU te) {
        super(new ContainerQuantumCPU(inventoryPlayer, te));
        this.ySize = 256;
        this.xSize += 26;
        this.container = (ContainerQuantumCPU) this.inventorySlots;
    }

    @Override
    protected void actionPerformed(final @NotNull GuiButton par1GuiButton) throws IOException {
        super.actionPerformed(par1GuiButton);
        if (par1GuiButton == this.craftingBlockList) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.GUI_CRAFTING_BLOCKS_LIST));
        } else if (par1GuiButton == this.pageUpButton) {
            changePage(true);
        } else if (par1GuiButton == this.pageDownButton) {
            changePage(false);
        }
    }

    private void changePage(boolean scrollUp) {
        int currentPage = this.container.getCurrentPage();
        int newPage = scrollUp ? currentPage - 1 : currentPage + 1;
        int totalPages = this.container.getTotalPages();
        if (newPage < 0) newPage = 0;
        if (newPage >= totalPages) newPage = totalPages > 0 ? totalPages - 1 : 0;

        try {
            if (currentPage != newPage) {
                CrazyAEClientState.lastQCpuPage = newPage;
                this.container.setCurrentPage(newPage);
                NetworkHandler.instance().sendToServer(new PacketToggleGuiObject("CRAZYAE.GUI.QCPU.page.change", String.valueOf(newPage)));
                updatePageButtonStates();
            }
        } catch (Throwable ignored) {}
    }

    private void goToPage(int pageNum) {
        int currentPage = this.container.getCurrentPage();
        int totalPages = this.container.getTotalPages();
        if (pageNum < 0) pageNum = 0;
        if (pageNum >= totalPages) pageNum = totalPages > 0 ? totalPages - 1 : 0;

        try {
            if (currentPage != pageNum) {
                CrazyAEClientState.lastQCpuPage = pageNum;
                this.container.setCurrentPage(pageNum);
                NetworkHandler.instance().sendToServer(new PacketToggleGuiObject("CRAZYAE.GUI.QCPU.page.change", String.valueOf(pageNum)));
                updatePageButtonStates();
            }
        } catch (Throwable ignored) {}
    }


    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(this.craftingBlockList = new OptionSideButton(
                this.guiLeft + 174,
                this.guiTop,
                StateSprite.QUARTZ_WRENCH,
                CrazyAEGuiText.OPEN_CRAFTING_BLOCK_LIST.getLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.DEFAULT
        ));

        this.buttonList.add(this.pageUpButton = new BasicButton(998, this.guiLeft + 69, this.guiTop + 160, 17, 11, "", this.getGuiHue()).hide(true));
        this.buttonList.add(this.pageDownButton = new BasicButton(999, this.guiLeft + 90, this.guiTop + 160, 17, 11, "", this.getGuiHue()).hide(true));

        this.goToPage(CrazyAEClientState.lastQCpuPage);
        updatePageButtonStates();
    }

    private void updatePageButtonStates() {
        int currentPage = this.container.getCurrentPage();
        int totalPages = this.container.getTotalPages();

        this.pageUpButton.enabled = currentPage > 0;
        this.pageDownButton.enabled = currentPage < totalPages - 1 && totalPages > 1;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            int slotAreaXStart = this.guiLeft + 17;
            int slotAreaYStart = this.guiTop + 15;
            int slotAreaXEnd = slotAreaXStart + 8 * 18;
            int slotAreaYEnd = slotAreaYStart + 8 * 18;

            if (mouseX >= slotAreaXStart && mouseX < slotAreaXEnd && mouseY >= slotAreaYStart && mouseY < slotAreaYEnd) {
                changePage(dWheel > 0);
            }
        }
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        String pageText = String.format("%d / %d", this.container.getCurrentPage() + 1, Math.max(1, this.container.getTotalPages()));
        this.drawString(this.getGuiDisplayName(this.getName().getLocal())
                + " - " + CrazyAEGuiText.PAGE.getLocalWithSpaceAtEnd() + pageText, 8, 6);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updatePageButtonStates();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(getBackground());
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    protected String getBackground() {
        return "guis/quantum_cpu.png";
    }

    protected CrazyAEGuiText getName() {
        return CrazyAEGuiText.QUANTUM_CPU;
    }
}
