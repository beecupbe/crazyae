package dev.beecube31.crazyae2.common.items.cells.storage;

import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import org.jetbrains.annotations.NotNull;

public class DenseItemCell extends DenseCell<IAEItemStack> {

	public DenseItemCell(Materials.MaterialType whichCell, int bytes, int type) {
		super(whichCell, bytes, type);
	}

	public DenseItemCell(Materials.MaterialType whichCell, int bytes, int bytesPerType, int type) {
		super(whichCell, bytes, bytesPerType, 16, type);
	}

	public DenseItemCell(Materials.MaterialType whichCell, int bytes, double idleDrain, int type) {
		super(whichCell, bytes, 1, idleDrain, type);
	}

	@NotNull
	@Override
	public IItemStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IItemStorageChannel.class);
	}
}
