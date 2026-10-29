package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.config.CondenserOutput;
import appeng.api.config.Settings;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.OutputSlot;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ContainerImprovedCondenser extends CrazyAEBaseContainer implements IProgressProvider {
    public static final SlotSemantic[] INPUT_ROWS = new SlotSemantic[5];

    static {
        for (int i = 0; i < INPUT_ROWS.length; i++) {
            INPUT_ROWS[i] = SlotSemantics.register("CRAZYAE_CONDENSER_INPUT_" + i, false);
        }
    }

    private final TileImprovedCondenser condenser;
    private final RestrictedSlot componentInputSlot;

    @GuiSync(0)
    public long requiredEnergy = 0;
    @GuiSync(1)
    public long storedPower = 0;
    @GuiSync(2)
    public CondenserOutput output = CondenserOutput.TRASH;

    public ContainerImprovedCondenser(MenuType<ContainerImprovedCondenser> type, int id, Inventory ip,
                                      TileImprovedCondenser condenser) {
        super(type, id, ip, condenser);
        this.condenser = condenser;

        var inv = condenser.getInternalInventory();
        int slot = 0;
        for (int i = 0; i < TileImprovedCondenser.INPUT_SLOTS; i++) {
            this.addSlot(new RestrictedSlot(RestrictedSlot.PlaceableItemType.TRASH, inv, slot++),
                    INPUT_ROWS[i / 5]);
        }
        for (int i = 0; i < TileImprovedCondenser.OUTPUT_SLOTS; i++) {
            this.addSlot(new OutputSlot(inv, slot++, null), SlotSemantics.MACHINE_OUTPUT);
        }

        this.componentInputSlot = new RestrictedSlot(RestrictedSlot.PlaceableItemType.STORAGE_COMPONENT, inv, slot);

        this.addSlot(this.componentInputSlot.setStackLimit(1), SlotSemantics.STORAGE_CELL);

        this.createPlayerInventorySlots(ip);
    }

    @Override
    protected int findBestSlotLocation(ItemStack stack) {
        if (RestrictedSlot.PlaceableItemType.STORAGE_COMPONENT.associatedFilter
                .allowInsert(null, -1, stack)) {
            return this.componentInputSlot.getSlotIndex();
        }

        return -1;
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            double maxStorage = this.condenser.getStorage();
            double required = this.condenser.getRequiredPower();
            double max = required == 0 ? maxStorage : Math.min(required, maxStorage);
            double current = Math.min(this.condenser.getStoredPower(), max);

            if (max > Integer.MAX_VALUE) {
                current = current / max * Integer.MAX_VALUE;
                max = Integer.MAX_VALUE;
            }
            this.requiredEnergy = (long) max;
            this.storedPower = (long) current;
            this.output = this.condenser.getConfigManager().getSetting(Settings.CONDENSER_OUTPUT);
        }
        super.broadcastChanges();
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
}
