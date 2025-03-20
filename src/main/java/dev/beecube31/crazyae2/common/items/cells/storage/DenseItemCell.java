package dev.beecube31.crazyae2.common.items.cells.storage;

import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import org.jetbrains.annotations.NotNull;

public class DenseItemCell extends DenseCell<IAEItemStack> {

	public DenseItemCell(Materials.MaterialType whichCell, int bytes) {
		super(whichCell, bytes);
	}

	public DenseItemCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		super(whichCell, bytes, bytesPerType, 16);
	}

	public DenseItemCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		super(whichCell, bytes, 1, idleDrain);
	}

	@NotNull
	@Override
	public IItemStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IItemStorageChannel.class);
	}
}
