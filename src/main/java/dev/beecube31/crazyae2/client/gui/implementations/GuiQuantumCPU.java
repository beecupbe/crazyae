package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEBlocks;
import appeng.menu.slot.AppEngSlot;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerQuantumCPU;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiQuantumCPU extends CrazyAEBaseGui<ContainerQuantumCPU> {
    private static final int GRID_LEFT = 17;
    private static final int GRID_TOP = 15;

    private static int lastPage = 0;
    private int page;

    public GuiQuantumCPU(ContainerQuantumCPU menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.page = Math.min(lastPage, ContainerQuantumCPU.PAGES - 1);
    }

    @Override
    protected void init() {
        super.init();

        var blocksButton = new OptionSideButton(AEBlocks.CRAFTING_ACCELERATOR.stack(),
                CrazyAEGuiText.OPEN_CRAFTING_BLOCK_LIST.getComponent(), OptionSideButton.ButtonType.DEFAULT,
                this::getGuiHue, menu::openCraftingBlocks);
        blocksButton.setPosition(this.leftPos + CrazyPriorityButton.OFFSET_X, this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(blocksButton);

        this.addRenderableWidget(new AreaButton(this.leftPos + 69, this.topPos + 160, 17, 11, () -> setPage(page - 1)));
        this.addRenderableWidget(new AreaButton(this.leftPos + 90, this.topPos + 160, 17, 11, () -> setPage(page + 1)));

        layoutPatternSlots();
    }

    private void setPage(int newPage) {
        int pages = ContainerQuantumCPU.PAGES;
        this.page = ((newPage % pages) + pages) % pages;
        lastPage = this.page;
        layoutPatternSlots();
    }

    private void layoutPatternSlots() {
        var slots = menu.getSlots(CrazySemanticsContainer.QUANTUM_CPU_PATTERNS);
        for (int i = 0; i < slots.size(); i++) {
            var slot = slots.get(i);
            int local = i % ContainerQuantumCPU.SLOTS_PER_PAGE;
            slot.x = GRID_LEFT + (local % ContainerQuantumCPU.COLS) * 18;
            slot.y = GRID_TOP + (local / ContainerQuantumCPU.COLS) * 18;
            if (slot instanceof AppEngSlot aeSlot) {
                aeSlot.setActive(i / ContainerQuantumCPU.SLOTS_PER_PAGE == page);
            }
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var text = CrazyAEGuiText.PAGE.getComponent().getString() + " " + (page + 1) + "/" + ContainerQuantumCPU.PAGES;
        guiGraphics.drawString(this.font, text, 8, 5, 0x404040, false);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double wheelDelta) {
        double localX = x - leftPos;
        double localY = y - topPos;
        if (wheelDelta != 0 && localX >= GRID_LEFT - 1 && localX < GRID_LEFT + ContainerQuantumCPU.COLS * 18
                && localY >= GRID_TOP - 1 && localY < 172) {
            setPage(page + (wheelDelta > 0 ? -1 : 1));
            return true;
        }
        return super.mouseScrolled(x, y, wheelDelta);
    }

    private static class AreaButton extends AbstractButton {
        private final Runnable action;

        AreaButton(int x, int y, int w, int h, Runnable action) {
            super(x, y, w, h, Component.empty());
            this.action = action;
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (isHoveredOrFocused()) {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x40FFFFFF);
            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
