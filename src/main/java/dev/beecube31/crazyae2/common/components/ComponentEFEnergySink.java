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

import java.util.*;

public class ComponentEFEnergySink extends BaseEnergyDelegate implements IEnergySink {

    private int hashCode;
    boolean hasHashCode = false;
    int hashCodeSource;

    public double pastEnergy;
    public double perEnergy;
    public double tick;
    public final List<Integer> energyTicks = new ArrayList<>();
    public final List<InfoTile<IEnergyTile>> validTEs = new ArrayList<>();
    public final Map<EnumFacing, IEnergyTile> energyConductorMap = new HashMap<>();
    public long id;

    private final PartEnergyImportBus part;

    @Override
    public List<InfoTile<IEnergyTile>> getValidReceivers() {
        return this.validTEs;
    }

    public ComponentEFEnergySink(PartEnergyImportBus part) {
        super();
        this.part = part;
    }

    @Override
    public int getSinkTier() {
        return 14;
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    @Override
    public long getIdNetwork() {
        return this.id;
    }

    @Override
    public void setId(final long id) {
        this.id = id;
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
    public void AddTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.part.getTile().getWorld()).isRemote) {
            this.energyConductorMap.put(dir, tile);
            this.validTEs.add(new InfoTile<>(tile, dir.getOpposite()));
        }
    }

    @Override
    public void RemoveTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.part.getTile().getWorld()).isRemote) {
            this.energyConductorMap.remove(dir);
            Iterator<InfoTile<IEnergyTile>> iter = this.validTEs.iterator();
            while (iter.hasNext()) {
                InfoTile<IEnergyTile> tileInfoTile = iter.next();
                if (tileInfoTile.tileEntity == tile) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    @Override
    public Map<EnumFacing, IEnergyTile> getTiles() {
        return this.energyConductorMap;
    }

    @Override
    public double getDemandedEnergy() {
        return this.part.getDemandedEnergy(CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    @Override
    public void receiveEnergy(double amount) {
        this.part.receiveEnergy(amount, CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    @Override
    public List<Integer> getEnergyTickList() {
        return energyTicks;
    }

    @Override
    public double getPerEnergy() {
        return this.perEnergy;
    }

    @Override
    public double getPastEnergy() {
        return this.pastEnergy;
    }

    @Override
    public void setPastEnergy(final double pastEnergy) {
        this.pastEnergy = pastEnergy;
    }

    @Override
    public void addPerEnergy(final double setEnergy) {
        this.perEnergy = setEnergy;
    }

    @Override
    public void addTick(final double tick) {
        this.tick = tick;
    }

    @Override
    public double getTick() {
        return this.tick;
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
