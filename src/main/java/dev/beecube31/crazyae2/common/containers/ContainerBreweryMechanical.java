package dev.beecube31.crazyae2.common.containers;

import appeng.api.networking.IGridNode;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOutput;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalBrewery;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class ContainerBreweryMechanical extends ContainerMechanicalBotaniaTileBase {
    public ContainerBreweryMechanical(InventoryPlayer ip, TileMechanicalBrewery te) {
        super(ip, te);
    }

    @Override
    protected void initSlots() {
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        final IItemHandler input = this.getUpgradeable().getInventoryByName("input");
        final IItemHandler bottle = this.getUpgradeable().getInventoryByName("bottle");
        final IItemHandler output = this.getUpgradeable().getInventoryByName("output");

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlotToContainer(new SlotFake(input, x + y * 3, 62 + x * 18, 16 + y * 18, true));
            }
        }

        this.addSlotToContainer(new SlotFake(bottle, 0, 80, 53, true).setIIcon(StateSprite.BREW));
        this.addSlotToContainer(new SlotOutput(output, 0, 80, 87, null));

        this.addSlotToContainer(new RestrictedSlot(
                this.type == BotaniaMechanicalDeviceType.ELVENTRADE ? RestrictedSlot.PlaceableItemType.ELVENTRADE_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.MANAPOOL ? RestrictedSlot.PlaceableItemType.MANAPOOL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PETAL ? RestrictedSlot.PlaceableItemType.PETAL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PUREDAISY ? RestrictedSlot.PlaceableItemType.PUREDAISY_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.RUNEALTAR ? RestrictedSlot.PlaceableItemType.RUNEALTAR_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.TERAPLATE ? RestrictedSlot.PlaceableItemType.TERAPLATE_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.BREWERY ? RestrictedSlot.PlaceableItemType.BREWERY_BLANK_PATTERN
                        : RestrictedSlot.PlaceableItemType.TRASH,
                this.getUpgradeable().getInventoryByName("patterns"),
                0,
                66,
                108,
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
