package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.config.Upgrades;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class CrazyAEUpgradeInventory extends AppEngInternalInventory implements IAEAppEngInventory {
    private final IAEAppEngInventory parent;

    private boolean cached = false;
    private boolean cachedCrazyAE = false;

    private int stacks = 0;
    private int improvedSpeed = 0;
    private int advancedSpeed = 0;

    private int fuzzyUpgrades = 0;
    private int speedUpgrades = 0;
    private int redstoneUpgrades = 0;
    private int capacityUpgrades = 0;
    private int inverterUpgrades = 0;
    private int craftingUpgrades = 0;
    private int patternExpansionUpgrades = 0;
    private int magnetUpgrades = 0;
    private int quantumUpgrades = 0;

    public CrazyAEUpgradeInventory(final IAEAppEngInventory parent, final int s) {
        super(null, s, 1);
        this.setTileEntity(this);
        this.parent = parent;
        this.setFilter(new UpgradeInvFilter());
    }

    @Override
    protected boolean eventsEnabled() {
        return true;
    }

    public int getInstalledUpgrades(final Upgrades u) {
        if (!this.cached)
            this.updateUpgradeInfo();

        return switch (u) {
            case CAPACITY -> this.capacityUpgrades;
            case FUZZY -> this.fuzzyUpgrades;
            case REDSTONE -> this.redstoneUpgrades;
            case SPEED -> this.speedUpgrades;
            case INVERTER -> this.inverterUpgrades;
            case CRAFTING -> this.craftingUpgrades;
            case PATTERN_EXPANSION -> this.patternExpansionUpgrades;
            case MAGNET -> this.magnetUpgrades;
            case QUANTUM_LINK -> this.quantumUpgrades;
            default -> 0;
        };
    }

    public int getInstalledUpgrades(final dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
        if (!this.cachedCrazyAE)
            this.updateCrazyAEUpgradesInfo();

        return switch (u) {
            case STACKS -> this.stacks;
            case ADVANCED_SPEED -> this.advancedSpeed;
            case IMPROVED_SPEED -> this.improvedSpeed;
            default -> 0;
        };
    }

    public abstract int getMaxInstalled(Upgrades upgrades);

    public abstract int getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType upgrades);

    private void updateUpgradeInfo() {
        this.cached = true;
        this.patternExpansionUpgrades = this.inverterUpgrades = this.capacityUpgrades = this.speedUpgrades = this.redstoneUpgrades = this.fuzzyUpgrades  = this.craftingUpgrades = magnetUpgrades = quantumUpgrades = 0;

        for (final ItemStack is : this) {
            if (is == null || is.getItem() == Items.AIR || !(is.getItem() instanceof IUpgradeModule)) {
                continue;
            }

            final Upgrades myUpgrade = ((IUpgradeModule) is.getItem()).getType(is);
            switch (myUpgrade) {
                case CAPACITY:
                    this.capacityUpgrades++;
                    break;
                case FUZZY:
                    this.fuzzyUpgrades++;
                    break;
                case REDSTONE:
                    this.redstoneUpgrades++;
                    break;
                case SPEED:
                    this.speedUpgrades++;
                    break;
                case INVERTER:
                    this.inverterUpgrades++;
                    break;
                case CRAFTING:
                    this.craftingUpgrades++;
                    break;
                case PATTERN_EXPANSION:
                    this.patternExpansionUpgrades++;
                    break;
                case MAGNET:
                    this.magnetUpgrades++;
                    break;
                case QUANTUM_LINK:
                    this.quantumUpgrades++;
                default:
                    break;
            }
        }

        this.capacityUpgrades = Math.min(this.capacityUpgrades, this.getMaxInstalled(Upgrades.CAPACITY));
        this.fuzzyUpgrades = Math.min(this.fuzzyUpgrades, this.getMaxInstalled(Upgrades.FUZZY));
        this.redstoneUpgrades = Math.min(this.redstoneUpgrades, this.getMaxInstalled(Upgrades.REDSTONE));
        this.speedUpgrades = Math.min(this.speedUpgrades, this.getMaxInstalled(Upgrades.SPEED));
        this.inverterUpgrades = Math.min(this.inverterUpgrades, this.getMaxInstalled(Upgrades.INVERTER));
        this.craftingUpgrades = Math.min(this.craftingUpgrades, this.getMaxInstalled(Upgrades.CRAFTING));
        this.patternExpansionUpgrades = Math.min(this.patternExpansionUpgrades, this.getMaxInstalled(Upgrades.PATTERN_EXPANSION));
        this.magnetUpgrades = Math.min(this.magnetUpgrades, this.getMaxInstalled(Upgrades.MAGNET));
        this.quantumUpgrades = Math.min(this.quantumUpgrades, this.getMaxInstalled(Upgrades.QUANTUM_LINK));
    }

    private void updateCrazyAEUpgradesInfo() {
        this.cachedCrazyAE = true;
        this.stacks = this.improvedSpeed = this.advancedSpeed = 0;

        for (final ItemStack is : this) {
            if (is == null || is.getItem() == Items.AIR || !(is.getItem() instanceof CrazyAEUpgradeModule)) {
                continue;
            }

            final dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType myUpgrade = ((CrazyAEUpgradeModule) is.getItem()).getType(is);
            switch (myUpgrade) {
                case STACKS:
                    this.stacks++;
                    break;
                case IMPROVED_SPEED:
                    this.improvedSpeed++;
                    break;
                case ADVANCED_SPEED:
                    this.advancedSpeed++;
                    break;
                default:
                    break;
            }
        }

        this.stacks = Math.min(this.stacks, this.getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS));
        this.improvedSpeed = Math.min(this.improvedSpeed, this.getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.IMPROVED_SPEED));
        this.advancedSpeed = Math.min(this.advancedSpeed, this.getMaxInstalled(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED));
    }

    @Override
    public void readFromNBT(final NBTTagCompound target) {
        super.readFromNBT(target);
        this.updateUpgradeInfo();
    }

    @Override
    public void saveChanges() {
        if (this.parent != null) {
            this.parent.saveChanges();
        }
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removedStack, final ItemStack newStack) {
        this.cached = false;
        this.cachedCrazyAE = false;
        if (this.parent != null && Platform.isServer()) {
            this.parent.onChangeInventory(inv, slot, mc, removedStack, newStack);
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    private class UpgradeInvFilter implements IAEItemFilter {

        @Override
        public boolean allowExtract(IItemHandler inv, int slot, int amount) {
            return true;
        }

        @Override
        public boolean allowInsert(IItemHandler inv, int slot, ItemStack itemstack) {
            if (itemstack.isEmpty()) {
                return false;
            }
            final Item it = itemstack.getItem();
            if (it instanceof IUpgradeModule) {
                final Upgrades u = ((IUpgradeModule) it).getType(itemstack);

                if (u != null) {
                    return CrazyAEUpgradeInventory.this.getInstalledUpgrades(u) < CrazyAEUpgradeInventory.this.getMaxInstalled(u);
                }
            }

            if (it instanceof CrazyAEUpgradeModule) {
                final dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType cu = ((CrazyAEUpgradeModule) it).getType(itemstack);

                if (cu != null) {
                    return CrazyAEUpgradeInventory.this.getInstalledUpgrades(cu) < CrazyAEUpgradeInventory.this.getMaxInstalled(cu);
                }
            }
            return false;
        }
    }
}
