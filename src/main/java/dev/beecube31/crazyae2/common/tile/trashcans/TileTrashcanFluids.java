package dev.beecube31.crazyae2.common.tile.trashcans;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalFluidInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileTrashcanFluids extends TileTrashcanBase {

    private final CrazyAEBlockUpgradeInv upgrades;
    private final CrazyAEInternalFluidInv fluidInv = new CrazyAEInternalFluidInv(null, 72, 65536);

    public TileTrashcanFluids() {
        super();
        this.upgrades = new CrazyAEBlockUpgradeInv(CrazyAE.definitions().blocks().trashcanItem().maybeBlock().orElse(null), this, 4);
    }

    @Override
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removed, ItemStack added) {
        if (inv == this.upgrades) {
            this.checkUpgrades();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (fluidInv != null) {
            this.fluidInv.writeToNBT(data, "inv");
        }
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("inv")) {
            this.fluidInv.readFromNBT(data, "inv");
        }
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        super.getDrops(w, pos, drops);
        for (int upgradeIndex = 0; upgradeIndex < this.upgrades.getSlots(); upgradeIndex++) {
            final ItemStack stackInSlot = this.upgrades.getStackInSlot(upgradeIndex);

            if (!stackInSlot.isEmpty()) {
                drops.add(stackInSlot);
            }
        }
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    public int getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType u) {
        return this.upgrades.getInstalledUpgrades(u);
    }

    protected void checkUpgrades() {
        switch (this.getInstalledCustomUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            default:
                this.itemsToSendPerTick = 32 * 1024;
                break;
            case 1:
                this.itemsToSendPerTick = 2048 * 1024;
                break;
            case 2:
                this.itemsToSendPerTick = 16384 * 1024;
                break;
            case 3:
                this.itemsToSendPerTick = 65536 * 1024;
                break;
            case 4:
                this.itemsToSendPerTick = 262144 * 1024;
                break;
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        if (Platform.isServer()) {
            this.checkUpgrades();
        }
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().trashcanFluid();
    }

    @Override
    public IItemHandler getInventoryByName(String name) {
        if (name.equals("upgrades")) {
            return this.upgrades;
        }

        return null;
    }

    @Override
    public @NotNull IItemHandler getInternalInventory() {
        return this.upgrades;
    }

    public IAEFluidTank getConfig() {
        return this.fluidInv;
    }

    @Override
    public @NotNull TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall) {
        if (!this.getProxy().isActive()) {
            return TickRateModulation.IDLE;
        }

        boolean worked = false;
        try {
            final IFluidStorageChannel channel = AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
            final IMEMonitor<IAEFluidStack> inv = this.getProxy().getStorage().getInventory(channel);
            
            for (IAEFluidStack filterStack : this.fluidInv) {
                if (filterStack == null) {
                    continue;
                }
                
                final IAEFluidStack toExtract = filterStack.copy();
                toExtract.setStackSize(this.itemsToSendPerTick);
                
                final IAEFluidStack simulated = inv.extractItems(toExtract, Actionable.SIMULATE, this.src);
                
                if (simulated != null && simulated.getStackSize() > 0) {
                    final IAEFluidStack modulated = inv.extractItems(simulated, Actionable.MODULATE, this.src);
                    
                    if (modulated != null && modulated.getStackSize() > 0) {
                        worked = true;
                    }
                }
            }
        } catch (GridAccessException e) {
            return TickRateModulation.IDLE;
        }

        return worked ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }
}
