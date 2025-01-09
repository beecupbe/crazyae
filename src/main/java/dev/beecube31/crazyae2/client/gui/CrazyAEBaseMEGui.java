package dev.beecube31.crazyae2.client.gui;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.me.SlotME;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.core.AEConfig;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public abstract class CrazyAEBaseMEGui extends CrazyAEBaseGui {

    public CrazyAEBaseMEGui(final Container container) {
        super(container);
    }

    @Override
    protected void renderToolTip(final ItemStack stack, final int x, final int y) {
        final Slot s = this.getSlot(x, y);

        final int bigNumber = AEConfig.instance().useTerminalUseLargeFont() ? 999 : 9999;
        final List<String> currentToolTip = this.getItemToolTip(stack);

        if (s instanceof SlotME && !stack.isEmpty()) {

            IAEItemStack myStack = null;

            try {
                final SlotME theSlotField = (SlotME) s;
                myStack = theSlotField.getAEStack();
            } catch (final Throwable ignore) {
            }

            if (myStack != null) {
                if (myStack.getStackSize() > 1) {
                    final String local = ButtonToolTips.ItemsStored.getLocal();
                    final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(myStack.getStackSize());
                    final String format = String.format(local, formattedAmount);

                    currentToolTip.add(TextFormatting.GRAY + format);
                }

                if (myStack.getCountRequestable() > 0) {
                    final String local = ButtonToolTips.ItemsRequestable.getLocal();
                    final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(myStack.getCountRequestable());
                    final String format = String.format(local, formattedAmount);

                    currentToolTip.add(format);
                }

                if (myStack.isCraftable() && AEConfig.instance().isShowCraftableTooltip()) {
                    final String local = ButtonToolTips.ItemsCraftable.getLocal();
                    currentToolTip.add(TextFormatting.GRAY + local);
                }

                this.drawHoveringText(currentToolTip, x, y, this.fontRenderer);

                return;
            } else if (stack.getCount() > bigNumber) {
                final String local = ButtonToolTips.ItemsStored.getLocal();
                final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(stack.getCount());
                final String format = String.format(local, formattedAmount);

                currentToolTip.add(TextFormatting.GRAY + format);

                this.drawHoveringText(currentToolTip, x, y, this.fontRenderer);

                return;
            }
        } else if (s instanceof AppEngSlot) {
            if (!(s instanceof SlotPlayerInv) && !(s instanceof SlotPlayerHotBar)) {
                if (!s.getStack().isEmpty()) {
                    final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(s.getStack().getCount());
                    currentToolTip.add(TextFormatting.GRAY + formattedAmount);
                    this.drawHoveringText(currentToolTip, x, y, this.fontRenderer);
                    return;
                }
            }
        }

        super.renderToolTip(stack, x, y);
    }
}