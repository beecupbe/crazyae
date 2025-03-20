package dev.beecube31.crazyae2.common.items.cells.storage;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.core.api.storage.IManaStorageChannel;
import org.jetbrains.annotations.NotNull;

public class ManaItemCell extends ManaCell<IAEItemStack> {

	public ManaItemCell(Materials.MaterialType whichCell, int bytes) {
		super(whichCell, bytes);
	}

	public ManaItemCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		super(whichCell, bytes, bytesPerType, 16);
	}

	public ManaItemCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		super(whichCell, bytes, 1, idleDrain);
	}

	@NotNull
	@Override
	public IManaStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IManaStorageChannel.class);
	}
}
