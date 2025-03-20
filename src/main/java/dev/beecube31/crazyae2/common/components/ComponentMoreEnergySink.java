package dev.beecube31.crazyae2.common.components;

import appeng.api.definitions.IItemDefinition;
import com.denfop.api.sytem.*;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyMoreDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyImportBus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComponentMoreEnergySink extends BaseEnergyMoreDelegate implements ISink {
    private int hashCodeSource;
    private int hashCode;
    private boolean hasHashCode = false;
    List<ISource> systemTicks = new LinkedList<>();
    private final IItemDefinition what;
    private final PartEnergyImportBus part;

    public ComponentMoreEnergySink(IItemDefinition what, PartEnergyImportBus part) {
        super();
        this.what = what;
        this.part = part;
    }

    public boolean acceptsFrom(IEmitter emitter, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    public long getIdNetwork() {
        return this.part.getIdNetwork();
    }

    @Override
    public void setHashCodeSource(final int hashCode) {
        hashCodeSource = hashCode;
    }

    @Override
    public int getHashCodeSource() {
        return hashCodeSource;
    }

    @Override
    public List<ISource> getEnergyTickList() {
        return systemTicks;
    }


    public void setId(final long id) {
        this.part.setId(id);
    }

    @Override
    public void AddTile(EnergyType type, final ITile tile, final EnumFacing dir) {
        this.part.AddTile(type,tile, dir);
    }

    @Override
    public void RemoveTile(EnergyType type,final ITile tile, final EnumFacing dir) {
        this.part.RemoveTile(type,tile, dir);
    }

    @Override
    public Map<EnumFacing, ITile> getTiles(EnergyType energyType) {
        return this.part.energyConductorMapQS;
    }


    @Override
    public List<InfoTile<ITile>> getValidReceivers(final EnergyType energyType) {
        return this.part.validTEsQS;
    }

    @Override
    public int hashCode() {
        if (!hasHashCode) {
            hasHashCode = true;
            this.hashCode = this.part.hashCode();
        }
        return hashCode;
    }

    @Override
    public @NotNull BlockPos getBlockPos() {
        return this.part.getHost().getTile().getPos();
    }

    public double getDemanded() {
        return this.part.getDemandedEnergy(this.what);
    }

    @Override
    public TileEntity getTile() {
        return this.part.getHost().getTile();
    }

    public void receivedEnergy(double amount) {
        this.part.receiveEnergy(this.what, amount);
    }

    @Override
    public double getPerEnergy() {
        return this.part.getPerEnergy();
    }

    @Override
    public double getPastEnergy() {
        return this.part.getPastEnergy();
    }

    @Override
    public void setPastEnergy(final double pastEnergy) {
        this.part.setPastEnergy(pastEnergy);
    }

    @Override
    public void addPerEnergy(final double setEnergy) {
        this.part.addPerEnergy(setEnergy);
    }

    @Override
    public void addTick(final double tick) {
        this.part.tick = tick;
    }

    @Override
    public double getTick() {
        return this.part.tick;
    }

    @Override
    public boolean isSink() {
        return true;
    }
}
