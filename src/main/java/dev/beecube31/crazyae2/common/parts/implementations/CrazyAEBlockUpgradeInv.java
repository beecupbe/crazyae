package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.config.Upgrades;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CrazyAEBlockUpgradeInv extends CrazyAEUpgradeInventory {
    private final Block block;

    public CrazyAEBlockUpgradeInv(final Block block, final IAEAppEngInventory parent, final int s) {
        super(parent, s);
        this.block = block;
    }

    @Override
    public int getMaxInstalled(final Upgrades upgrades) {
        int max = 0;

        for (final ItemStack is : upgrades.getSupported().keySet()) {
            final Item encodedItem = is.getItem();

            if (encodedItem instanceof ItemBlock && Block.getBlockFromItem(encodedItem) == this.block) {
                max = upgrades.getSupported().get(is);
                break;
            }
        }

        return max;
    }

    @Override
    public int getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType upgrades) {
        int max = 0;

        for (final ItemStack is : upgrades.getSupported().keySet()) {
            final Item encodedItem = is.getItem();

            if (encodedItem instanceof ItemBlock && Block.getBlockFromItem(encodedItem) == this.block) {
                max = upgrades.getSupported().get(is);
                break;
            }
        }

        return max;
    }
}
