package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.features.DamagedItemDefinition;
import appeng.core.features.IStackSrc;
import appeng.items.materials.MaterialType;
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
import org.jetbrains.annotations.NotNull;
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
	}


	@NotNull
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

	public IItemDefinition quantumProcessor() {
		return this.quantumProcessor;
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

		QUANTUM_PROCESSOR("quantum_processor", Features.STUB);


		private static Int2ObjectLinkedOpenHashMap<MaterialType> cachedValues;

		private final String id;
		private final Features features;
		private final String translationKey;
		private final ModelResourceLocation model;
		private boolean isRegistered;
		private int damageValue = this.ordinal();
		private Item itemInstance;
		private IStackSrc stackSrc;

		MaterialType(String id, Features features) {
			this.id = id;
			this.features = features;
			this.translationKey = "item." + Tags.MODID + ".material." + id;
			this.model = new ModelResourceLocation(new ResourceLocation(Tags.MODID, "material/" + id), "inventory");
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
