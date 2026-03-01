package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.helpers.InventoryAction;
import appeng.helpers.IContainerCraftingPacket;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerMechanicalBotaniaTileBase extends ContainerCrazyAEUpgradeable implements IContainerCraftingPacket {

    protected final TileBotaniaMechanicalMachineBase tile;
    protected final BotaniaMechanicalDeviceType type;

    protected final IGridNode networkNode;

    public ContainerMechanicalBotaniaTileBase(InventoryPlayer ip, TileBotaniaMechanicalMachineBase te) {
        super(ip, te);
        this.tile = te;
        this.type = te.getType();
        this.addMyOffsetX(26);
        this.networkNode = ((IGridHost) te).getGridNode(AEPartLocation.INTERNAL);

        this.initSlots();
    }

    protected abstract void initSlots();

    @Override
    protected int getHeight() {
        return 210;
    }

    @Override
    protected void setupConfig() {}

    public boolean validateRecipe() {
        return this.tile.validateRecipe();
    }

    public void encodePattern() {
        this.tile.encodePattern();
    }

    public TileBotaniaMechanicalMachineBase getTile() {
        return this.tile;
    }

    public void syncClientOnFirstLoad() {
        this.tile.validateRecipe();
    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public int availableUpgrades() {
        return 5;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        this.standardDetectAndSendChanges();
    }

    @Override
    public void doAction(final EntityPlayerMP player, final InventoryAction action, final int slot, final long id) {
        if (action == InventoryAction.PICKUP_OR_SET_DOWN && slot >= 0 && slot < this.inventorySlots.size()) {
            final Slot targetSlot = this.getSlot(slot);
            if (targetSlot instanceof SlotFake) {
                final ItemStack hand = player.inventory.getItemStack();
                if (!hand.isEmpty()) {
                    final ItemStack configuredStack = hand.copy();
                    configuredStack.setCount(1);
                    targetSlot.putStack(configuredStack);
                    return;
                }
            }
        }

        super.doAction(player, action, slot, id);
    }
}
