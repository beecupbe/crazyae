package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import appeng.core.network.serverbound.SwitchGuisPacket;
import appeng.menu.SlotSemantic;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.sprite.Sprite;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.client.gui.widgets.renderonly.TooltipArea;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerQuantumCPUCraftingBlocks;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class GuiQuantumCPUCraftingBlocks extends CrazyAEBaseGui<ContainerQuantumCPUCraftingBlocks> implements ICrazyGui {

    public GuiQuantumCPUCraftingBlocks(ContainerQuantumCPUCraftingBlocks menu, Inventory playerInventory,
                                       Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        layout(CrazySemanticsContainer.COMBINER_ACCELERATORS, 12);
        layout(CrazySemanticsContainer.COMBINER_STORAGE, 68);

        var icon = menu.getHost().getMainMenuIcon();
        var back = CrazyPriorityButton.create(this::getGuiHue, icon.getHoverName(),
                () -> PacketDistributor.sendToServer(SwitchGuisPacket.returnToParentMenu()), icon);
        back.setPosition(this.leftPos + 228, this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(back);

        addRenderableOnly(new TooltipArea(this.leftPos + 10, this.topPos + 5, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.threads")
                        .append(" " + GuiCraftingUnitsCombiner.formatCount(menu.threads)),
                Component.translatable("gui.tooltip.crazyae.cu_place_all_accelerators_here").withStyle(ChatFormatting.GRAY))));
        addRenderableOnly(new TooltipArea(this.leftPos + 10, this.topPos + 62, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.storage")
                        .append(" " + GuiCraftingUnitsCombiner.formatBytes(menu.storageBytes)),
                Component.translatable("gui.tooltip.crazyae.cu_place_all_crafting_storages_here").withStyle(ChatFormatting.GRAY))));
    }

    private void layout(SlotSemantic semantic, int top) {
        var slots = menu.getSlots(semantic);
        for (int i = 0; i < slots.size(); i++) {
            int row = i / 9;
            slots.get(i).x = 62 + (i % 9) * 18;
            slots.get(i).y = top + row * (18 + row);
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        Sprite.CRAFTING_ACCELERATOR.getBlitter().dest(14, 10, 40, 40).blit(guiGraphics);
        Sprite.CRAFTING_STORAGE.getBlitter().dest(14, 66, 40, 40).blit(guiGraphics);
    }
}
