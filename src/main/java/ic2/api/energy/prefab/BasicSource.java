package ic2.api.energy.prefab;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.ILocatable;

public class BasicSource extends ic2.api.energy.prefab.BasicEnergyTile implements IEnergySource {
	public BasicSource(TileEntity parent, double capacity, int tier) {
		super(parent, capacity);

		if (tier < 0) throw new IllegalArgumentException("invalid tier: "+tier);

		this.tier = tier;
		double power = EnergyNet.instance.getPowerFromTier(tier);

		if (getCapacity() < power) setCapacity(power);
	}

	public BasicSource(ILocatable parent, double capacity, int tier) {
		super(parent, capacity);

		if (tier < 0) throw new IllegalArgumentException("invalid tier: "+tier);

		this.tier = tier;
		double power = EnergyNet.instance.getPowerFromTier(tier);

		if (getCapacity() < power) setCapacity(power);
	}

	public BasicSource(World world, BlockPos pos, double capacity, int tier) {
		super(world, pos, capacity);

		if (tier < 0) throw new IllegalArgumentException("invalid tier: "+tier);

		this.tier = tier;
		double power = EnergyNet.instance.getPowerFromTier(tier);

		if (getCapacity() < power) setCapacity(power);
	}

	public void setSourceTier(int tier) {
		if (tier < 0) throw new IllegalArgumentException("invalid tier: "+tier);

		double power = EnergyNet.instance.getPowerFromTier(tier);

		if (getCapacity() < power) setCapacity(power);

		this.tier = tier;
	}

	// energy net interface >>

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		return getEnergyStored();
	}

	@Override
	public void drawEnergy(double amount) {
		setEnergyStored(getEnergyStored() - amount);
	}

	@Override
	public int getSourceTier() {
		return tier;
	}

	// << energy net interface

	@Override
	protected String getNbtTagName() {
		return "IC2BasicSource";
	}

	protected int tier;
}
