package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.sprite.Sprite;
import dev.beecube31.crazyae2.client.gui.widgets.renderonly.TooltipArea;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GuiCraftingUnitsCombiner extends CrazyAEBaseGui<ContainerCraftingUnitsCombiner> implements ICrazyGui {
    public GuiCraftingUnitsCombiner(ContainerCraftingUnitsCombiner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        layout(CrazySemanticsContainer.COMBINER_ACCELERATORS, 12);
        layout(CrazySemanticsContainer.COMBINER_STORAGE, 68);

        addRenderableOnly(new TooltipArea(this.leftPos + 10, this.topPos + 5, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.threads").append(" " + Utils.formatCount(menu.threads)),
                Component.translatable("gui.tooltip.crazyae.cu_place_all_accelerators_here").withStyle(ChatFormatting.GRAY))));
        addRenderableOnly(new TooltipArea(this.leftPos + 10, this.topPos + 62, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.storage").append(" " + Utils.formatBytes(menu.storageBytes)),
                Component.translatable("gui.tooltip.crazyae.cu_place_all_crafting_storages_here").withStyle(ChatFormatting.GRAY))));
    }

    private void layout(appeng.menu.SlotSemantic semantic, int top) {
        var slots = menu.getSlots(semantic);
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).x = 62 + (i % 6) * 18;
            slots.get(i).y = top + (i / 6) * 19;
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        Sprite.CRAFTING_ACCELERATOR.getBlitter().dest(14, 10, 40, 40).blit(guiGraphics);
        Sprite.CRAFTING_STORAGE.getBlitter().dest(14, 66, 40, 40).blit(guiGraphics);
    }
}
