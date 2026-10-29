package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.menu.SlotSemantics;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerImprovedMAC extends ContainerCrazyUpgradeable<TileImprovedMAC> {
    public ContainerImprovedMAC(int id, Inventory playerInv, TileImprovedMAC be) {
        super(CrazyContainers.IMPROVED_MAC.get(), id, playerInv, be);
    }


    @Override
    protected void setupConfig() {
        var mac = this.getHost().getSubInventory(TileImprovedMAC.INV_MAIN);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;
                int x = 8 + col * 18;
                int y = 18 + row * 18;

                var slot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.ENCODED_CRAFTING_PATTERN, mac, slotIndex);

                this.addSlot(slot, SlotSemantics.ENCODED_PATTERN);
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return super.quickMoveStack(player, i);
    }

    @Override
    public boolean stillValid(Player player) {
        return super.stillValid(player);
    }
}
