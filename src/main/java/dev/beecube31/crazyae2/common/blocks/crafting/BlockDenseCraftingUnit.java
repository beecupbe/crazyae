package dev.beecube31.crazyae2.common.blocks.crafting;

import appeng.api.definitions.ITileDefinition;
import appeng.block.crafting.BlockCraftingUnit;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockDenseCraftingUnit extends BlockCraftingUnit {
	public final DenseCraftingUnitType type;

	public BlockDenseCraftingUnit(DenseCraftingUnitType type) {
		super(null);
		this.type = type;
	}

	public DenseCraftingUnitType getType() {
		return this.type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		Utils.addReqChannelTooltip(tooltip);
		super.addInformation(stack, player, tooltip, advanced);
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
		STORAGE_2GB(2147483648L, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage2gb();
			}
		},
		STORAGE_8GB(2147483648L * 4, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage8gb();
			}
		},
		STORAGE_32GB(2147483648L * 16, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage32gb();
			}
		},
		STORAGE_128GB(2147483648L * 64, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorage128gb();
			}
		},
		STORAGE_CREATIVE(Long.MAX_VALUE, 0) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().craftingStorageCreative();
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
		},
		COPROCESSOR_262144X(0, 262144) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor262144x();
			}
		},
		COPROCESSOR_1048576X(0, 1048576) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor1048576x();
			}
		},
		COPROCESSOR_4194304X(0, 4194304) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessor4194304x();
			}
		},
		COPROCESSOR_CREATIVE(0, Integer.MAX_VALUE) {
			@Override
			public ITileDefinition getBlock() {
				return CrazyAE.definitions().blocks().coprocessorCreative();
			}
		};


		private final long bytes;
		private final int accelFactor;

		DenseCraftingUnitType(long bytes, int accelFactor) {
			this.bytes = bytes;
			this.accelFactor = accelFactor;
		}

		public long getBytes() {
			return this.bytes;
		}

		public abstract ITileDefinition getBlock();

		public int getAccelerationFactor() {
			return this.accelFactor;
		}
	}
}
