package dev.beecube31.crazyae2.client.gui.implementations;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.client.gui.widgets.TooltipArea;
import dev.beecube31.crazyae2.common.containers.ContainerCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import java.io.IOException;

public class GuiCraftingUnitsCombiner extends CrazyAEBaseGui {

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
        this.drawSprite(
                Sprite.CRAFTING_ACCELERATOR.getTextureStr(),
                14,
                10,
                Sprite.CRAFTING_ACCELERATOR.getTextureX(),
                Sprite.CRAFTING_ACCELERATOR.getTextureY(),
                40,
                40,
                Sprite.CRAFTING_ACCELERATOR.getSizeX(),
                Sprite.CRAFTING_ACCELERATOR.getSizeY(),
                false
        );

        this.drawSprite(
                Sprite.CRAFTING_STORAGE.getTextureStr(),
                14,
                66,
                Sprite.CRAFTING_STORAGE.getTextureX(),
                Sprite.CRAFTING_STORAGE.getTextureY(),
                40,
                40,
                Sprite.CRAFTING_STORAGE.getSizeX(),
                Sprite.CRAFTING_STORAGE.getSizeY(),
                false
        );
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(
                "guis/crafting_units_combiner.png"
        );

        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }
}
