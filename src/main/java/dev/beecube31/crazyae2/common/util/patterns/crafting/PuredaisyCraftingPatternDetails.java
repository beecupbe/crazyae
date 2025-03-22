package dev.beecube31.crazyae2.common.util.patterns.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PuredaisyCraftingPatternDetails extends CrazyAECraftingPatternDetails implements Comparable<PuredaisyCraftingPatternDetails> {
    private final boolean requireOutputBucket;

    public PuredaisyCraftingPatternDetails
    (
            final ItemStack is
    ) {
        super(is);

        final NBTTagCompound encodedValue = is.getTagCompound();

        this.requireOutputBucket = encodedValue.getBoolean("reqBucket");
    }

    public boolean requireOutputBucket() {
        return this.requireOutputBucket;
    }

    @Override
    public ItemStack getPattern() {
        return this.patternItem;
    }

    @Override
    public int compareTo(final PuredaisyCraftingPatternDetails o) {
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

        final PuredaisyCraftingPatternDetails other = (PuredaisyCraftingPatternDetails) obj;

        if (this.pattern != null && other.pattern != null) {
            return this.pattern.equals(other.pattern);
        }
        return false;
    }

    @Override
    public int getInventorySizeX() {
        return 1;
    }

    @Override
    public int getInventorySizeY() {
        return 1;
    }
}
