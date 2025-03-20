package dev.beecube31.crazyae2.common.containers;

import appeng.api.networking.IGridNode;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOutput;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileMechanicalTerraplate;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class ContainerTeraplateMechanical extends ContainerMechanicalBotaniaTileBase {
    public ContainerTeraplateMechanical(InventoryPlayer ip, TileMechanicalTerraplate te) {
        super(ip, te);
    }

    @Override
    protected void initSlots() {
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        final IItemHandler input = this.getUpgradeable().getInventoryByName("input");
        final IItemHandler output = this.getUpgradeable().getInventoryByName("output");

        this.addSlotToContainer(new SlotFake(input, 0, 61, 30, true));
        this.addSlotToContainer(new SlotFake(input, 1, 80, 30, true));
        this.addSlotToContainer(new SlotFake(input, 2, 99, 30, true));

        this.addSlotToContainer(new SlotOutput(output, 0, 80, 68, null));

        this.addSlotToContainer(new RestrictedSlot(
                this.type == BotaniaMechanicalDeviceType.ELVENTRADE ? RestrictedSlot.PlaceableItemType.ELVENTRADE_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.MANAPOOL ? RestrictedSlot.PlaceableItemType.MANAPOOL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PETAL ? RestrictedSlot.PlaceableItemType.PETAL_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PUREDAISY ? RestrictedSlot.PlaceableItemType.PUREDAISY_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.RUNEALTAR ? RestrictedSlot.PlaceableItemType.RUNEALTAR_BLANK_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.TERAPLATE ? RestrictedSlot.PlaceableItemType.TERAPLATE_BLANK_PATTERN
                        : RestrictedSlot.PlaceableItemType.NONE,
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
