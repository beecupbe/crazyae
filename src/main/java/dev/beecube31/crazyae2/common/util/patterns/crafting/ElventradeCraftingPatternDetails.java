package dev.beecube31.crazyae2.common.util.patterns.crafting;

import net.minecraft.item.ItemStack;


public class ElventradeCraftingPatternDetails extends CrazyAECraftingPatternDetails implements Comparable<ElventradeCraftingPatternDetails> {
    public ElventradeCraftingPatternDetails
    (
            final ItemStack is
    ) {
        super(is);
    }

    @Override
    public ItemStack getPattern() {
        return this.patternItem;
    }

    @Override
    public int compareTo(final ElventradeCraftingPatternDetails o) {
        return Integer.compare(o.priority, this.priority);
    }

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        final ElventradeCraftingPatternDetails other = (ElventradeCraftingPatternDetails) obj;

        if (this.pattern != null && other.pattern != null) {
            return this.pattern.equals(other.pattern);
        }
        return false;
    }

    @Override
    public int getInventorySizeX() {
        return 8;
    }

    @Override
    public int getInventorySizeY() {
        return 2;
    }
}
