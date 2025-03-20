package dev.beecube31.crazyae2.common.items.cells.energy;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.core.api.storage.energy.IEnergyStorageChannel;
import org.jetbrains.annotations.NotNull;

public class MultiEnergyItemCell extends MultiEnergyCell<IAEItemStack> {

	public MultiEnergyItemCell(Materials.MaterialType whichCell, int bytes) {
		super(whichCell, bytes);
	}

	public MultiEnergyItemCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		super(whichCell, bytes, bytesPerType, 16);
	}

	public MultiEnergyItemCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		super(whichCell, bytes, 1, idleDrain);
	}

	@NotNull
	@Override
	public IEnergyStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IEnergyStorageChannel.class);
	}
}
