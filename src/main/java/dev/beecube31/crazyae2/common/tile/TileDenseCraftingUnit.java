package dev.beecube31.crazyae2.common.tile;

import appeng.tile.crafting.TileCraftingStorageTile;
import co.neeve.nae2.client.rendering.helpers.BeamFormerRenderHelper;
import dev.beecube31.crazyae2.client.rendering.bloom.IBloomEffectProvider;
import dev.beecube31.crazyae2.common.blocks.BlockDenseCraftingUnit;
import dev.beecube31.crazyae2.common.interfaces.IDenseCoProcessor;
import gregtech.client.utils.IBloomEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class TileDenseCraftingUnit extends TileCraftingStorageTile implements IDenseCoProcessor, IBloomEffectProvider {
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
	public boolean isStorage() {
		var block = this.getBlock();
		if (block != null) {
			return block.getType().getBytes() > 0;
		}
		return false;
	}

	public int getStorageBytes() {
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

	@Override
	public IBloomEffect getEffect() {
		return null;
//		BlockPos bp = this.getPos();
//		int x = bp.getX();
//		int y = bp.getY();
//		int z = bp.getZ();
//
	}

	@Override
	public boolean canRenderBloom() {
		return this.isValid();
	}
}
