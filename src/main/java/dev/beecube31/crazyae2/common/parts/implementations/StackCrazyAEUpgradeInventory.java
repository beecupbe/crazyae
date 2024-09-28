package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.config.Upgrades;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.item.ItemStack;

public class StackCrazyAEUpgradeInventory extends CrazyAEUpgradeInventory {
    private final ItemStack stack;

    public StackCrazyAEUpgradeInventory(final ItemStack stack, final IAEAppEngInventory inventory, final int s) {
        super(inventory, s);
        this.stack = stack;
    }

    @Override
    public int getMaxInstalled(final Upgrades upgrades) {
        int max = 0;

        for (final ItemStack is : upgrades.getSupported().keySet()) {
            if (ItemStack.areItemsEqual(this.stack, is)) {
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
            if (ItemStack.areItemsEqual(this.stack, is)) {
                max = upgrades.getSupported().get(is);
                break;
            }
        }

        return max;
    }
}
