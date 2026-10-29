package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.inventories.InternalInventory;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.world.item.ItemStack;

public class SlotOutput extends CrazyAESlot {

    public SlotOutput(InternalInventory inv, int invSlot, final StateSprite icon) {
        super(inv, invSlot);
        if (icon != null)
            this.setIcon(icon);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
