package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.util.*;
import appeng.me.GridAccessException;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.implementations.CraftingCPUCalculator;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import appeng.util.inv.WrapperChainedItemHandler;
import dev.beecube31.crazyae2.core.CrazyAE;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

public class TileCraftingUnitsCombiner extends TileCraftingTile implements IConfigManagerHost, IUpgradeableHost, IAEAppEngInventory {

    private CraftingCPUCluster cluster;
    private CraftingCPUCalculator calc = new CraftingCPUCalculator(this);

    private final AppEngInternalInventory acceleratorsInv = new
            AppEngInternalInventory(this, 12, 64);
    private final AppEngInternalInventory storageInv = new
            AppEngInternalInventory(this, 12, 64);

    private final IItemHandler combinedInv = new WrapperChainedItemHandler(this.acceleratorsInv, this.storageInv);

    private long storageAmt = 0;
    private int acceleratorAmt = 0;

    private int storageItemsAmt = 0;
    private int acceleratorItemsAmt = 0;

    private boolean isPowered = false;


    public TileCraftingUnitsCombiner() {
        this.getProxy().setIdlePowerUsage(16.0);
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setVisualRepresentation(CrazyAE.definitions().blocks()
                .craftingUnitsCombiner().maybeStack(1).orElse(ItemStack.EMPTY));
    }

    @Override
    public void setName(final String name) {
        super.setName(name);
        if (this.cluster != null) {
            this.cluster.updateName();
        }
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    @Override
    public IAECluster getCluster() {
        return this.cluster;
    }

    @Override
    public void disconnect(final boolean update) {
        if (this.cluster != null) {
            this.cluster.destroy();
        }
    }

    public void updateStatus(final CraftingCPUCluster c) {
        if (this.cluster != null && this.cluster != c) {
            this.cluster.breakCluster();
        }

        this.cluster = c;
    }

    private void init() {
        this.calculateCrafting();
        this.updateMultiBlock();
        try {
            this.getProxy().getGrid().postEvent(new MENetworkCraftingCpuChange(this.getProxy().getNode()));
        } catch (GridAccessException e) {
            // :(
        }
    }

    @Override
    public void onReady() {
        this.getProxy().onReady();
        this.init();
    }


    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public void updateMultiBlock() {
        if (Platform.isClient()) return;
        if (this.calc == null) this.calc = new CraftingCPUCalculator(this);

        IAECluster c = this.calc.createCluster(world, this.getLocation(), this.getLocation());

        boolean updateGrid = false;
        final IAECluster cluster = this.cluster;
        if (cluster == null) {
            this.calc.updateTiles(c, world, this.getLocation(), this.getLocation());
            updateGrid = true;
        } else {
            c = cluster;
        }

        c.updateStatus(updateGrid);
    }

    public boolean isAccelerator() {
        return false;
    }

    public boolean isStorage() {
        return false;
    }

    public long getStorageAmt() {
        return this.storageAmt;
    }

    public int getAcceleratorAmt() {
        return this.acceleratorAmt;
    }

    public boolean isCoreBlock() {
        return true;
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        final boolean oldPower = this.isPowered;
        this.isPowered = data.readBoolean();
        return this.isPowered != oldPower || c;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeBoolean(this.isPowered);
    }

    public boolean isFormed() {
        return this.cluster != null;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("core", this.isCoreBlock());
        if (this.isCoreBlock() && this.cluster != null) {
            this.cluster.writeToNBT(data);
        }
        this.acceleratorsInv.writeToNBT(data, "acceleratorsInv");
        this.storageInv.writeToNBT(data, "storageInv");
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.setCoreBlock(data.getBoolean("core"));
        if (this.isCoreBlock()) {
            if (this.cluster != null) {
                this.cluster.readFromNBT(data);
            } else {
                this.setPreviousState(data.copy());
            }
        }
        this.acceleratorsInv.readFromNBT(data, "acceleratorsInv");
        this.storageInv.readFromNBT(data, "storageInv");
       // this.init();
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.COVERED;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "ainv" -> this.acceleratorsInv;
            case "sinv" -> this.storageInv;
            default -> null;
        };
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {}

    public IItemHandler getAcceleratorsInv() {
        return this.acceleratorsInv;
    }

    public IItemHandler getStorageInv() {
        return this.storageInv;
    }

    @Override
    public boolean isActive() {
        if (Platform.isServer()) {
            try {
                return this.getProxy().getEnergy().isNetworkPowered();
            } catch (GridAccessException e) {
                return false;
            }
        }
        return this.isPowered;
    }

    public void removeCluster() {
        this.cluster.cancel();
        this.calc.disconnect();
        this.breakCluster();
    }


    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (this.getProxy().isReady() && (!removed.isEmpty() || !added.isEmpty())) {
            this.removeCluster();
            this.calculateCrafting();
            this.updateMultiBlock();
            try {
                this.getProxy().getGrid().postEvent(new MENetworkCraftingCpuChange(this.getProxy().getNode()));
            } catch (GridAccessException e) {
                // :(
            }
        }
    }

