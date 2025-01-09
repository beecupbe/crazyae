package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.client.me.ManaRepo;
import net.minecraft.item.ItemStack;

public class InternalManaSlotME {

    private final int offset;
    private final int xPos;
    private final int yPos;
    private final ManaRepo repo;

    public InternalManaSlotME(final ManaRepo def, final int offset, final int displayX, final int displayY) {
        this.offset = offset;
        this.repo = def;
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
