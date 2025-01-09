package dev.beecube31.crazyae2.common.containers.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerNull extends Container {
    @Override
    public boolean canInteractWith(final EntityPlayer entityplayer) {
        return false;
    }
}
