package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.features.DamagedItemDefinition;
import appeng.core.features.IStackSrc;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.items.CrazyAEMaterial;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.DamagedDefinitions;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEDamagedItemRendering;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEIModelProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Materials implements DamagedDefinitions<DamagedItemDefinition, Materials.MaterialType> {
	private final Object2ObjectOpenHashMap<String, DamagedItemDefinition> byId = new Object2ObjectOpenHashMap<>();

	private final IItemDefinition cellPart256k;
	private final IItemDefinition cellPart1mb;
	private final IItemDefinition cellPart4mb;
	private final IItemDefinition cellPart16mb;
	private final IItemDefinition cellPart64mb;
	private final IItemDefinition cellPart256mb;
	private final IItemDefinition cellPart1gb;
	private final IItemDefinition cellPart2gb;

	private final IItemDefinition fluidCellPart256k;
	private final IItemDefinition fluidCellPart1mb;
	private final IItemDefinition fluidCellPart4mb;
	private final IItemDefinition fluidCellPart16mb;
	private final IItemDefinition fluidCellPart64mb;
	private final IItemDefinition fluidCellPart256mb;
	private final IItemDefinition fluidCellPart1gb;
	private final IItemDefinition fluidCellPart2gb;

	private final IItemDefinition quantumProcessor;
	private final IItemDefinition manaProcessor;

	private final IItemDefinition manaPart1k;
	private final IItemDefinition manaPart4k;
	private final IItemDefinition manaPart16k;
	private final IItemDefinition manaPart64k;
	private final IItemDefinition manaPart256k;
	private final IItemDefinition manaPart1mb;
	private final IItemDefinition manaPart4mb;
	private final IItemDefinition manaPart16mb;
	private final IItemDefinition manaPart64mb;
	private final IItemDefinition manaPart256mb;
	private final IItemDefinition manaPart1gb;
	private final IItemDefinition manaPart2gb;



	private final CrazyAEMaterial material;

	public Materials(Registry registry) {
		this.material = new CrazyAEMaterial();
		registry.item("material", () -> this.material)
			.rendering(new CrazyAEDamagedItemRendering<>(this))
			.build();

		this.cellPart256k = this.createMaterial(this.material, MaterialType.CELL_PART_256K);
		this.cellPart1mb = this.createMaterial(this.material, MaterialType.CELL_PART_1MB);
		this.cellPart4mb = this.createMaterial(this.material, MaterialType.CELL_PART_4MB);
		this.cellPart16mb = this.createMaterial(this.material, MaterialType.CELL_PART_16MB);
		this.cellPart64mb = this.createMaterial(this.material, MaterialType.CELL_PART_64MB);
		this.cellPart256mb = this.createMaterial(this.material, MaterialType.CELL_PART_256MB);
		this.cellPart1gb = this.createMaterial(this.material, MaterialType.CELL_PART_1GB);
		this.cellPart2gb = this.createMaterial(this.material, MaterialType.CELL_PART_2GB);
		
		this.fluidCellPart256k = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_256K);
		this.fluidCellPart1mb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_1MB);
		this.fluidCellPart4mb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_4MB);
		this.fluidCellPart16mb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_16MB);
		this.fluidCellPart64mb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_64MB);
		this.fluidCellPart256mb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_256MB);
		this.fluidCellPart1gb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_1GB);
		this.fluidCellPart2gb = this.createMaterial(this.material, MaterialType.CELL_FLUID_PART_2GB);

		this.quantumProcessor = this.createMaterial(this.material, MaterialType.QUANTUM_PROCESSOR);
		this.manaProcessor = this.createMaterial(this.material, MaterialType.MANA_PROCESSOR);

		this.manaPart1k = this.createMaterial(this.material, MaterialType.MANA_PART_1K);
		this.manaPart4k = this.createMaterial(this.material, MaterialType.MANA_PART_4K);
		this.manaPart16k = this.createMaterial(this.material, MaterialType.MANA_PART_16K);
		this.manaPart64k = this.createMaterial(this.material, MaterialType.MANA_PART_64K);
		this.manaPart256k = this.createMaterial(this.material, MaterialType.MANA_PART_256K);
		this.manaPart1mb = this.createMaterial(this.material, MaterialType.MANA_PART_1MB);
		this.manaPart4mb = this.createMaterial(this.material, MaterialType.MANA_PART_4MB);
		this.manaPart16mb = this.createMaterial(this.material, MaterialType.MANA_PART_16MB);
		this.manaPart64mb = this.createMaterial(this.material, MaterialType.MANA_PART_64MB);
		this.manaPart256mb = this.createMaterial(this.material, MaterialType.MANA_PART_256MB);
		this.manaPart1gb = this.createMaterial(this.material, MaterialType.MANA_PART_1GB);
		this.manaPart2gb = this.createMaterial(this.material, MaterialType.MANA_PART_2GB);
	}


	private DamagedItemDefinition createMaterial(CrazyAEMaterial material, MaterialType materialType) {
		var def = new DamagedItemDefinition(materialType.getId(),
				material.createMaterial(materialType));

		this.byId.put(materialType.getId(), def);
		return def;
	}

	public Optional<MaterialType> getById(int itemDamage) {
		return Optional.ofNullable(MaterialType.getCachedValues().getOrDefault(itemDamage, null));
	}

	public Optional<DamagedItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	@Override
	public Collection<MaterialType> getEntries() {
		return MaterialType.getCachedValues().values();
	}

	@Nullable
	@Override
	public MaterialType getType(ItemStack is) {
		return this.material.getTypeByStack(is);
	}

	public IItemDefinition manaPart1k() {
		return this.manaPart1k;
	}

	public IItemDefinition manaPart4k() {
		return this.manaPart4k;
	}

	public IItemDefinition manaPart16k() {
		return this.manaPart16k;
	}

	public IItemDefinition manaPart64k() {
		return this.manaPart64k;
	}

	public IItemDefinition manaPart256k() {
		return this.manaPart256k;
	}

	public IItemDefinition manaPart1mb() {
		return this.manaPart1mb;
	}

	public IItemDefinition manaPart4mb() {
		return this.manaPart4mb;
	}

	public IItemDefinition manaPart16mb() {
		return this.manaPart16mb;
	}

	public IItemDefinition manaPart64mb() {
		return this.manaPart64mb;
	}

	public IItemDefinition manaPart256mb() {
		return this.manaPart256mb;
	}

	public IItemDefinition manaPart1gb() {
		return this.manaPart1gb;
	}

	public IItemDefinition manaPart2gb() {
		return this.manaPart2gb;
	}

	public IItemDefinition quantumProcessor() {
		return this.quantumProcessor;
	}

	public IItemDefinition manaProcessor() {
		return this.manaProcessor;
	}

	public IItemDefinition cellPart256K() {
		return this.cellPart256k;
	}

	public IItemDefinition cellPart1MB() {
		return this.cellPart1mb;
	}

	public IItemDefinition cellPart4MB() {
		return this.cellPart4mb;
	}

	public IItemDefinition cellPart16MB() {
		return this.cellPart16mb;
	}

	public IItemDefinition cellPart64MB() {
		return this.cellPart64mb;
	}

	public IItemDefinition cellPart256MB() {
		return this.cellPart256mb;
	}

	public IItemDefinition cellPart1GB() {
		return this.cellPart1gb;
	}

	public IItemDefinition cellPart2GB() {
		return this.cellPart2gb;
	}

	public IItemDefinition cellFluidPart256K() {
		return this.fluidCellPart256k;
	}

	public IItemDefinition cellFluidPart1MB() {
		return this.fluidCellPart1mb;
	}

	public IItemDefinition cellFluidPart4MB() {
		return this.fluidCellPart4mb;
	}

	public IItemDefinition cellFluidPart16MB() {
		return this.fluidCellPart16mb;
	}

	public IItemDefinition cellFluidPart64MB() {
		return this.fluidCellPart64mb;
	}

	public IItemDefinition cellFluidPart256MB() {
		return this.fluidCellPart256mb;
	}

	public IItemDefinition cellFluidPart1GB() {
		return this.fluidCellPart1gb;
	}

	public IItemDefinition cellFluidPart2GB() {
		return this.fluidCellPart2gb;
	}


	public enum MaterialType implements CrazyAEIModelProvider {
		CELL_PART_256K("cell_part_256k", Features.DENSE_CELLS),
		CELL_PART_1MB("cell_part_1mb", Features.DENSE_CELLS),
		CELL_PART_4MB("cell_part_4mb", Features.DENSE_CELLS),
		CELL_PART_16MB("cell_part_16mb", Features.DENSE_CELLS),
		CELL_PART_64MB("cell_part_64mb", Features.MEGA_DENSE_CELLS),
		CELL_PART_256MB("cell_part_256mb", Features.MEGA_DENSE_CELLS),
		CELL_PART_1GB("cell_part_1gb", Features.MEGA_DENSE_CELLS),
		CELL_PART_2GB("cell_part_2gb", Features.MEGA_DENSE_CELLS),

		CELL_FLUID_PART_256K("cell_part_fluid_256k", Features.DENSE_CELLS),
		CELL_FLUID_PART_1MB("cell_part_fluid_1mb", Features.DENSE_CELLS),
		CELL_FLUID_PART_4MB("cell_part_fluid_4mb", Features.DENSE_CELLS),
		CELL_FLUID_PART_16MB("cell_part_fluid_16mb", Features.DENSE_CELLS),
		CELL_FLUID_PART_64MB("cell_part_fluid_64mb", Features.MEGA_DENSE_CELLS),
		CELL_FLUID_PART_256MB("cell_part_fluid_256mb", Features.MEGA_DENSE_CELLS),
		CELL_FLUID_PART_1GB("cell_part_fluid_1gb", Features.MEGA_DENSE_CELLS),
		CELL_FLUID_PART_2GB("cell_part_fluid_2gb", Features.MEGA_DENSE_CELLS),

		QUANTUM_PROCESSOR("quantum_processor", Features.STUB),
		MANA_PROCESSOR("mana_processor", Features.STUB, "botania"),

		MANA_PART_1K("mana_part_1k", Features.MANA_CELLS),
		MANA_PART_4K("mana_part_4k", Features.MANA_CELLS),
		MANA_PART_16K("mana_part_16k", Features.MANA_CELLS),
		MANA_PART_64K("mana_part_64k", Features.MANA_CELLS),
		MANA_PART_256K("mana_part_256k", Features.MANA_DENSE_CELLS),
		MANA_PART_1MB("mana_part_1mb", Features.MANA_DENSE_CELLS),
		MANA_PART_4MB("mana_part_4mb", Features.MANA_DENSE_CELLS),
		MANA_PART_16MB("mana_part_16mb", Features.MANA_DENSE_CELLS),
		MANA_PART_64MB("mana_part_64mb", Features.MEGA_MANA_DENSE_CELLS),
		MANA_PART_256MB("mana_part_256mb", Features.MEGA_MANA_DENSE_CELLS),
		MANA_PART_1GB("mana_part_1gb", Features.MEGA_MANA_DENSE_CELLS),
		MANA_PART_2GB("mana_part_2gb", Features.MEGA_MANA_DENSE_CELLS);


		private static Int2ObjectLinkedOpenHashMap<MaterialType> cachedValues;

		private final String id;
		private final String modid;
		private final String translationKey;
		private final ModelResourceLocation model;
		private boolean isRegistered;
		private int damageValue = this.ordinal();
		private final Features features;
		private Item itemInstance;
		private IStackSrc stackSrc;

		MaterialType(String id, Features features) {
			this.id = id;
			this.modid = null;
			this.features = features;
			this.translationKey = "item." + Tags.MODID + ".material." + id;
			this.model = new ModelResourceLocation(new ResourceLocation(Tags.MODID, "material/" + id), "inventory");
		}

		MaterialType(String id, Features features, String modid) {
			this.id = id;
			this.features = features;
			this.translationKey = "item." + Tags.MODID + ".material." + id;
			this.model = new ModelResourceLocation(new ResourceLocation(Tags.MODID, "material/" + id), "inventory");
			this.modid = modid;
		}

		public static Int2ObjectLinkedOpenHashMap<MaterialType> getCachedValues() {
			if (cachedValues == null) {
				cachedValues = new Int2ObjectLinkedOpenHashMap<>();
				Arrays.stream(values()).forEach((materialType -> cachedValues.put(materialType.ordinal(),
					materialType)));
			}
			return cachedValues;
		}

		public String getId() {
			return this.id;
		}

		public Features getFeature() {
			return this.features;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		public ItemStack stack(final int size) {
			return new ItemStack(this.getItemInstance(), size, this.getDamageValue());
		}

		public boolean isRegistered() {
			return this.isRegistered;
		}

		@Override
		public boolean isEnabled() {
			if (this.modid != null && !Loader.isModLoaded(this.modid)) {
				return false;
			}

			return this.features != null && this.features.isEnabled();
		}

		public void markReady() {
			this.isRegistered = true;
		}

		public int getDamageValue() {
			return this.damageValue;
		}

		void setDamageValue(final int damageValue) {
			this.damageValue = damageValue;
		}

		public Item getItemInstance() {
			return this.itemInstance;
		}

		public void setItemInstance(final Item itemInstance) {
			this.itemInstance = itemInstance;
		}

		public MaterialStackSrc getStackSrc() {
			return (MaterialStackSrc) this.stackSrc;
		}

		public void setStackSrc(final MaterialStackSrc stackSrc) {
			this.stackSrc = stackSrc;
		}

		@Override
		public ModelResourceLocation getModel() {
			return this.model;
		}
	}

	public static class MaterialStackSrc implements IStackSrc {
		private final MaterialType src;
		private final boolean enabled;

		public MaterialStackSrc(MaterialType src, boolean enabled) {
			Preconditions.checkNotNull(src);
			this.src = src;
			this.enabled = enabled;
		}

		public ItemStack stack(int stackSize) {
			return this.src.stack(stackSize);
		}

		public Item getItem() {
			return this.src.getItemInstance();
		}

		public int getDamage() {
			return this.src.getDamageValue();
		}

		public boolean isEnabled() {
			return this.enabled;
		}
	}

}
