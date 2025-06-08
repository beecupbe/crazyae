package dev.beecube31.crazyae2.common.tile.crafting;

import appeng.tile.crafting.TileCraftingTile;
import dev.beecube31.crazyae2.common.blocks.crafting.BlockDenseCraftingUnit;
import dev.beecube31.crazyae2.common.interfaces.ICrazyCraftingTile;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileDenseCraftingUnit extends TileCraftingTile implements ICrazyCraftingTile {
	@Override
	protected ItemStack getItemFromTile(Object obj) {
		if (this.world != null && !this.notLoaded() && !this.isInvalid()) {
			var unit = (BlockDenseCraftingUnit) this.world.getBlockState(this.pos).getBlock();
			return unit.getType().getBlock().maybeStack(1).orElse(ItemStack.EMPTY);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean isAccelerator() {
		return false;
	}

    @Override
	public long getStorageCnt() {
		var block = this.getBlock();
		if (block != null) {
			return block.getType().getBytes();
		}
		return 0;
	}

	public @Nullable BlockDenseCraftingUnit getBlock() {
		if (this.world != null && !this.notLoaded() && !this.isInvalid()) {
			return (BlockDenseCraftingUnit) this.world.getBlockState(this.pos).getBlock();
		} else {
			return null;
		}
	}

	@Override
	public int getAccelerationFactor() {
		var block = this.getBlock();
		if (block != null) {
			return block.getType().getAccelerationFactor();
		}
		return 0;
	}
}
