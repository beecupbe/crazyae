package dev.beecube31.crazyae2.common.tile.trashcans;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import com.google.common.collect.ImmutableList;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.AEUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalAEInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileTrashcanItems extends TileTrashcanBase {

    private final CrazyAEBlockUpgradeInv upgrades;
    private final CrazyAEInternalAEInv inv;

    public TileTrashcanItems() {
        super();
        this.upgrades = new CrazyAEBlockUpgradeInv(CrazyAE.definitions().blocks().trashcanItem().maybeBlock().orElse(null), this, 4);
        this.inv = new CrazyAEInternalAEInv(this, 72, 1);
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
        if (inv != null) {
            this.inv.writeToNBT(data, "inv");
        }
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        if (inv != null) {
            this.inv.readFromNBT(data, "inv");
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
                this.itemsToSendPerTick = 32;
                break;
            case 1:
                this.itemsToSendPerTick = 2048;
                break;
            case 2:
                this.itemsToSendPerTick = 16384;
                break;
            case 3:
                this.itemsToSendPerTick = 65536;
                break;
            case 4:
                this.itemsToSendPerTick = 262144;
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
        return CrazyAE.definitions().blocks().trashcanItem();
    }

    @Override
    public IItemHandler getInventoryByName(String name) {
        switch (name) {
            case "inv" -> {
                return this.inv;
            }

            case "upgrades" -> {
                return this.upgrades;
            }
        }

        return null;
    }

    @Override
    public @NotNull IItemHandler getInternalInventory() {
        return this.inv;
    }


    @Override
    public @NotNull TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall)  {
        if (!this.getProxy().isActive()) {
            return TickRateModulation.IDLE;
        }

        boolean worked = false;
        try {
            final FuzzyMode fzMode = (FuzzyMode) this.getConfigManager().getSetting(Settings.FUZZY_MODE);
            final IMEMonitor<IAEItemStack> inv = this.getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            for (IAEItemStack candidate : this.inv) {
                if (candidate == null) {
                    continue;
                }

                if (this.getInstalledUpgrades(Upgrades.FUZZY) > 0) {
                    for (final IAEItemStack o : ImmutableList.copyOf(inv.getStorageList().findFuzzy(candidate, fzMode))) {
                        if (o.getStackSize() > 0) {
                            AEUtils.extractFromME(inv, o.setStackSize(this.itemsToSendPerTick), this.src, Actionable.MODULATE);
                            worked = true;

                        }
                    }
                } else {
                    final IAEItemStack o = inv.getStorageList().findPrecise(candidate);
                    if (o != null && o.getStackSize() > 0) {
                        AEUtils.extractFromME(inv, o.setStackSize(this.itemsToSendPerTick), this.src, Actionable.MODULATE);
                        worked = true;
                    }
                }
            }

        } catch (Throwable ignored) {
            return TickRateModulation.IDLE;
        }

        return worked ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }
}
