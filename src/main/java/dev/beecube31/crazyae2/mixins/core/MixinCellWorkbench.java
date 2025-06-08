package dev.beecube31.crazyae2.mixins.core;

import appeng.api.config.CopyMode;
import appeng.api.config.Settings;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.tile.AEBaseTile;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.TileCellWorkbench;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import dev.beecube31.crazyae2.common.items.cells.energy.MultiEnergyItemCell;
import dev.beecube31.crazyae2.common.items.cells.energy.MultiEnergyItemCreativeCell;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.*;

@Mixin(value = TileCellWorkbench.class, remap = false)
public abstract class MixinCellWorkbench extends AEBaseTile implements IUpgradeableHost, IAEAppEngInventory, IConfigManagerHost {

    @Shadow @Final private AppEngInternalInventory cell;

    @Shadow private boolean locked;

    @Shadow private IItemHandler cacheUpgrades;

    @Shadow private IItemHandler cacheConfig;

    @Shadow protected abstract IItemHandler getCellConfigInventory();

    @Shadow @Final private ConfigManager manager;

    @Shadow @Final private AppEngInternalAEInventory config;

    @Shadow public abstract ICellWorkbenchItem getCell();

    /**
     * @author Beecube31
     * @reason Support Multi Energy cells
     */
    @Overwrite
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removedStack, final ItemStack newStack) {
        if (inv == this.cell && !this.locked) {
            this.locked = true;

            this.cacheUpgrades = null;
            this.cacheConfig = null;

            final IItemHandler configInventory = this.getCellConfigInventory();
            if (configInventory != null) {
                boolean cellHasConfig = false;
                for (int x = 0; x < configInventory.getSlots(); x++) {
                    if (!configInventory.getStackInSlot(x).isEmpty()) {
                        cellHasConfig = true;
                        break;
                    }
                }

                if (cellHasConfig) {
                    for (int x = 0; x < this.config.getSlots(); x++) {
                        this.config.setStackInSlot(x, configInventory.getStackInSlot(x));
                    }
                } else {
                    ItemHandlerUtil.copy(this.config, configInventory, false);
                }
            } else if (this.manager.getSetting(Settings.COPY_MODE) == CopyMode.CLEAR_ON_REMOVE) {
                for (int x = 0; x < this.config.getSlots(); x++) {
                    this.config.setStackInSlot(x, ItemStack.EMPTY);
                }

                this.saveChanges();
            }

            this.locked = false;
        } else if (inv == this.config && !this.locked) {
            this.crazyae$defaultUpdate();

            ItemStack cell = this.cell.getStackInSlot(0);
            boolean isEnergyCell = !cell.isEmpty() && (cell.getItem() instanceof MultiEnergyItemCell || cell.getItem() instanceof MultiEnergyItemCreativeCell);
            if (isEnergyCell) {
                final IItemHandler c = this.getCellConfigInventory();
                Utils.updateEnergyHandler(c);
                Utils.copy(c, this.config, false);
            }
        }
    }

    @Unique
    private void crazyae$defaultUpdate() {
        this.locked = true;
        final IItemHandler c = this.getCellConfigInventory();
        if (c != null) {
            Utils.copy(this.config, c, false);
            Utils.copy(c, this.config, false);
        }
        this.locked = false;
    }
}
