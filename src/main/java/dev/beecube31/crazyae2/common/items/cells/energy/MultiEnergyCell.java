package dev.beecube31.crazyae2.common.items.cells.energy;

import appeng.api.definitions.IItemDefinition;
import appeng.api.storage.data.IAEStack;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import dev.beecube31.crazyae2.common.items.cells.BaseCell;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.core.CrazyAESidedHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class MultiEnergyCell<T extends IAEStack<T>> extends BaseCell {
	protected final Materials.MaterialType component;
	protected final int capacity;
	protected final double idleDrain;
	protected final int bytesPerType;

	public MultiEnergyCell(Materials.MaterialType whichCell, int bytes) {
		this(whichCell, bytes, 1, 16);
	}

	public MultiEnergyCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		this(whichCell, bytes, bytesPerType, 16);
	}

	public MultiEnergyCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		this(whichCell, bytes, 1, idleDrain);
	}

	public MultiEnergyCell(Materials.MaterialType whichCell, int bytes, int bytesPerType, double idleDrain) {
		super(whichCell, bytes, bytesPerType, idleDrain);
		this.setMaxStackSize(1);
		this.component = whichCell;
		this.capacity = bytes;
		this.idleDrain = idleDrain;
		this.bytesPerType = bytesPerType;
	}

	public IItemHandler getUpgradesInventory(ItemStack is) {
		return new CellUpgrades(is, 2);
	}

	public IItemHandler getConfigInventory(ItemStack is) {
		return new CellConfig(is);
	}

	@Override
	public String getUnlocalizedGroupName(Set others, ItemStack is) {
		return CrazyAEGuiText.MULTI_ENERGY_CELLS.getUnlocalized();
	}

	@Override
	public int getTotalTypes(@NotNull ItemStack cellItem) {
		return CrazyAESidedHandler.availableEnergyTypes.size();
	}

	@Override
	public boolean isBlackListed(@NotNull ItemStack cellItem, @NotNull IAEStack requestedAddition) {
		for (IItemDefinition candidate : CrazyAESidedHandler.availableEnergyTypes) {
			if (candidate.isSameAs(requestedAddition.asItemStackRepresentation())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isEditable(ItemStack is) {
		return true;
	}
}
