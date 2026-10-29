package dev.beecube31.crazyae2.common.util.inv;

import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrazyAEInternalInv extends AppEngInternalInventory {

    public CrazyAEInternalInv(InternalInventoryHost host, int size, int maxStack, IAEItemFilter filter) {
        super(host, size, maxStack, filter);
    }

    public CrazyAEInternalInv(@Nullable InternalInventoryHost inventory, int size, int maxStack) {
        this(inventory, size, maxStack, null);
    }

    public CrazyAEInternalInv(@Nullable InternalInventoryHost inventory, int size) {
        this(inventory, size, 64);
    }

    public List<ItemStack> getNotEmptyStacks() {
        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack s : this) {
            if (!s.isEmpty()) {
                ret.add(s);
            }
        }
        return ret;
    }

    public CrazyAEInternalInv setItemFilter(IAEItemFilter filter) {
        this.setFilter(filter);
        return this;
    }
}
