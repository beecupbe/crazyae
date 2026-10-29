package dev.beecube31.crazyae2.client.gui.implementations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;
import dev.beecube31.crazyae2.client.gui.CrazyGuiColors;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyPriorityButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerPerfectInterface;
import dev.beecube31.crazyae2.common.containers.slots.FakeSlot;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class GuiPerfectInterface extends GuiInterface<ContainerPerfectInterface> implements ICrazyGui {
    private static final int LEFT = 8;
    private static final int CONFIG_TOP = 23;
    private static final int STORAGE_TOP = 41;
    private static final int ROW_PAIR = 36;

    public GuiPerfectInterface(ContainerPerfectInterface menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();
        layoutRows();
        removeAmountButtons();
        replacePriorityButton();
    }

    private void removeAmountButtons() {
        for (var child : List.copyOf(this.children())) {
            if (child instanceof AbstractWidget w && "SetAmountButton".equals(w.getClass().getSimpleName())) {
                this.removeWidget(w);
            }
        }
    }

    private void replacePriorityButton() {
        for (var child : List.copyOf(this.children())) {
            if (child instanceof appeng.client.gui.widgets.TabButton) {
                this.removeWidget(child);
            }
        }
        var priority = CrazyPriorityButton
                .create(CrazyGuiColors::gui);
        priority.setPosition(this.leftPos + CrazyPriorityButton.OFFSET_X,
                this.topPos + CrazyPriorityButton.OFFSET_Y);
        this.addRenderableWidget(priority);
    }

    private void layoutRows() {
        var config = menu.getSlots(SlotSemantics.CONFIG);
        var storage = menu.getSlots(SlotSemantics.STORAGE);
        for (int i = 0; i < config.size(); i++) {
            var slot = config.get(i);
            slot.x = LEFT + (i % 9) * 18;
            slot.y = CONFIG_TOP + (i / 9) * ROW_PAIR;
        }
        for (int i = 0; i < storage.size(); i++) {
            var slot = storage.get(i);
            slot.x = LEFT + (i % 9) * 18;
            slot.y = STORAGE_TOP + (i / 9) * ROW_PAIR;
        }
    }

    @Nullable
    private FakeSlot hoveredConfigSlot() {
        if (this.hoveredSlot instanceof FakeSlot fake
                && !fake.getItem().isEmpty()
                && menu.getSlots(SlotSemantics.CONFIG).contains(fake)) {
            return fake;
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double delta) {
        if (delta != 0 && Minecraft.getInstance().hasShiftDown()) {
            var slot = hoveredConfigSlot();
            if (slot != null) {
                boolean up = delta > 0;
                int op = Minecraft.getInstance().hasControlDown()
                        ? (up ? ContainerPerfectInterface.OP_DOUBLE : ContainerPerfectInterface.OP_HALVE)
                        : (up ? ContainerPerfectInterface.OP_INCREASE : ContainerPerfectInterface.OP_DECREASE);
                int times = Math.max(1, (int) Math.round(Math.abs(delta)));
                for (int i = 0; i < times; i++) {
                    menu.adjustAmount(slot.getSlotIndex(), op);
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == 2) {
            var slot = hoveredConfigSlot();
            if (slot != null) {
                menu.openSetAmountMenu(slot.getSlotIndex());
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack stack) {
        var lines = super.getTooltipFromContainerItem(stack);
        if (hoveredConfigSlot() != null) {
            lines = new ArrayList<>(lines);
            lines.add(CrazyAEGuiTooltip.CONFIG_AMOUNT_SCROLL.getComponent().withStyle(ChatFormatting.GRAY));
            lines.add(CrazyAEGuiTooltip.CONFIG_AMOUNT_SCROLL_DOUBLE.getComponent().withStyle(ChatFormatting.GRAY));
            lines.add(CrazyAEGuiTooltip.CONFIG_AMOUNT_MIDDLE_CLICK.getComponent().withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        boolean shift = Minecraft.getInstance().hasShiftDown();
        for (var child : children()) {
            if (child instanceof AbstractWidget w && "SetAmountButton".equals(w.getClass().getSimpleName())) {
                w.visible = w.visible && shift;
            }
        }
    }
}
