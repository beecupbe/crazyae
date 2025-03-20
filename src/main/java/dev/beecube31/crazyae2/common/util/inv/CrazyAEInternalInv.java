package dev.beecube31.crazyae2.common.util.inv;

import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.filter.IAEItemFilter;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class CrazyAEInternalInv extends AppEngInternalInventory {
    public CrazyAEInternalInv(IAEAppEngInventory inventory, int size, int maxStack, IAEItemFilter filter) {
        super(inventory, size, maxStack, filter);
    }

    public CrazyAEInternalInv(IAEAppEngInventory inventory, int size, int maxStack) {
        super(inventory, size, maxStack);
    }

    public CrazyAEInternalInv(IAEAppEngInventory inventory, int size) {
        super(inventory, size);
    }

    public List<ItemStack> getStacks() {
        return Collections.unmodifiableList(this.stacks);
    }
}
