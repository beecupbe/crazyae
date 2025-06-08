package dev.beecube31.crazyae2.common.items.cells.exp;

import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.core.api.storage.exp.IExperienceStorageChannel;
import org.jetbrains.annotations.NotNull;

public class ExperienceItemCell extends ExperienceCell<IAEItemStack> {

	public ExperienceItemCell(Materials.MaterialType whichCell, int bytes) {
		super(whichCell, bytes);
	}

	public ExperienceItemCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		super(whichCell, bytes, bytesPerType, 16);
	}

	public ExperienceItemCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		super(whichCell, bytes, 1, idleDrain);
	}

	@NotNull
	@Override
	public IExperienceStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IExperienceStorageChannel.class);
	}
}
