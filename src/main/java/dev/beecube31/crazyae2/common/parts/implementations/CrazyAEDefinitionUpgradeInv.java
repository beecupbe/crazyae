package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.util.inv.IAEAppEngInventory;
import net.minecraft.item.ItemStack;

public class CrazyAEDefinitionUpgradeInv extends CrazyAEUpgradeInventory {
    private final IItemDefinition definition;

    public CrazyAEDefinitionUpgradeInv(final IItemDefinition definition, final IAEAppEngInventory parent, final int s) {
        super(parent, s);

        this.definition = definition;
    }

    @Override
    public int getMaxInstalled(final Upgrades upgrades) {
        int max = 0;

        for (final ItemStack stack : upgrades.getSupported().keySet()) {
            if (this.definition.isSameAs(stack)) {
                max = upgrades.getSupported().get(stack);
                break;
            }
        }

        return max;
    }

    @Override
    public int getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType upgrades) {
        int max = 0;

        for (final ItemStack stack : upgrades.getSupported().keySet()) {
            if (this.definition.isSameAs(stack)) {
                max = upgrades.getSupported().get(stack);
                break;
            }
        }

        return max;
    }
}
