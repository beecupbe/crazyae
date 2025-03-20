package dev.beecube31.crazyae2.common.components;

import com.denfop.api.energy.IEnergyEmitter;
import com.denfop.api.energy.IEnergySink;
import com.denfop.api.energy.IEnergyTile;
import com.denfop.api.sytem.InfoTile;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyImportBus;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComponentEFEnergySink extends BaseEnergyDelegate implements IEnergySink {

    private int hashCode;
    boolean hasHashCode = false;
    int hashCodeSource;

    private final PartEnergyImportBus part;
    List<Integer> energyTicks = new LinkedList<>();

    public List<InfoTile<IEnergyTile>> getValidReceivers() {
        return this.part.validTEs;
    }

    public ComponentEFEnergySink(PartEnergyImportBus part) {
        super();
        this.part = part;
    }

    public int getSinkTier() {
        return 14;
    }

    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    public long getIdNetwork() {
        return this.part.getIdNetwork();
    }

    public void setId(final long id) {
        this.part.setId(id);
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
    public int hashCode() {
        if (!hasHashCode) {
            hasHashCode = true;
            this.hashCode = this.part.hashCode();
        }
        return hashCode;
    }

    @Override
    public void AddTile(final IEnergyTile tile, final EnumFacing dir) {
        this.part.AddTile(tile, dir);
    }

    @Override
    public void RemoveTile(final IEnergyTile tile, final EnumFacing dir) {
        this.part.RemoveTile(tile, dir);
    }

    @Override
    public Map<EnumFacing, IEnergyTile> getTiles() {
        return this.part.energyConductorMap;
    }

    public double getDemandedEnergy() {
        return this.part.getDemandedEnergy(CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    public void receiveEnergy(double amount) {
        this.part.receiveEnergy(amount, CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    @Override
    public List<Integer> getEnergyTickList() {
        return energyTicks;
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

    @Override
    public TileEntity getTileEntity() {
        return this.part.getHost().getTile();
    }

    @Override
    public @NotNull BlockPos getBlockPos() {
        return this.part.getHost().getTile().getPos();
    }
}
