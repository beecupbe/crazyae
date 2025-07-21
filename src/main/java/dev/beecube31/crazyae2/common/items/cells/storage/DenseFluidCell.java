package dev.beecube31.crazyae2.common.items.cells.storage;

import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DenseFluidCell extends DenseCell<IAEFluidStack> {

	public DenseFluidCell(Materials.MaterialType whichCell, int bytes, int type) {
		super(whichCell, bytes, type);
	}

	public DenseFluidCell(Materials.MaterialType whichCell, int bytes, int bytesPerType, int type) {
		super(whichCell, bytes, bytesPerType, 16, type);
	}

	public DenseFluidCell(Materials.MaterialType whichCell, int bytes, double idleDrain, int type) {
		super(whichCell, bytes, 1, idleDrain, type);
	}

	@NotNull
	@Override
	public IFluidStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IFluidStorageChannel.class);
	}

	@Override
	public int getTotalTypes(@NotNull ItemStack cellItem) {
		return this.type;
	}
}
