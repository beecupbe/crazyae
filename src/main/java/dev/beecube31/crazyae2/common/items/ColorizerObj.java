package dev.beecube31.crazyae2.common.items;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;

public class ColorizerObj implements IGuiItemObject {
    private final ItemStack is;

    public ColorizerObj(ItemStack is) {
        this.is = is;
    }
    @Override
    public ItemStack getItemStack() {
        return CrazyAE.definitions().items().colorizer().maybeStack(1).orElse(ItemStack.EMPTY);
    }
}