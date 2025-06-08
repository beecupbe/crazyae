package dev.beecube31.crazyae2.client.gui;

import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.client.me.ItemRepo;
import net.minecraft.item.ItemStack;

public class CrazyAEInternalMESlot {

    private final int offset;
    private final int xPos;
    private final int yPos;
    private final ItemRepo repo;

    public CrazyAEInternalMESlot(final ItemRepo def, final int offset, final int displayX, final int displayY) {
        this.repo = def;
        this.offset = offset;
        this.xPos = displayX;
        this.yPos = displayY;
    }

    ItemStack getStack() {
        return this.getAEStack() == null ? ItemStack.EMPTY : this.getAEStack().asItemStackRepresentation();
    }

    IAEItemStack getAEStack() {
        return this.repo.getReferenceItem(this.offset);
    }

    boolean hasPower() {
        return this.repo.hasPower();
    }

    int getxPosition() {
        return this.xPos;
    }

    int getyPosition() {
        return this.yPos;
    }
}
