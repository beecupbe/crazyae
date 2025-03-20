package dev.beecube31.crazyae2.common.items.cells.storage;

import appeng.api.storage.data.IAEStack;
import appeng.core.localization.GuiText;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import dev.beecube31.crazyae2.common.items.cells.BaseCell;
import dev.beecube31.crazyae2.common.registration.definitions.Materials;
import dev.beecube31.crazyae2.core.CrazyAEConfig;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class DenseCell<T extends IAEStack<T>> extends BaseCell {
	protected final Materials.MaterialType component;
	protected final int capacity;
	protected final double idleDrain;
	protected final int bytesPerType;

	public DenseCell(Materials.MaterialType whichCell, int bytes) {
		this(whichCell, bytes, 1, 16);
	}

	public DenseCell(Materials.MaterialType whichCell, int bytes, int bytesPerType) {
		this(whichCell, bytes, bytesPerType, 16);
	}

	public DenseCell(Materials.MaterialType whichCell, int bytes, double idleDrain) {
		this(whichCell, bytes, 1, idleDrain);
	}

	public DenseCell(Materials.MaterialType whichCell, int bytes, int bytesPerType, double idleDrain) {
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
		return GuiText.StorageCells.getUnlocalized();
	}

	@Override
	public int getTotalTypes(@NotNull ItemStack cellItem) {
		return CrazyAEConfig.cellItemsTypesAmt;
	}

	@Override
	public boolean isBlackListed(@NotNull ItemStack cellItem, @NotNull IAEStack requestedAddition) {
		return false;
	}

	@Override
	public boolean isEditable(ItemStack is) {
		return true;
	}
}
