package dev.beecube31.crazyae2.common.components;

import com.denfop.api.energy.IEnergyAcceptor;
import com.denfop.api.energy.IEnergySource;
import com.denfop.api.energy.IEnergyTile;
import com.denfop.api.sytem.InfoTile;
import dev.beecube31.crazyae2.common.components.base.BaseEnergyDelegate;
import dev.beecube31.crazyae2.common.parts.implementations.PartEnergyExportBus;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ComponentEFEnergySource extends BaseEnergyDelegate implements IEnergySource {

    private int hashCode;
    boolean hasHashCode = false;
    int hashCodeSource;

    public double pastEnergy;
    public double perEnergy;
    public final List<InfoTile<IEnergyTile>> validTEs = new ArrayList<>();
    public final Map<EnumFacing, IEnergyTile> energyConductorMap = new HashMap<>();
    public long id;

    private final PartEnergyExportBus part;

    public ComponentEFEnergySource(PartEnergyExportBus part) {
        super();
        this.part = part;
    }

    @Override
    public void setHashCodeSource(int hashCode) {
        this.hashCodeSource = hashCode;
    }

    @Override
    public int getHashCodeSource() {
        return this.hashCodeSource;
    }

    @Override
    public int hashCode() {
        if (!hasHashCode) {
            hasHashCode = true;
            this.hashCode = this.part.hashCode();
        }
        return hashCode;
    }

    public List<InfoTile<IEnergyTile>> getValidReceivers() {
        return this.validTEs;
    }

    @Override
    public void AddTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.part.getTile().getWorld().isRemote)) {
            this.energyConductorMap.put(dir, tile);
            this.validTEs.add(new InfoTile<>(tile, dir.getOpposite()));
        }
    }

    @Override
    public void RemoveTile(IEnergyTile tile, EnumFacing dir) {
        if (!(this.part.getTile().getWorld().isRemote)) {
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
    public @NotNull BlockPos getBlockPos() {
        return this.part.getHost().getTile().getPos();
    }

    @Override
    public int getSourceTier() {
        return 14;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    @Override
    public long getIdNetwork() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public double canExtractEnergy() {
        return this.part.availableEnergy(CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    @Override
    public void extractEnergy(double amount) {
        this.part.extractEnergy(amount, CrazyAE.definitions().items().EFEnergyAsAeStack());
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
    public void setPastEnergy(double pastEnergy) {
        this.pastEnergy = pastEnergy;
    }

    @Override
    public void addPerEnergy(double setEnergy) {
        this.perEnergy = setEnergy;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    @Override
    public TileEntity getTileEntity() {
        return this.part.getHost().getTile();
    }
}
