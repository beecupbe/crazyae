package dev.beecube31.crazyae2.common.duality;

import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.AppEngInternalOversizedInventory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PerfectInterfaceDuality extends DualityInterface {

    public PerfectInterfaceDuality(AENetworkProxy networkProxy, IInterfaceHost ih) {
        super(networkProxy, ih);
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalInventory(this, 0, 1),
                "patterns"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalAEInventory(this, 36, Integer.MAX_VALUE),
                "config"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalOversizedInventory(this, 36, Integer.MAX_VALUE),
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
}