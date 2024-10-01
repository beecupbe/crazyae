package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.interfaces.IProgressProvider;
import appeng.container.slot.SlotOutput;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.misc.TileCondenser;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerImprovedCondenser extends AEBaseContainer implements IProgressProvider {

    private final TileImprovedCondenser condenser;
    @GuiSync(0)
    public long requiredEnergy = 0;
    @GuiSync(1)
    public long storedPower = 0;
    @GuiSync(2)
    public CondenserOutput output = CondenserOutput.TRASH;
    private final ItemStack prevStack = ItemStack.EMPTY;

    public ContainerImprovedCondenser(final InventoryPlayer ip, final TileImprovedCondenser condenser) {
        super(ip, condenser, null);
        this.condenser = condenser;

        IItemHandler inv = condenser.getInternalInventory();

        for (int x = 0; x < 5; x++) {
            for (int j = 0; j < 5; j++) {
                this.addSlotToContainer(new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.TRASH, inv, j + x * 5, 8 + x * 18, 13 + j * 18, ip));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int y = 0; y < 2; y++) {
                this.addSlotToContainer(new SlotOutput(inv, (y + i * 2) + 25, 140 + i * 18, 40 + y * 18, -1));
            }
        }
        this.addSlotToContainer(
                (new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.STORAGE_COMPONENT, inv, 30, 134, 13, ip)).setStackLimit(1));

        this.bindPlayerInventory(ip, 0, 197 - /* height of player inventory */82);
    }

    @Override
    public void detectAndSendChanges() {
        final ItemStack is = this.condenser.getInternalInventory().getStackInSlot(30);
        if (Platform.isServer()) {
            final double maxStorage = this.condenser.getStorage();
            final double requiredEnergy = this.condenser.getRequiredPower();

            this.requiredEnergy = requiredEnergy == 0 ? (int) maxStorage : (int) Math.min(requiredEnergy, maxStorage);
            this.storedPower = (int) this.condenser.getStoredPower();
            this.setOutput((CondenserOutput) this.condenser.getConfigManager().getSetting(Settings.CONDENSER_OUTPUT));

            for (final IContainerListener listener : this.listeners) {
                if (!ItemStack.areItemsEqual(is, prevStack)) {
                    listener.sendSlotContents(this, 1, is);
                }
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return (int) this.storedPower;
    }

    @Override
    public int getMaxProgress() {
        return (int) this.requiredEnergy;
    }

    public CondenserOutput getOutput() {
        return this.output;
    }

    private void setOutput(final CondenserOutput output) {
        this.output = output;
    }
}
