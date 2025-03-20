package dev.beecube31.crazyae2.common.parts.implementations;

import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Upgrades;
import appeng.api.networking.GridFlags;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.InventoryAdaptor;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public abstract class CrazyAEPartSharedBus extends CrazyAEPartUpgradeable implements IGridTickable {

    private final AppEngInternalAEInventory config = new AppEngInternalAEInventory(this, 9);
    private boolean lastRedstone = false;

    public CrazyAEPartSharedBus(final ItemStack is) {
        super(is);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public void upgradesChanged() {
        this.updateState();
    }

    @Override
    public void readFromNBT(final net.minecraft.nbt.NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.getConfig().readFromNBT(extra, "config");
    }

    @Override
    public void writeToNBT(final net.minecraft.nbt.NBTTagCompound extra) {
        super.writeToNBT(extra);
        this.getConfig().writeToNBT(extra, "config");
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        if (name.equals("config")) {
            return this.getConfig();
        }

        return super.getInventoryByName(name);
    }

    @Override
    public void onNeighborChanged(IBlockAccess w, BlockPos pos, BlockPos neighbor) {
        this.updateState();
        if (this.lastRedstone != this.getHost().hasRedstone(this.getSide())) {
            this.lastRedstone = !this.lastRedstone;
            if (this.lastRedstone && this.getRSMode() == RedstoneMode.SIGNAL_PULSE) {
                this.doBusWork();
            }
        }
    }

    protected InventoryAdaptor getHandler() {
        final TileEntity self = this.getHost().getTile();
        final TileEntity target = this.getTileEntity(self, self.getPos().offset(this.getSide().getFacing()));

        return InventoryAdaptor.getAdaptor(target, this.getSide().getFacing().getOpposite());
    }

    protected TileEntity getVictim() {
        final TileEntity self = this.getHost().getTile();

        return this.getTileEntity(self, self.getPos().offset(this.getSide().getFacing()));
    }

    protected IMEMonitor<IAEItemStack> getEnergyInv() {
        try {
            return this.getProxy()
                    .getStorage()
                    .getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class));

        } catch (GridAccessException ignored) {}

        return null;
    }

    protected IMEMonitor<IAEItemStack> getItemsInv() {
        try {
            return this.getProxy()
                    .getStorage()
                    .getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

        } catch (GridAccessException ignored) {}

        return null;
    }

    private TileEntity getTileEntity(final TileEntity self, final BlockPos pos) {
        final World w = self.getWorld();

        if (w.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null) {
            return w.getTileEntity(pos);
        }

        return null;
    }

    protected int availableSlots() {
        return Math.min(1 + this.getInstalledUpgrades(Upgrades.CAPACITY) * 4, this.getConfig().getSlots());
    }

    protected int calculateItemsToSend() {
        int items = 256;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> items += 8 * 96;
            case 2 -> items += 8 * 192;
            case 3 -> items += 8 * 384;
            case 4 -> items += 8 * 512;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            items += 4096;
        }

        return items;
    }

    protected int calculateEnergyToSend() {
        int energy = 32;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> energy = 2048;
            case 2 -> energy = 131072;
            case 3 -> energy = 8388608;
            case 4 -> energy = Integer.MAX_VALUE;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            energy += 32768;
        }

        return energy;
    }

    protected int calculateEFEnergyToSend() {
        int energy = 32;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> energy = 2048;
            case 2 -> energy = 32768;
            case 3 -> energy = 524288;
            case 4 -> energy = 33554432;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            energy += 32768;
        }

        return energy;
    }

    protected int calculateManaIterations() {
        int mana = 1;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> mana += 1;
            case 2 -> mana += 2;
            case 3 -> mana += 4;
            case 4 -> mana += 6;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            mana += 1;
        }

        return mana;
    }

    protected int calculateManaToSend() {
        int mana = 256;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> mana += 256 * 64;
            case 2 -> mana += 256 * 512;
            case 3 -> mana += 256 * 2048;
            case 4 -> mana += 256 * 8192;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            mana += 65536 * 2;
        }

        return mana;
    }
    /**
     * Checks if the bus can actually do something.
     * <p>
     * Currently this tests if the chunk for the target is actually loaded.
     *
     * @return true, if the the bus should do its work.
     */
    protected boolean canDoBusWork() {
        final TileEntity self = this.getHost().getTile();
        final BlockPos selfPos = self.getPos().offset(this.getSide().getFacing());
        final int xCoordinate = selfPos.getX();
        final int zCoordinate = selfPos.getZ();
        final World world = self.getWorld();

        return world != null && world.getChunkProvider().getLoadedChunk(xCoordinate >> 4, zCoordinate >> 4) != null;
    }

    private void updateState() {
        try {
            if (!this.isSleeping()) {
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
            } else {
                this.getProxy().getTick().sleepDevice(this.getProxy().getNode());
            }
        } catch (final GridAccessException e) {
            // :P
        }
    }

    protected abstract TickRateModulation doBusWork();

    AppEngInternalAEInventory getConfig() {
        return this.config;
    }
}
