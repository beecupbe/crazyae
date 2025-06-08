package dev.beecube31.crazyae2.common.tile.trashcans;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.AEUtils;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileTrashcanMana extends TileTrashcanBase {

    private final CrazyAEBlockUpgradeInv upgrades;

    public TileTrashcanMana() {
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
        return CrazyAE.definitions().blocks().trashcanMana();
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

    @Override
    public @NotNull TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall)  {
        if (!this.getProxy().isActive()) {
            return TickRateModulation.IDLE;
        }

        try {
            final IMEMonitor<IAEItemStack> inv = this.getProxy().getStorage().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            AEUtils.extractFromME(inv, AEUtils.createAEStackFromDefinition(CrazyAE.definitions().items().manaAsAEStack(), this.itemsToSendPerTick), this.src, Actionable.MODULATE);
            return TickRateModulation.FASTER;

        } catch (Throwable ignored) {
            return TickRateModulation.IDLE;
        }
    }
}
