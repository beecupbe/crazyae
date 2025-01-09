package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.container.slot.IOptionalSlot;
import appeng.container.slot.IOptionalSlotHost;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class OptionalSlotFake extends SlotFake implements IOptionalSlot {

    private final int srcX;
    private final int srcY;
    private final int groupNum;
    private final IOptionalSlotHost host;
    private boolean renderDisabled = true;

    public OptionalSlotFake(final IItemHandler inv, final IOptionalSlotHost containerBus, final int idx, final int x, final int y, final int offX, final int offY, final int groupNum) {
        super(inv, idx, x + offX * 18, y + offY * 18);
        this.srcX = x;
        this.srcY = y;
        this.groupNum = groupNum;
        this.host = containerBus;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        if (!this.isSlotEnabled()) {
            if (!this.getDisplayStack().isEmpty()) {
                this.clearStack();
            }
        }

        return super.getStack();
    }

    @Override
    public boolean isSlotEnabled() {
        if (this.host == null) {
            return false;
        }

        return this.host.isSlotEnabled(this.groupNum);
    }

    @Override
    public int getSlotStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isRenderDisabled() {
        return this.renderDisabled;
    }

    public void setRenderDisabled(final boolean renderDisabled) {
        this.renderDisabled = renderDisabled;
    }

    @Override
    public int getSourceX() {
        return this.srcX;
    }

    @Override
    public int getSourceY() {
        return this.srcY;
    }
}
