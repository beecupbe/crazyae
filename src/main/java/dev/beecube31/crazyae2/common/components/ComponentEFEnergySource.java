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

import java.util.List;
import java.util.Map;

public class ComponentEFEnergySource extends BaseEnergyDelegate implements IEnergySource {

    private int hashCode;
    boolean hasHashCode = false;
    int hashCodeSource;

    private final PartEnergyExportBus part;

    public ComponentEFEnergySource(PartEnergyExportBus part) {
        super();
        this.part = part;
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
    public List<InfoTile<IEnergyTile>> getValidReceivers() {
        return this.part.getValidReceivers();
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
        return this.part.getTiles();
    }

    @Override
    public @NotNull BlockPos getBlockPos() {
        return this.part.getHost().getTile().getPos();
    }

    public int getSourceTier() {
        return 14;
    }

    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing dir) {
        return dir == this.part.getSide().getFacing();
    }

    public long getIdNetwork() {
        return this.part.getIdNetwork();
    }

    public void setId(final long id) {
        this.part.setId(id);
    }

    public double canExtractEnergy() {
        return this.part.availableEnergy(CrazyAE.definitions().items().EFEnergyAsAeStack());
    }

    public void extractEnergy(double amount) {
        this.part.extractEnergy(amount, CrazyAE.definitions().items().EFEnergyAsAeStack());
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
    public boolean isSource() {
        return true;
    }

    @Override
    public TileEntity getTileEntity() {
        return this.part.getHost().getTile();
    }
}
