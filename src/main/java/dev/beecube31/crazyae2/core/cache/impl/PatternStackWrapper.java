package dev.beecube31.crazyae2.core.cache.impl;

import appeng.util.Platform;
import net.minecraft.item.ItemStack;

import java.util.Objects;

class PatternStackWrapper {
    final ItemStack stack;
    private int hash = 0;

    public PatternStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PatternStackWrapper other = (PatternStackWrapper) obj;
        return Platform.itemComparisons().isSameItem(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        if (this.hash == 0 && this.stack != null && !this.stack.isEmpty()) {
            int result = 1;
            result = 31 * result + Objects.hashCode(this.stack.getItem());
            result = 31 * result + this.stack.getItemDamage();
            if (this.stack.hasTagCompound()) {
                result = 31 * result + this.stack.getTagCompound().hashCode();
            }
            this.hash = result;
        }
        return this.hash;
    }
}
