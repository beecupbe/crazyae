package dev.beecube31.crazyae2.common.duality;

import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngNetworkInventory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PerfectInterfaceDuality extends DualityInterface {
    private final AENetworkProxy proxy;

    public PerfectInterfaceDuality(AENetworkProxy networkProxy, IInterfaceHost ih) {
        super(networkProxy, ih);
        this.proxy = networkProxy;

        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalAEInventory(this, 36, Integer.MAX_VALUE),
                "config"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngNetworkInventory(
                        this::getStorageGrid,
                        ObfuscationReflectionHelper.getPrivateValue(
                                DualityInterface.class,
                                this,
                                "mySource"
                        ),
                        this,
                        36,
                        Integer.MAX_VALUE
                ),
                "storage"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new IAEItemStack[] {
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null
                },
                "requireWork"
        );
    }

    private IStorageGrid getStorageGrid() {
        try {
            return this.proxy.getStorage();
        } catch (GridAccessException ignored) {
            return null;
        }
    }
}