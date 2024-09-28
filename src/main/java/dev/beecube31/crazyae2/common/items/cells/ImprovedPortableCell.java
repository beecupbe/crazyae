package dev.beecube31.crazyae2.common.items.cells;

import appeng.api.storage.channels.IItemStorageChannel;
import appeng.core.Api;
import org.jetbrains.annotations.NotNull;

public class ImprovedPortableCell extends DensePortableCell {

	public ImprovedPortableCell(double batteryCapacity, int bytes, int bytesPerType, double idleDrain) {
		super(batteryCapacity, bytes, bytesPerType, idleDrain);
	}

	@NotNull
	@Override
	public IItemStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IItemStorageChannel.class);
	}
}
