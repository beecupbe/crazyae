package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.client.me.EnergyRepo;
import net.minecraft.item.ItemStack;

public class InternalEnergySlotME {

    private final int offset;
    private final int xPos;
    private final int yPos;
    private final EnergyRepo repo;

    public InternalEnergySlotME(final EnergyRepo def, final int offset, final int displayX, final int displayY) {
        this.offset = offset;
        this.repo = def;
        this.xPos = displayX;
        this.yPos = displayY;
    }

    ItemStack getStack() {
        return this.getAEStack() == null ? ItemStack.EMPTY : this.getAEStack().asItemStackRepresentation();
    }

    ItemStack getDefinition() {
        return this.getAEStack() == null ? ItemStack.EMPTY : this.getAEStack().getDefinition();
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
