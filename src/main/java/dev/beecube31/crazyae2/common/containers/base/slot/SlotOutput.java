package dev.beecube31.crazyae2.common.containers.base.slot;

import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotOutput extends CrazyAESlot {

    public SlotOutput(final IItemHandler inv, final int id, final int x, final int y, final StateSprite icon) {
        super(inv, id, x, y);
        if (icon != null)
            this.setIIcon(icon);
    }

    @Override
    public boolean isItemValid(final @NotNull ItemStack i) {
        return false;
    }
}