    private void calculateCrafting() {
        this.storageAmt = 0;
        this.acceleratorAmt = 0;
        this.storageItemsAmt = 0;
        this.acceleratorItemsAmt = 0;

        for (ItemStack item : this.storageInv) {
            if (item != null) {
                this.storageItemsAmt += item.getCount();
                for (int i = 0; i < item.getCount(); i++) {
                    this.storageAmt += AEApi.instance().definitions().blocks().craftingStorage1k().isSameAs(item) ? 1024
                            : AEApi.instance().definitions().blocks().craftingStorage4k().isSameAs(item) ? 1024 * 4
                            : AEApi.instance().definitions().blocks().craftingStorage16k().isSameAs(item) ? 1024 * 16
                            : AEApi.instance().definitions().blocks().craftingStorage64k().isSameAs(item) ? 1024 * 64
                            : CrazyAE.definitions().blocks().craftingStorage256k().isSameAs(item) ? 1024 * 256
                            : CrazyAE.definitions().blocks().craftingStorage1mb().isSameAs(item) ? 1024 * 1024
                            : CrazyAE.definitions().blocks().craftingStorage4mb().isSameAs(item) ? 1024 * 4096
                            : CrazyAE.definitions().blocks().craftingStorage16mb().isSameAs(item) ? 1024 * 16384
                            : CrazyAE.definitions().blocks().craftingStorage64mb().isSameAs(item) ? 1024 * 65536
                            : CrazyAE.definitions().blocks().craftingStorage256mb().isSameAs(item) ? 1024 * 262144
                            : CrazyAE.definitions().blocks().craftingStorage1gb().isSameAs(item) ? 1024 * 1048576
                            : CrazyAE.definitions().blocks().craftingStorage2gb().isSameAs(item) ? Integer.MAX_VALUE
                            : 0;
                }
            }
        }

        for (ItemStack item : this.acceleratorsInv) {
            if (item != null) {
                this.acceleratorItemsAmt += item.getCount();
                for (int i = 0; i < item.getCount(); i++) {
                    this.acceleratorAmt += AEApi.instance().definitions().blocks().craftingAccelerator().isSameAs(item) ? 1
                            : CrazyAE.definitions().blocks().coprocessor4x().isSameAs(item) ? 4
                            : CrazyAE.definitions().blocks().coprocessor16x().isSameAs(item) ? 16
                            : CrazyAE.definitions().blocks().coprocessor64x().isSameAs(item) ? 64
                            : CrazyAE.definitions().blocks().coprocessor256x().isSameAs(item) ? 256
                            : CrazyAE.definitions().blocks().coprocessor1024x().isSameAs(item) ? 1024
                            : CrazyAE.definitions().blocks().coprocessor4096x().isSameAs(item) ? 4096
                            : CrazyAE.definitions().blocks().coprocessor16384x().isSameAs(item) ? 16384
                            : CrazyAE.definitions().blocks().coprocessor65536x().isSameAs(item) ? 65536
                            : 0;
                }
            }
        }

        this.getProxy().setIdlePowerUsage((storageItemsAmt + acceleratorItemsAmt) * 2);
    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {
        super.getDrops(w, pos, drops);
        for (final ItemStack is : this.acceleratorsInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }

        for (final ItemStack is : this.storageInv) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Override
    public int getInstalledUpgrades(Upgrades upgrades) {
        return 0;
    }

    @Override
    public IConfigManager getConfigManager() {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.combinedInv;
        }
        return super.getCapability(capability, facing);
    }
}
