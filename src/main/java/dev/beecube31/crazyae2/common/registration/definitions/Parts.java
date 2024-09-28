package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.core.features.DamagedItemDefinition;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import com.google.common.collect.ImmutableList;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.features.IFeature;
import dev.beecube31.crazyae2.common.items.CrazyAEBaseItemPart;
import dev.beecube31.crazyae2.common.parts.implementations.PartExportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.PartFluidExportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.PartFluidImportBusImp;
import dev.beecube31.crazyae2.common.parts.implementations.PartImportBusImp;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.*;

public class Parts implements Definitions<DamagedItemDefinition> {
	private final Object2ObjectOpenHashMap<String, DamagedItemDefinition> byId = new Object2ObjectOpenHashMap<>();
	private final CrazyAEBaseItemPart itemPart;

	private final DamagedItemDefinition improvedImportBus;
	private final DamagedItemDefinition improvedExportBus;
	private final DamagedItemDefinition improvedImportFluidBus;
	private final DamagedItemDefinition improvedExportFluidBus;


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
	}

	public DamagedItemDefinition improvedImportBus() {
		return this.improvedImportBus;
	}

	public DamagedItemDefinition improvedExportBus() {
		return this.improvedExportBus;
	}

	public DamagedItemDefinition improvedImportFluidBus() {
		return this.improvedImportFluidBus;
	}

	public DamagedItemDefinition improvedExportFluidBus() {
		return this.improvedExportFluidBus;
	}

	public static Optional<PartType> getById(int itemDamage) {
		return Optional.ofNullable(PartType.getCachedValues().getOrDefault(itemDamage, null));
	}

	@NotNull
	private DamagedItemDefinition createPart(CrazyAEBaseItemPart baseItemPart, PartType partType) {
		var def = new DamagedItemDefinition(partType.getId(),
			baseItemPart.createPart(partType));

		this.byId.put(partType.id, def);
		return def;
	}

	@Override
	public Optional<DamagedItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	public enum PartType implements IDefinition {
		IMPROVED_IMPORT_BUS("improved_import_bus", PartImportBusImp.class, Features.IMPROVED_BUSES),
		IMPROVED_EXPORT_BUS("improved_export_bus", PartExportBusImp.class, Features.IMPROVED_BUSES),

		IMPROVED_IMPORT_FLUID_BUS("improved_import_fluid_bus", PartFluidImportBusImp.class, Features.IMPROVED_BUSES),
		IMPROVED_EXPORT_FLUID_BUS("improved_export_fluid_bus", PartFluidExportBusImp.class, Features.IMPROVED_BUSES);



		private static Int2ObjectLinkedOpenHashMap<PartType> cachedValues;
		private final String id;
		private final Class<? extends IPart> clazz;
		private final int baseDamage;
		private final boolean enabled;
		private final Set<ResourceLocation> models;
		private Constructor<? extends IPart> constructor;
		private GuiText extraName;
		private List<ModelResourceLocation> itemModels;

		PartType(String id, Class<? extends IPart> clazz, IFeature features) {
			this.id = id;
			this.clazz = clazz;
			this.baseDamage = this.ordinal();

			this.enabled = features.isEnabled();
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
