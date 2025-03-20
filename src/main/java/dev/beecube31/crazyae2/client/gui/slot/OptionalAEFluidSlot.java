package dev.beecube31.crazyae2.client.gui.slot;

import appeng.api.storage.data.IAEFluidStack;
import appeng.container.slot.IOptionalSlotHost;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.renderer.GlStateManager;

public class OptionalAEFluidSlot extends AEFluidSlot {
    private final IOptionalSlotHost containerBus;
    private final int groupNum;
    private final int srcX;
    private final int srcY;

    public OptionalAEFluidSlot(IAEFluidTank fluids, final IOptionalSlotHost containerBus, int slot, int id, int groupNum, int x, int y, int xoffs, int yoffs) {
        super(fluids, slot, id, x + xoffs * 18, y + yoffs * 18);
        this.containerBus = containerBus;
        this.groupNum = groupNum;
        this.srcX = x;
        this.srcY = y;
    }

    @Override
    public boolean isSlotEnabled() {
        if (this.containerBus == null) {
            return false;
        }
        return this.containerBus.isSlotEnabled(this.groupNum);
    }

    @Override
    public IAEFluidStack getFluidStack() {
        if (!this.isSlotEnabled() && super.getFluidStack() != null) {
            this.setFluidStack(null);
        }
        return super.getFluidStack();
    }

    @Override
    public void drawBackground(int guileft, int guitop) {
        GlStateManager.enableBlend();
        if (this.isSlotEnabled()) {
            this.getSlotHue().drawHue();
        } else {
            this.getSlotHue().setAlpha(0.4F);
        }
        this.getSlotHue().drawHue();

        this.drawTexturedModalRect(guileft + this.xPos() - 1, guitop + this.yPos() - 1, this.srcX - 1, this.srcY - 1, this.getWidth() + 2,
                this.getHeight() + 2);

        this.getSlotHue().setAlpha(1.0F);
        this.getSlotHue().endDrawHue();
    }
}
