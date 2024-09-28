package dev.beecube31.crazyae2.common.interfaces;

import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import net.minecraft.item.ItemStack;

public interface IChangeablePriorityHost {
    /**
     * get current priority.
     */
    int getPriority();

    /**
     * set new priority
     */
    void setPriority(int newValue);

    ItemStack getItemStackRepresentation();

    CrazyAEGuiBridge getGuiBridge();
}
