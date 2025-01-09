package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkTile;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.interfaces.IChannelsMultiplier;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;

import java.io.IOException;
import java.util.EnumSet;

public class TileQuantumChannelsBooster extends AENetworkTile implements IPowerChannelState, IChannelsMultiplier {

    public static final int POWERED_FLAG = 1;
    public static final int CHANNEL_FLAG = 2;

    private int clientFlags = 0;
    private boolean isRegistered = false;

    public TileQuantumChannelsBooster() {
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setValidSides(EnumSet.noneOf(EnumFacing.class));
        this.getProxy().setIdlePowerUsage(CrazyAEConfig.QCMBoostAmt * 256);
    }

    @Override
    public void setOrientation(final EnumFacing inForward, final EnumFacing inUp) {
        super.setOrientation(inForward, inUp);
        this.getProxy().setValidSides(EnumSet.of(this.getForward().getOpposite()));
    }

    @MENetworkEventSubscribe
    public void chanRender(final MENetworkChannelsChanged c) {
        this.markForUpdate();
    }

    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        this.markForUpdate();
    }

    @Override
    protected boolean readFromStream(final ByteBuf data) throws IOException {
        final boolean c = super.readFromStream(data);
        final int old = this.getClientFlags();
        this.setClientFlags(data.readByte());

        return old != this.getClientFlags() || c;
    }

    @Override
    protected void writeToStream(final ByteBuf data) throws IOException {
        super.writeToStream(data);
        this.setClientFlags(0);

        try {
            if (this.getProxy().getEnergy().isNetworkPowered()) {
                this.setClientFlags(this.getClientFlags() | POWERED_FLAG);
            }

            if (this.getProxy().getNode().meetsChannelRequirements()) {
                this.setClientFlags(this.getClientFlags() | CHANNEL_FLAG);
            }
        } catch (final GridAccessException e) {
            // meh
        }

        data.writeByte((byte) this.getClientFlags());
    }

    @Override
    public AECableType getCableConnectionType(final AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void onReady() {
        super.onReady();
    }

    @Override
    public void saveChanges() {
        super.saveChanges();
    }

    @Override
    public boolean isActive() {
        if (Platform.isClient()) {
            return this.isPowered() && (CHANNEL_FLAG == (this.getClientFlags() & CHANNEL_FLAG));
        }

        if (!this.isRegistered) {
            try {
                this.getProxy().getGrid().<IGridChannelBoostersCache>getCache(IGridChannelBoostersCache.class).addNode(this.getProxy().getNode(), this.getProxy().getMachine());
                this.isRegistered = true;
            } catch (GridAccessException e) {
                // :c
            }
        }

        return this.getProxy().isActive();
    }

    public void removeBoost() {
        if (this.isRegistered) {
            try {
                this.getProxy().getGrid().<IGridChannelBoostersCache>getCache(IGridChannelBoostersCache.class).removeNode(this.getProxy().getNode(), this.getProxy().getMachine());
                this.isRegistered = true;
            } catch (GridAccessException e) {
                // :c
            }
        }
    }

    @Override
    public boolean isPowered() {
        return POWERED_FLAG == (this.getClientFlags() & POWERED_FLAG);
    }

    public int getClientFlags() {
        return this.clientFlags;
    }

    private void setClientFlags(final int clientFlags) {
        this.clientFlags = clientFlags;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
