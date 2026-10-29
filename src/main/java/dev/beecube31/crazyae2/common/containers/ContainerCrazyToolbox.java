package dev.beecube31.crazyae2.common.containers;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.items.tools.NetworkToolItem;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.RestrictedInputSlot;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ContainerCrazyToolbox {
    private final CrazyAEBaseContainer menu;
    private final int slot;
    private final NetworkToolMenuHost inv;

    public ContainerCrazyToolbox(CrazyAEBaseContainer menu) {
        this.menu = menu;

        this.inv = NetworkToolItem.findNetworkToolInv(menu.getPlayer());
        if (inv != null) {
            this.slot = Objects.requireNonNullElse(inv.getPlayerInventorySlot(), 0);
            menu.lockPlayerInventorySlot(this.slot);
        } else {
            this.slot = 0;
        }

        // Add quick access slots for the upgrade cards stored in the toolbox
        if (isPresent()) {
            for (int i = 0; i < 9; i++) {
                var slot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES,
                        this.inv.getInventory(), i);
                // The toolbox is in the network tool that is part of the player inventory
                menu.addSlot(slot, SlotSemantics.TOOLBOX);
            }
        }

    }

    public boolean isPresent() {
        return this.inv != null;
    }

    public void tick() {
        if (inv != null) {
            if (!inv.isValid()) {
                menu.setValidMenu(false);
                return;
            }
            inv.tick();
        }
    }

    public Component getName() {
        return this.inv != null ? this.inv.getItemStack().getHoverName() : Component.empty();
    }

}
