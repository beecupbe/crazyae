package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.AEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.TooltipArea;
import dev.beecube31.crazyae2.common.containers.ContainerCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiCraftingUnitsCombiner extends AEBaseGui {

    public GuiCraftingUnitsCombiner(final InventoryPlayer inventoryPlayer, final TileCraftingUnitsCombiner te) {
        super(new ContainerCraftingUnitsCombiner(inventoryPlayer, te));
        this.ySize = 197;
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new TooltipArea(0, 10 + this.guiLeft, 5 + this.guiTop, 44, 48, CrazyAEGuiTooltip.CU_PLACE_ALL_ACCELERATORS_HERE.getLocal()));
        this.buttonList.add(new TooltipArea(1, 10 + this.guiLeft, 62 + this.guiTop, 44, 48, CrazyAEGuiTooltip.CU_PLACE_ALL_CRAFTING_STORAGES_HERE.getLocal()));
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
//        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.Condenser.getLocal()), 8, 6, 4210752);
//        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.bindTexture("guis/crafting_units_combiner.png");

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    public void bindTexture(String file) {
        ResourceLocation loc = new ResourceLocation("crazyae", "textures/" + file);
        this.mc.getTextureManager().bindTexture(loc);
    }
}
