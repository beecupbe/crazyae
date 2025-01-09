package dev.beecube31.crazyae2.common.containers;

import appeng.api.networking.IGridNode;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOutput;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalElventrade;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class ContainerElventradeMechanical extends ContainerMechanicalBotaniaTileBase {
    public ContainerElventradeMechanical(InventoryPlayer ip, TileMechanicalElventrade te) {
        super(ip, te);
    }


    @Override
    protected void initSlots() {
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        final IItemHandler input = this.getUpgradeable().getInventoryByName("input");
        final IItemHandler output = this.getUpgradeable().getInventoryByName("output");

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                this.addSlotToContainer(new SlotFake(input, x + y * 8, 17 + 18 * x, 14 + 18 * y, true));
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                this.addSlotToContainer(new SlotOutput(output, x + y * 8, 17 + 18 * x, 66 + 18 * y, null));
            }
        }

        this.addSlotToContainer(new RestrictedSlot(
                this.type == BotaniaMechanicalDeviceType.ELVENTRADE ? RestrictedSlot.PlaceableItemType.ELVENTRADE_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.MANAPOOL ? RestrictedSlot.PlaceableItemType.MANAPOOL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PETAL ? RestrictedSlot.PlaceableItemType.PETAL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PUREDAISY ? RestrictedSlot.PlaceableItemType.PUREDAISY_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.RUNEALTAR ? RestrictedSlot.PlaceableItemType.RUNEALTAR_BLANK_PATTERN
                        : RestrictedSlot.PlaceableItemType.TRASH,
                this.getUpgradeable().getInventoryByName("patterns"),
                0,
                66,
                106,
                this.getInventoryPlayer()
        ));

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, u, 187 + 26, 8 + u * 18, this.getInventoryPlayer())).setStackLimit(1).setNotDraggable());
        }
    }

    @Override
    public IGridNode getNetworkNode() {
        return this.networkNode;
    }

    @Override
    public IItemHandler getInventoryByName(String s) {
        if (s.equals("player")) {
            return new PlayerInvWrapper(this.getInventoryPlayer());
        }

        return this.getUpgradeable().getInventoryByName(s);
    }

    @Override
    public boolean useRealItems() {
        return false;
    }

    @Override
    public ItemStack[] getViewCells() {
        return new ItemStack[0];
    }
}
