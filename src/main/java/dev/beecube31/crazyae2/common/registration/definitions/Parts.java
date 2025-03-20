package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import com.google.common.collect.ImmutableList;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.items.CrazyAEBaseItemPart;
import dev.beecube31.crazyae2.common.parts.implementations.*;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.PartFluidExportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.PartFluidImportBusImp;
import dev.beecube31.crazyae2.common.registration.registry.CrazyAEDamagedItemDefinition;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.helpers.PartModelsHelper;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.Definitions;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.IDefinition;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEItemPartRendering;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Constructor;
import java.util.*;

@SuppressWarnings("unused")
public class Parts implements Definitions<CrazyAEDamagedItemDefinition> {
	private final Object2ObjectOpenHashMap<String, CrazyAEDamagedItemDefinition> byId = new Object2ObjectOpenHashMap<>();
	private final CrazyAEBaseItemPart itemPart;

	private final CrazyAEDamagedItemDefinition improvedImportBus;
	private final CrazyAEDamagedItemDefinition improvedExportBus;
	private final CrazyAEDamagedItemDefinition improvedImportFluidBus;
	private final CrazyAEDamagedItemDefinition improvedExportFluidBus;

	private final CrazyAEDamagedItemDefinition manaImportBus;
	private final CrazyAEDamagedItemDefinition manaExportBus;

	private final CrazyAEDamagedItemDefinition energyImportBus;
	private final CrazyAEDamagedItemDefinition energyExportBus;

	private final CrazyAEDamagedItemDefinition manaTerm;

	private final CrazyAEDamagedItemDefinition energyTerminal;

	//private final CrazyAEDamagedItemDefinition partDrive;

	private final CrazyAEDamagedItemDefinition partPatternsInterface;
	private final CrazyAEDamagedItemDefinition partPerfectInterface;


	public Parts(Registry registry) {
		this.itemPart = new CrazyAEBaseItemPart();
		registry.item("part", () -> this.itemPart)
			.rendering(new CrazyAEItemPartRendering(this.itemPart))
			.build();

		// Register all part models
		var partModels = AEApi.instance().registries().partModels();
		for (var partType : PartType.values()) {
			partModels.registerModels(partType.getModels());
		}

		this.improvedImportBus = this.createPart(this.itemPart, PartType.IMPROVED_IMPORT_BUS);
		this.improvedExportBus = this.createPart(this.itemPart, PartType.IMPROVED_EXPORT_BUS);

		this.improvedImportFluidBus = this.createPart(this.itemPart, PartType.IMPROVED_IMPORT_FLUID_BUS);
		this.improvedExportFluidBus = this.createPart(this.itemPart, PartType.IMPROVED_EXPORT_FLUID_BUS);

		this.manaImportBus = this.createPart(this.itemPart, PartType.MANA_IMPORT_BUS);
		this.manaExportBus = this.createPart(this.itemPart, PartType.MANA_EXPORT_BUS);

		this.manaTerm = this.createPart(this.itemPart, PartType.MANA_TERM);
		this.energyTerminal = this.createPart(this.itemPart, PartType.ENERGY_TERM);

		//this.partDrive = this.createPart(this.itemPart, PartType.PART_DRIVE);

		this.partPatternsInterface = this.createPart(this.itemPart, PartType.PART_PATTERNS_IFACE);
		this.partPerfectInterface = this.createPart(this.itemPart, PartType.PART_PERFECT_IFACE);

		this.energyImportBus = this.createPart(this.itemPart, PartType.ENERGY_IMPORT_BUS);
		this.energyExportBus = this.createPart(this.itemPart, PartType.ENERGY_EXPORT_BUS);
	}

	public CrazyAEDamagedItemDefinition improvedImportBus() {
		return this.improvedImportBus;
	}

	public CrazyAEDamagedItemDefinition improvedExportBus() {
		return this.improvedExportBus;
	}

	public CrazyAEDamagedItemDefinition improvedImportFluidBus() {
		return this.improvedImportFluidBus;
	}

	public CrazyAEDamagedItemDefinition improvedExportFluidBus() {
		return this.improvedExportFluidBus;
	}

	public CrazyAEDamagedItemDefinition manaImportBus() {
		return this.manaImportBus;
	}

	public CrazyAEDamagedItemDefinition manaExportBus() {
		return this.manaExportBus;
	}

	public CrazyAEDamagedItemDefinition energyExportBus() {
		return this.energyExportBus;
	}

	public CrazyAEDamagedItemDefinition energyImportBus() {
		return this.energyImportBus;
	}

	public CrazyAEDamagedItemDefinition manaTerminal() {
		return this.manaTerm;
	}

	public CrazyAEDamagedItemDefinition energyTerminal() {
		return this.energyTerminal;
	}

//	public CrazyAEDamagedItemDefinition partDrive() {
//		return this.partDrive;
//	}

	public CrazyAEDamagedItemDefinition perfectInterface() {
		return this.partPerfectInterface;
	}

	public CrazyAEDamagedItemDefinition patternsInterface() {
		return this.partPatternsInterface;
	}

	public static Optional<PartType> getById(int itemDamage) {
		return Optional.ofNullable(PartType.getCachedValues().getOrDefault(itemDamage, null));
	}

