package dev.beecube31.crazyae2.common.tile.solars;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkTile;
import appeng.util.SettingsFrom;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

public abstract class TileEnergyPanelBase extends AENetworkTile implements IGridTickable, IAEPowerStorage {
    public double internalCurrentEnergy;
    private boolean isVisible;

    public TileEnergyPanelBase() {
        this.getProxy().setIdlePowerUsage(0);
        this.getProxy().setFlags();
    }

    protected abstract double getPowerPerTick();

    protected abstract double getPowerPerTickAtNight();

    protected abstract double getCapacity();

    @Override
    public double injectAEPower(double v, @NotNull Actionable actionable) {
        return 0;
    }

    @Override
    public double getAEMaxPower() {
        return this.getCapacity();
    }

    @Override
    public double getAECurrentPower() {
        return this.internalCurrentEnergy;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @NotNull
    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ;
    }

    @Override
    public double extractAEPower(double v, @NotNull Actionable mode, @NotNull PowerMultiplier pm) {
        return pm.divide(this.extractAEPower(pm.multiply(v), mode));
    }

    public double extractAEPower(double v, @NotNull Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            return Math.min(this.internalCurrentEnergy, v);
        }

        final boolean wasFull = this.internalCurrentEnergy >= this.getCapacity() - 0.001;

        if (wasFull && v > 0) {
            try {
                this.getProxy().getGrid().postEvent(new MENetworkPowerStorage(this, MENetworkPowerStorage.PowerEventType.REQUEST_POWER));
            } catch (final GridAccessException ignored) {

            }
        }

        if (this.internalCurrentEnergy > v) {
            this.internalCurrentEnergy -= v;
            return v;
        }
        v = this.internalCurrentEnergy;
        this.internalCurrentEnergy = 0;

        return v;
    }

    @Override
    public void uploadSettings(final SettingsFrom from, final NBTTagCompound compound, EntityPlayer player) {
        if (from == SettingsFrom.DISMANTLE_ITEM) {
            this.internalCurrentEnergy = compound.getDouble("internalCurrentEnergy");
        }
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }


    @Override
    public NBTTagCompound downloadSettings(final SettingsFrom from) {
        if (from == SettingsFrom.DISMANTLE_ITEM) {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble("internalCurrentEnergy", this.internalCurrentEnergy);
            tag.setDouble("internalMaxPower", this.getCapacity()); // used for tool tip.
            return tag;
        }
        return null;
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.COVERED;
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        super.readFromStream(data);
        return true;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("internalCurrentEnergy", this.internalCurrentEnergy);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.internalCurrentEnergy = data.getDouble("internalCurrentEnergy");
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updateVisibility();
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (this.getWorld().provider.getWorldTime() % 100 == 0) {
            this.updateVisibility();
        }

        if (this.isVisible) this.makeEnergy();
        this.sendEnergy();

        return TickRateModulation.SAME;
    }

    private void updateVisibility() {
        this.isVisible = !this.world.provider.isNether() && this.world.canBlockSeeSky(this.pos.up()) && this.world
                .getBlockState(this.pos.up()).getMaterial().getMaterialMapColor() == MapColor.AIR;
    }

    public void makeEnergy() {
        if (this.world.isDaytime()) {
            this.internalCurrentEnergy += Math.min(this.getPowerPerTick(), this.getCapacity() - this.internalCurrentEnergy);
        } else {
            this.internalCurrentEnergy += Math.min(this.getPowerPerTickAtNight(), this.getCapacity() - this.internalCurrentEnergy);
        }
    }

    public void sendEnergy() {
        try {
            final IEnergyGrid grid = this.getProxy().getEnergy();
            final double overFlow = grid.injectPower(this.internalCurrentEnergy, Actionable.SIMULATE);

            grid.injectPower(Math.max(0.0, this.internalCurrentEnergy - overFlow), Actionable.MODULATE);
            this.internalCurrentEnergy = overFlow;
        } catch (final GridAccessException e) {
            // :(
        }
    }
}
