package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOutput;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.gui.ICrazyAEProgressProvider;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerImprovedCondenser extends CrazyAEBaseContainer implements ICrazyAEProgressProvider {

    private final TileImprovedCondenser condenser;
    @GuiSync(10)
    public long requiredEnergy = 0;
    @GuiSync(11)
    public long storedPower = 0;
    @GuiSync(12)
    public CondenserOutput output = CondenserOutput.TRASH;
    private final ItemStack prevStack = ItemStack.EMPTY;
    private final IItemHandler inv;

    public ContainerImprovedCondenser(final InventoryPlayer ip, final TileImprovedCondenser condenser) {
        super(ip, condenser, null);
        this.condenser = condenser;

        this.inv = condenser.getInternalInventory();

        for (int x = 0; x < 5; x++) {
            for (int j = 0; j < 5; j++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.TRASH, inv, j + x * 5, 8 + x * 18, 13 + j * 18, ip));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int y = 0; y < 2; y++) {
                this.addSlotToContainer(new SlotOutput(inv, (y + i * 2) + 25, 134 + i * 18, 40 + y * 18, null));
            }
        }
        this.addSlotToContainer(
                (new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_COMPONENT, inv, 29, 134, 13, ip)).setStackLimit(1));

        this.bindPlayerInventory(ip, 0, 197 - /* height of player inventory */82);
    }

    @Override
    public void detectAndSendChanges() {
        if (Platform.isServer()) {
            final double maxStorage = this.condenser.getStorage();
            final double requiredEnergy = this.condenser.getRequiredPower();

            this.requiredEnergy = requiredEnergy == 0 ? (int) maxStorage : (int) Math.min(requiredEnergy, maxStorage);
            this.storedPower = (int) this.condenser.getStoredPower();
            this.setOutput((CondenserOutput) this.condenser.getConfigManager().getSetting(Settings.CONDENSER_OUTPUT));

            for (final IContainerListener listener : this.listeners) {
                for (int i = 25; i < 29; i++) {
                    ItemStack is = inv.getStackInSlot(i);
                    if (!ItemStack.areItemsEqual(is, prevStack)) {
                        listener.sendSlotContents(this, i - 25, is);
                    }
                }
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public double getCurrentProgress() {
        return (int) this.storedPower;
    }

    @Override
    public double getMaxProgress() {
        return this.requiredEnergy;
    }

    public CondenserOutput getOutput() {
        return this.output;
    }

    private void setOutput(final CondenserOutput output) {
        this.output = output;
    }

    @Override
    public String getTooltip(String title, boolean disableMaxProgress, int tooltipID) {
        return title +
                "\n" +
                this.getCurrentProgress(tooltipID) +
                ' ' +
                GuiText.Of.getLocal() +
                ' ' +
                this.getMaxProgress(tooltipID);
    }
}
