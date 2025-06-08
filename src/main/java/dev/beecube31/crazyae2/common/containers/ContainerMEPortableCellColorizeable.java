package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.container.interfaces.IInventorySlotAware;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerMEPortableCellColorizeable extends ContainerMEMonitorableColorizeable implements IInventorySlotAware {

    protected final IPortableCell portableCell;
    private final int slot;
    private double powerMultiplier = 0.5;
    private int ticks = 0;

    public ContainerMEPortableCellColorizeable(InventoryPlayer ip, IPortableCell guiObject) {
        super(ip, guiObject, guiObject, false);
        if (guiObject != null) {
            final int slotIndex = ((IInventorySlotAware) guiObject).getInventorySlot();
            if (!((IInventorySlotAware) guiObject).isBaubleSlot()) {
                this.lockPlayerInventorySlot(slotIndex);
            }
            this.slot = slotIndex;
        } else {
            this.slot = -1;
            this.lockPlayerInventorySlot(ip.currentItem);
        }

        this.bindPlayerInventory(ip, 0, 0);

        this.portableCell = guiObject;
    }

    @Override
    public void detectAndSendChanges() {
        final ItemStack currentItem = this.slot < 0 ? this.getPlayerInv().getCurrentItem() : this.getPlayerInv().getStackInSlot(this.slot);

        if (this.portableCell == null || currentItem.isEmpty()) {
            this.setValidContainer(false);
        } else if (!this.portableCell.getItemStack().isEmpty() && currentItem != this.portableCell.getItemStack()) {
            if (!ItemStack.areItemsEqual(this.portableCell.getItemStack(), currentItem)) {
                this.setValidContainer(false);
            }
        }

        // drain 1 ae t
        this.ticks++;
        if (this.ticks > 10) {
            this.portableCell.extractAEPower(this.getPowerMultiplier() * this.ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
            this.ticks = 0;
        }
        super.detectAndSendChanges();
    }

    private double getPowerMultiplier() {
        return this.powerMultiplier;
    }

    void setPowerMultiplier(final double powerMultiplier) {
        this.powerMultiplier = powerMultiplier;
    }

    @Override
    public int getInventorySlot() {
        return this.slot;
    }
}