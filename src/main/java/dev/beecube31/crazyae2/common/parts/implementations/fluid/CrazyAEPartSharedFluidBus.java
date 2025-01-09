package dev.beecube31.crazyae2.common.parts.implementations.fluid;

import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Upgrades;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.util.AECableType;
import appeng.fluids.helper.IConfigurableFluidInventory;
import appeng.fluids.util.AEFluidInventory;
import appeng.fluids.util.IAEFluidTank;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEPartUpgradeable;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class CrazyAEPartSharedFluidBus extends CrazyAEPartUpgradeable implements IGridTickable, IConfigurableFluidInventory {

    private final AEFluidInventory config = new AEFluidInventory(null, 9);
    private boolean lastRedstone;

    public CrazyAEPartSharedFluidBus(ItemStack is) {
        super(is);
    }

    @Override
    public void upgradesChanged() {
        this.updateState();
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

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (Platform.isServer()) {
            CrazyAEGuiHandler.openGUI(player, this.getHost().getTile(), this.getSide(), CrazyAEGuiBridge.IMPROVED_FLUID_BUSES);
        }

        return true;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(6, 6, 11, 10, 10, 13);
        bch.addBox(5, 5, 13, 11, 11, 14);
        bch.addBox(4, 4, 14, 12, 12, 16);
    }

    protected TileEntity getConnectedTE() {
        TileEntity self = this.getHost().getTile();
        return this.getTileEntity(self, self.getPos().offset(this.getSide().getFacing()));
    }

    private TileEntity getTileEntity(final TileEntity self, final BlockPos pos) {
        final World w = self.getWorld();

        if (w.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4) != null) {
            return w.getTileEntity(pos);
        }

        return null;
    }

    protected int calculateFluidsToSend() {
        int fluidMB = this.getChannel().transferFactor() * 8;

        switch (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.STACKS)) {
            case 1 -> fluidMB += 4000 * 8;
            case 2 -> fluidMB += 8000 * 16;
            case 3 -> fluidMB += 16000 * 24;
            case 4 -> fluidMB += 32000 * 32;
        }

        if (this.getInstalledUpgrades(dev.beecube31.crazyae2.common.registration.definitions.Upgrades.UpgradeType.ADVANCED_SPEED) > 0) {
            fluidMB += 192000;
        }

        return fluidMB;
    }

    @Override
    public void readFromNBT(NBTTagCompound extra) {
        super.readFromNBT(extra);
        this.config.readFromNBT(extra, "config");
    }

    @Override
    public void writeToNBT(NBTTagCompound extra) {
        super.writeToNBT(extra);
        this.config.writeToNBT(extra, "config");
    }

    public IAEFluidTank getConfig() {
        return this.config;
    }

    @Override
    public IFluidHandler getFluidInventoryByName(final String name) {
        if (name.equals("config")) {
            return this.config;
        }
        return null;
    }

    protected IFluidStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 5;
    }

    protected abstract TickRateModulation doBusWork();

    protected abstract boolean canDoBusWork();
}
