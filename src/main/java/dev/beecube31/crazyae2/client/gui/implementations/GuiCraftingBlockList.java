package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.common.containers.ContainerCraftingBlockList;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiCraftingBlockList extends CrazyAEBaseGui {

    public GuiCraftingBlockList(final InventoryPlayer inventoryPlayer, final CrazyCraftContainer te) {
        super(new ContainerCraftingBlockList(inventoryPlayer, te));
        this.ySize = 197;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {

    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(
                "guis/crafting_blocks_list.png"
        );

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }
}
