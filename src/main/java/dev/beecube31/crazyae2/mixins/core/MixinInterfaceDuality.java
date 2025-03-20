package dev.beecube31.crazyae2.mixins.core;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.DualityInterface;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.IConfigManagerHost;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.IInventoryDestination;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DualityInterface.class, remap = false)
public abstract class MixinInterfaceDuality implements IGridTickable, IStorageMonitorable, IInventoryDestination, IAEAppEngInventory, IConfigManagerHost, ICraftingProvider, IUpgradeableHost {

    @Shadow private boolean hasConfig;

    @Shadow @Final private AppEngInternalAEInventory config;

    @Shadow protected abstract boolean hasWorkToDo();

    @Shadow @Final private IAEItemStack[] requireWork;

    @Shadow protected abstract void updatePlan(int slot);

    @Shadow @Final private AENetworkProxy gridProxy;

    @Shadow public abstract void notifyNeighbors();

    @Shadow protected abstract boolean usePlan(int x, IAEItemStack itemStack);

    /**
     * @author Beecube31
     * @reason Use this.requireWork for better compatibility with custom dualities
     */
    @Overwrite
    private void readConfig() {
        this.hasConfig = false;

        for (final ItemStack p : this.config) {
            if (!p.isEmpty()) {
                this.hasConfig = true;
                break;
            }
        }

        final boolean had = this.hasWorkToDo();

        for (int x = 0; x < this.requireWork.length; x++) {
            this.updatePlan(x);
        }

        final boolean has = this.hasWorkToDo();

        if (had != has) {
            try {
                if (has) {
                    this.gridProxy.getTick().alertDevice(this.gridProxy.getNode());
                } else {
                    this.gridProxy.getTick().sleepDevice(this.gridProxy.getNode());
                }
            } catch (final GridAccessException e) {
                // :P
            }
        }
        this.notifyNeighbors();
    }

    /**
     * @author Beecube31
     * @reason Use this.requireWork for better compatibility with custom dualities
     */
    @Overwrite
    private boolean updateStorage() {
        boolean didSomething = false;

        for (int x = 0; x < this.requireWork.length; x++) {
            if (this.requireWork[x] != null) {
                didSomething = this.usePlan(x, this.requireWork[x]) || didSomething;
            }
        }

        return didSomething;
    }
}
