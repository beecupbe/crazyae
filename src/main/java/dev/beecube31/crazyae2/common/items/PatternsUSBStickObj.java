package dev.beecube31.crazyae2.common.items;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;

public class PatternsUSBStickObj implements IGuiItemObject {
    private final ItemStack is;

    public PatternsUSBStickObj(ItemStack is) {
        this.is = is;
    }
    @Override
    public ItemStack getItemStack() {
        return null;
        //return CrazyAE.definitions().items().patternsUSBStick().maybeStack(1).orElse(ItemStack.EMPTY);
    }
}