	private CrazyAEDamagedItemDefinition createPart(CrazyAEBaseItemPart baseItemPart, PartType partType) {
		var def = new CrazyAEDamagedItemDefinition(partType.getId(), baseItemPart.createPart(partType));

		this.byId.put(partType.id, def);
		return def;
	}

	@Override
	public Optional<CrazyAEDamagedItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	public enum PartType implements IDefinition {
		IMPROVED_IMPORT_BUS("improved_import_bus", PartImportBusImp.class, Features.IMPROVED_BUSES),
		IMPROVED_EXPORT_BUS("improved_export_bus", PartExportBusImp.class, Features.IMPROVED_BUSES),

		IMPROVED_IMPORT_FLUID_BUS("improved_import_fluid_bus", PartFluidImportBusImp.class, Features.IMPROVED_BUSES),
		IMPROVED_EXPORT_FLUID_BUS("improved_export_fluid_bus", PartFluidExportBusImp.class, Features.IMPROVED_BUSES),

		MANA_IMPORT_BUS("mana_import_bus", PartManaImportBus.class, Features.MANA_BUSES),
		MANA_EXPORT_BUS("mana_export_bus", PartManaExportBus.class, Features.MANA_BUSES),

		MANA_TERM("mana_terminal", PartManaTerminal.class, Features.MANA_TERM),

		ENERGY_IMPORT_BUS("energy_import_bus", PartEnergyImportBus.class, Features.ENERGY_BUSES),
		ENERGY_EXPORT_BUS("energy_export_bus", PartEnergyExportBus.class, Features.ENERGY_BUSES),
		ENERGY_TERM("energy_terminal", PartEnergyTerminal.class, Features.ENERGY_TERM),

		PART_DRIVE("part_drive", PartDrive.class, Features.PART_DRIVE),

		PART_PERFECT_IFACE("part_perfect_iface", PartPerfectInterface.class, Features.PERFECT_INTERFACE),
		PART_PATTERNS_IFACE("part_patterns_iface", PartPatternsInterface.class, Features.PATTERNS_INTERFACE);



		private static Int2ObjectLinkedOpenHashMap<PartType> cachedValues;
		private final String id;
		private final Class<? extends IPart> clazz;
		private final int baseDamage;
		private final boolean enabled;
		private final Set<ResourceLocation> models;
		private Constructor<? extends IPart> constructor;
		private GuiText extraName;
		private final Features[] features;
		private List<ModelResourceLocation> itemModels;

		PartType(String id, Class<? extends IPart> clazz, Features... features) {
			this.id = id;
			this.clazz = clazz;
			this.baseDamage = this.ordinal();
			this.features = ArrayUtils.add(features, Features.PARTS);

			boolean enabled = false;
			for (Features f : features) {
				if (f.isEnabled()) {
					if (f.getRequiredModid() != null && !Loader.isModLoaded(f.getRequiredModid())) {
						continue;
					}
					enabled = true;
				}
			}

			this.enabled = enabled;

			if (this.enabled) {
				// Only load models if the part is enabled, otherwise we also run into class-loading issues while
				// scanning for annotations
				if (Platform.isClientInstall()) {
					this.itemModels = this.createItemModels(id);
				}
				if (clazz != null) {
					this.models = new HashSet<>(PartModelsHelper.createModels(clazz));
				} else {
					this.models = Collections.emptySet();
				}
			} else {
				if (Platform.isClientInstall()) {
					this.itemModels = Collections.emptyList();
				}
				this.models = Collections.emptySet();
			}
		}

		PartType(String id, Class<? extends IPart> clazz, Features features, GuiText extraName) {
			this(id, clazz, features);
			this.extraName = extraName;
		}

		public static Int2ObjectLinkedOpenHashMap<PartType> getCachedValues() {
			if (cachedValues == null) {
				cachedValues = new Int2ObjectLinkedOpenHashMap<>();
				Arrays.stream(values()).forEach((partType -> cachedValues.put(partType.ordinal(), partType)));
			}
			return cachedValues;
		}

		@SideOnly(Side.CLIENT)
		private static ModelResourceLocation modelFromBaseName(String baseName) {
			return new ModelResourceLocation(new ResourceLocation(Tags.MODID, "part/" + baseName), "inventory");
		}

		@SideOnly(Side.CLIENT)
		private List<ModelResourceLocation> createItemModels(String baseName) {
			return ImmutableList.of(modelFromBaseName(baseName));
		}

		public Features[] getFeature() {
			return this.features;
		}

		public boolean isEnabled() {
			return this.enabled;
		}

		public int getBaseDamage() {
			return this.baseDamage;
		}

		public Class<? extends IPart> getPart() {
			return this.clazz;
		}

		public String getUnlocalizedName() {
			return "item.crazyae.part." + this.name().toLowerCase();
		}

		public GuiText getExtraName() {
			return this.extraName;
		}

		public Constructor<? extends IPart> getConstructor() {
			return this.constructor;
		}

		public void setConstructor(final Constructor<? extends IPart> constructor) {
			this.constructor = constructor;
		}

		@SideOnly(Side.CLIENT)
		public List<ModelResourceLocation> getItemModels() {
			return this.itemModels;
		}

		public Set<ResourceLocation> getModels() {
			return this.models;
		}

		public String getId() {
			return this.id;
		}

		public void addCheckedInformation(ItemStack stack, World world, List<String> lines,
		                                  ITooltipFlag advancedTooltips) {
		}
	}
}
