package dev.beecube31.crazyae2.common.tile.energycells;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.block.networking.BlockEnergyCell;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkTile;
import appeng.util.SettingsFrom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileBigEnergyCells extends AENetworkTile implements IAEPowerStorage {
    private double internalCurrentPower = 0.0;
    private final double internalMaxPower;
    private byte currentMeta = -1;

    public TileBigEnergyCells(double maxPower) {
        this.internalMaxPower = maxPower;
        this.getProxy().setIdlePowerUsage(0.0);
    }

    public AECableType getCableConnectionType(AEPartLocation dir) {
        return AECableType.COVERED;
    }

    public void onReady() {
        super.onReady();
        int value = this.world.getBlockState(this.pos).getValue(BlockEnergyCell.ENERGY_STORAGE);
        this.currentMeta = (byte)value;
        this.changePowerLevel();
    }

    public static int getStorageLevelFromFillFactor(double fillFactor) {
        byte boundMetadata = (byte)((int)(8.0 * fillFactor));
        if (boundMetadata > 7) {
            boundMetadata = 7;
        }

        if (boundMetadata < 0) {
            boundMetadata = 0;
        }

        return boundMetadata;
    }

    private void changePowerLevel() {
        if (!this.notLoaded() && !this.isInvalid()) {
            int storageLevel = getStorageLevelFromFillFactor(this.internalCurrentPower / this.getInternalMaxPower());
            if (this.currentMeta != storageLevel) {
                this.currentMeta = (byte)storageLevel;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockEnergyCell.ENERGY_STORAGE, storageLevel));
            }

        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("internalCurrentPower", this.internalCurrentPower);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.internalCurrentPower = data.getDouble("internalCurrentPower");
    }

    public boolean canBeRotated() {
        return false;
    }

    public void uploadSettings(SettingsFrom from, NBTTagCompound compound, EntityPlayer player) {
        if (from == SettingsFrom.DISMANTLE_ITEM) {
            this.internalCurrentPower = compound.getDouble("internalCurrentPower");
        }

    }

    public NBTTagCompound downloadSettings(SettingsFrom from) {
        if (from == SettingsFrom.DISMANTLE_ITEM) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble("internalCurrentPower", this.internalCurrentPower);
            tag.setDouble("internalMaxPower", this.getInternalMaxPower());
            return tag;
        } else {
            return null;
        }
    }

    public final double injectAEPower(double amt, Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            double fakeBattery = this.internalCurrentPower + amt;
            return fakeBattery > this.getInternalMaxPower() ? fakeBattery - this.getInternalMaxPower() : 0.0;
        } else {
            if (this.internalCurrentPower < 0.01 && amt > 0.0) {
                this.getProxy().getNode().getGrid().postEvent(new MENetworkPowerStorage(this, MENetworkPowerStorage.PowerEventType.PROVIDE_POWER));
            }

            this.internalCurrentPower += amt;
            if (this.internalCurrentPower > this.getInternalMaxPower()) {
                amt = this.internalCurrentPower - this.getInternalMaxPower();
                this.internalCurrentPower = this.getInternalMaxPower();
                this.changePowerLevel();
                return amt;
            } else {
                this.changePowerLevel();
                return 0.0;
            }
        }
    }

    public double getAEMaxPower() {
        return this.getInternalMaxPower();
    }

    public double getAECurrentPower() {
        return this.internalCurrentPower;
    }

    public boolean isAEPublicPowerStorage() {
        return true;
    }

    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    public final double extractAEPower(double amt, Actionable mode, PowerMultiplier pm) {
        return pm.divide(this.extractAEPower(pm.multiply(amt), mode));
    }

    private double extractAEPower(double amt, Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            return this.internalCurrentPower > amt ? amt : this.internalCurrentPower;
        } else {
            boolean wasFull = this.internalCurrentPower >= this.getInternalMaxPower() - 0.001;
            if (wasFull && amt > 0.0) {
                try {
                    this.getProxy().getGrid().postEvent(new MENetworkPowerStorage(this, MENetworkPowerStorage.PowerEventType.REQUEST_POWER));
                } catch (GridAccessException var6) {
                }
            }

            if (this.internalCurrentPower > amt) {
                this.internalCurrentPower -= amt;
                this.changePowerLevel();
                return amt;
            } else {
                amt = this.internalCurrentPower;
                this.internalCurrentPower = 0.0;
                this.changePowerLevel();
                return amt;
            }
        }
    }

    private double getInternalMaxPower() {
        return this.internalMaxPower;
    }
}
