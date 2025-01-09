package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageMonitorable;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.SlotCraftingTerm;
import appeng.helpers.IContainerCraftingPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotPatternTerm extends SlotCraftingTerm {

    private final int groupNum;
    private final IOptionalSlotHost host;

    public SlotPatternTerm(final EntityPlayer player, final IActionSource mySrc, final IEnergySource energySrc, final IStorageMonitorable storage, final IItemHandler cMatrix, final IItemHandler secondMatrix, final IItemHandler output, final int x, final int y, final IOptionalSlotHost h, final int groupNumber, final IContainerCraftingPacket c) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, output, x, y, c);

        this.host = h;
        this.groupNum = groupNumber;
    }

    @Override
    public ItemStack getStack() {
        if (!this.isSlotEnabled()) {
            if (!this.getDisplayStack().isEmpty()) {
                this.clearStack();
            }
        }

        return super.getStack();
    }

    @Override
    public boolean isSlotEnabled() {
        if (this.host == null) {
            return false;
        }

        return this.host.isSlotEnabled(this.groupNum);
    }
}
