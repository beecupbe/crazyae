package dev.beecube31.crazyae2.common.util.patterns.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BreweryCraftingPatternDetails extends CrazyAECraftingPatternDetails implements Comparable<BreweryCraftingPatternDetails> {
    private final int requiredMana;

    public BreweryCraftingPatternDetails
    (
            final ItemStack is
    ) {
        super(is);
        final NBTTagCompound encodedValue = is.getTagCompound();

        this.requiredMana = encodedValue.getInteger("reqMana");
    }

    public int getRequiredMana() {
        return this.requiredMana;
    }

    @Override
    public ItemStack getPattern() {
        return this.patternItem;
    }

    @Override
    public int compareTo(final BreweryCraftingPatternDetails o) {
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

        final BreweryCraftingPatternDetails other = (BreweryCraftingPatternDetails) obj;

        if (this.pattern != null && other.pattern != null) {
            return this.pattern.equals(other.pattern);
        }
        return false;
    }

    @Override
    public int getInventorySizeX() {
        return 3;
    }

    @Override
    public int getInventorySizeY() {
        return 2;
    }
}
