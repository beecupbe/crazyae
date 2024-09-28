package dev.beecube31.crazyae2.common.blocks;

import appeng.api.definitions.ITileDefinition;
import appeng.block.crafting.BlockCraftingUnit;
import dev.beecube31.crazyae2.core.CrazyAE;

public class BlockDenseCraftingUnit extends BlockCraftingUnit {
	public final DenseCraftingUnitType type;

	public BlockDenseCraftingUnit(DenseCraftingUnitType type) {
		super(null);
		this.type = type;
	}

	public DenseCraftingUnitType getType() {
		return this.type;
	}

	public enum DenseCraftingUnitType {
		STORAGE_256K(256 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage256k();
			}
		},
		STORAGE_1024K(1024 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage1mb();
			}
		},
		STORAGE_4096K(4096 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage4mb();
			}
		},
		STORAGE_16384K(16384 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage16mb();
			}
		},
		STORAGE_65536K(65536 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage64mb();
			}
		},
		STORAGE_262144K(262144 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage256mb();
			}
		},
		STORAGE_1GB(1048576 * 1024, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage1gb();
			}
		},
		STORAGE_2GB(Integer.MAX_VALUE, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage2gb();
			}
		},
		COPROCESSOR_4X(0, 4) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor4x();
			}
		},
		COPROCESSOR_16X(0, 16) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor16x();
			}
		},
		COPROCESSOR_64X(0, 64) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor64x();
			}
		},
		COPROCESSOR_256X(0, 256) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor256x();
			}
		},
		COPROCESSOR_1024X(0, 1024) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor1024x();
			}
		},
		COPROCESSOR_4096X(0, 4096) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor4096x();
			}
		},
		COPROCESSOR_16384X(0, 16384) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor16384x();
			}
		},
		COPROCESSOR_65536X(0, 65536) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor65536x();
			}
		};


		private final int bytes;
		private final int accelFactor;

		DenseCraftingUnitType(int bytes, int accelFactor) {
			this.bytes = bytes;
			this.accelFactor = accelFactor;
		}

		public int getBytes() {
			return this.bytes;
		}

		public abstract ITileDefinition getBlock();

		public int getAccelerationFactor() {
			return this.accelFactor;
		}
	}
}
