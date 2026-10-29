package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ITooltip;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.sprite.Sprite;
import dev.beecube31.crazyae2.common.enums.CrazySemanticsContainer;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerCraftingUnitsCombiner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class GuiCraftingUnitsCombiner extends CrazyAEBaseGui<ContainerCraftingUnitsCombiner> implements ICrazyGui {
    public GuiCraftingUnitsCombiner(ContainerCraftingUnitsCombiner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        layout(CrazySemanticsContainer.COMBINER_ACCELERATORS, 12);
        layout(CrazySemanticsContainer.COMBINER_STORAGE, 68);

        addRenderableOnly(new TooltipArea(10, 5, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.threads").append(" " + formatCount(menu.threads)),
                Component.translatable("gui.tooltip.crazyae.cu_place_all_accelerators_here").withStyle(ChatFormatting.GRAY))));
        addRenderableOnly(new TooltipArea(10, 62, 44, 48, () -> List.of(
                Component.translatable("gui.crazyae.combiner.storage").append(" " + formatBytes(menu.storageBytes)),
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

    public static String formatCount(long value) {
        String[] units = {"", " K", " M", " G", " T", " P", " E"};
        double v = value;
        int i = 0;
        while (Math.abs(v) >= 1000 && i < units.length - 1) {
            v /= 1000;
            i++;
        }
        return trim(v, i == 0) + units[i];
    }

    public static String formatBytes(long bytes) {
        String[] units = {" B", " KB", " MB", " GB", " TB", " PB", " EB"};
        double v = bytes;
        int i = 0;
        while (v >= 1024 && i < units.length - 1) {
            v /= 1024;
            i++;
        }
        return trim(v, i == 0) + units[i];
    }

    private static String trim(double v, boolean integer) {
        if (integer) {
            return String.valueOf((long) v);
        }

        var s = String.format(Locale.ROOT, "%.2f", Math.floor(v * 100) / 100);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    private class TooltipArea implements Renderable, ITooltip {
        private final int x, y, w, h;
        private final Supplier<List<Component>> lines;

        TooltipArea(int x, int y, int w, int h, Supplier<List<Component>> lines) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.lines = lines;
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

        @Override
        public List<Component> getTooltipMessage() {
            return lines.get();
        }

        @Override
        public Rect2i getTooltipArea() {
            return new Rect2i(leftPos + x, topPos + y, w, h);
        }

        @Override
        public boolean isTooltipAreaVisible() {
            return true;
        }
    }
}
