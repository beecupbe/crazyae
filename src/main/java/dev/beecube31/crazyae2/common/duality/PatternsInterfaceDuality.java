package dev.beecube31.crazyae2.common.duality;

import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngInternalInventory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PatternsInterfaceDuality extends DualityInterface {
    public static final int NUMBER_OF_PATTERN_SLOTS = 72;


    public PatternsInterfaceDuality(AENetworkProxy networkProxy, IInterfaceHost ih) {
        super(networkProxy, ih);
        ObfuscationReflectionHelper.setPrivateValue(
                DualityInterface.class,
                this,
                new AppEngInternalInventory(this, 72, 1),
                "patterns"
        );
    }
}