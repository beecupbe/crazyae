package dev.beecube31.crazyae2.common.components;

import appeng.api.definitions.IItemDefinition;
import com.denfop.api.sytem.*;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyMoreDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyExportBus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ComponentMoreEnergySource extends BaseEnergyMoreDelegate implements ISource {
    private int hashCodeSource;
    private int hashCode;
    private boolean hasHashCode = false;
    private final IItemDefinition what;
    private final PartEnergyExportBus part;

    public double pastEnergy;
    public double perEnergy;
    public final List<InfoTile<ITile>> validTEsQS = new ArrayList<>();
    public final Map<EnumFacing, ITile> energyConductorMapQS = new HashMap<>();
    public long id;

    public ComponentMoreEnergySource(IItemDefinition what, PartEnergyExportBus part) {
        super();
        this.what = what;
        this.part = part;
    }

    public boolean emitsTo(IAcceptor receiver, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    public long getIdNetwork() {
        return this.id;
    }

    @Override
    public void setHashCodeSource(final int hashCode) {
        this.hashCodeSource = hashCode;
    }

    @Override
    public int getHashCodeSource() {
        return hashCodeSource;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public void AddTile(EnergyType energyType, ITile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            if (!this.energyConductorMapQS.containsKey(dir)) {
                this.energyConductorMapQS.put(dir, tile);
                validTEsQS.add(new InfoTile<>(tile, dir.getOpposite()));
            }
        }
    }

    @Override
    public void RemoveTile(EnergyType energyType, ITile tile, EnumFacing dir) {
        if (!(this.getTile().getWorld()).isRemote) {
            this.energyConductorMapQS.remove(dir);
            final Iterator<InfoTile<ITile>> iter = validTEsQS.iterator();
            while (iter.hasNext()){
                InfoTile<ITile> tileInfoTile = iter.next();
                if (tileInfoTile.tileEntity == tile) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    @Override
    public Map<EnumFacing, ITile> getTiles(EnergyType energyType) {
        return this.energyConductorMapQS;
    }


    @Override
    public List<InfoTile<ITile>> getValidReceivers(final EnergyType energyType) {
        return this.validTEsQS;
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

    @Override
    public double canProvideEnergy() {
        return this.part.availableEnergy(this.what);
    }

    @Override
    public TileEntity getTile() {
        return this.part.getHost().getTile();
    }

    public void extractEnergy(double amount) {
        this.part.extractEnergy(amount, this.what);
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
        this.perEnergy += setEnergy;
    }

    @Override
    public boolean isSource() {
        return true;
    }

}
