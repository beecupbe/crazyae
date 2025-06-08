package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.bootstrap.components.IPostInitComponent;
import appeng.core.features.DamagedItemDefinition;
import appeng.core.features.IStackSrc;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.IFeature;
import dev.beecube31.crazyae2.common.features.subfeatures.UpgradeFeatures;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.DamagedDefinitions;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEDamagedItemRendering;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEIModelProvider;
import dev.beecube31.crazyae2.common.registration.upgrades.UpgradesInfoProvider;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.util.FeatureSet;
import dev.beecube31.crazyae2.core.CrazyAE;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class Upgrades implements DamagedDefinitions<DamagedItemDefinition, Upgrades.UpgradeType> {
	private final Object2ObjectOpenHashMap<String, DamagedItemDefinition> byId = new Object2ObjectOpenHashMap<>();

	private final IItemDefinition stackUpgrade;
	private final IItemDefinition improvedSpeedUpgrade;
	private final IItemDefinition advancedSpeedUpgrade;

	private final CrazyAEUpgradeModule upgrade;

	public Upgrades(Registry registry) {
		this.upgrade = new CrazyAEUpgradeModule();
		registry.item("upgrade", () -> this.upgrade)
			.rendering(new CrazyAEDamagedItemRendering<>(this))
			.build();

		this.stackUpgrade = this.createUpgrade(this.upgrade, UpgradeType.STACKS);
		if (this.stackUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().ioPortImp(), new FeatureSet().add("stacks:3").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("2048;65536;1048576;8388608"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("256;1024;1792;3328;4352"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("256;1024;1792;3328;4352"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_FLUID_PER_TICK.getUnlocalized()).add("8000;40000;136000;392000;1032000"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_FLUID_PER_TICK.getUnlocalized()).add("8000;40000;136000;392000;1032000"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().improvedMolecularAssembler(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("8;16;32;64;128;160"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_OPERATIONS_PER_JOB).add("1;64;192;384;768;1152"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaImportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("256;16384;131328;524544;2097408"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaExportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("256;16384;131328;524544;2097408"));

				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().energyImportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;32768;524288;33554432"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().energyExportBus(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;32768;524288;33554432"));

				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().trashcanItem(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;16384;65536;262144"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().trashcanEnergy(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;16384;65536;262144"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().trashcanFluid(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;16384;65536;262144"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().trashcanMana(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("32;2048;16384;65536;262144"));

				//UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().perfectInscriber(), new FeatureSet().add("stacks:4").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;64;512;4096"));

				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalBrewery(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalTeraplate(), new FeatureSet().add("stacks:5").add(CrazyAETooltip.MORE_ITEMS_PER_TICK.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_TASKS_IN_JOB.getUnlocalized()).add("1;8;24;48;192;320").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;25;50;100;100"));
			});
		}

		this.improvedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.IMPROVED_SPEED);
		if (this.improvedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new FeatureSet().add("imp:5").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;10;15;25;40;60"));

				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalBrewery(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalTeraplate(), new FeatureSet().add("imp:4").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;2;5;10;15;20"));

			});
		}

		this.advancedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.ADVANCED_SPEED);
		if (this.advancedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));

				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalBrewery(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalTeraplate(), new FeatureSet().add("adv:1").add(CrazyAETooltip.MORE_OPERATIONS_PER_TICK.getUnlocalized()).add("1;100"));
			});
		}

		registry.addBootstrapComponent((IPostInitComponent) r -> {
			appeng.api.config.Upgrades.CRAFTING.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 1);

			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().manaImportBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().manaExportBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().energyImportBus(), 1);
			appeng.api.config.Upgrades.REDSTONE.registerItem(CrazyAE.definitions().parts().energyExportBus(), 1);

			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 2);

			appeng.api.config.Upgrades.FUZZY.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 1);
			appeng.api.config.Upgrades.FUZZY.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 1);

			appeng.api.config.Upgrades.FUZZY.registerItem(CrazyAE.definitions().blocks().trashcanItem(), 1);
		});
	}

	@NotNull
	private DamagedItemDefinition createUpgrade(CrazyAEUpgradeModule material, UpgradeType upgradeType) {
		var def = new DamagedItemDefinition(upgradeType.getId(),
			material.createUpgrade(upgradeType));

		this.byId.put(upgradeType.getId(), def);
		return def;
	}

	public Optional<UpgradeType> getById(int itemDamage) {
		return Optional.ofNullable(UpgradeType.getCachedValues().getOrDefault(itemDamage, null));
	}

	public Optional<DamagedItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	public IItemDefinition stackUpgrade() {
		return this.stackUpgrade;
	}

	public IItemDefinition improvedSpeedUpgrade() {
		return this.improvedSpeedUpgrade;
	}

	public IItemDefinition advancedSpeedUpgrade() {
		return this.advancedSpeedUpgrade;
	}

	@Override
	public Collection<UpgradeType> getEntries() {
		return UpgradeType.getCachedValues().values();
	}

	@Nullable
	@Override
	public UpgradeType getType(ItemStack is) {
		return this.upgrade.getType(is);
	}

	public enum UpgradeType implements CrazyAEIModelProvider {

		STACKS("stacks", UpgradeFeatures.STACKS),
		IMPROVED_SPEED("improved_speed", UpgradeFeatures.IMPROVED_SPEED_UPGRADE),
		ADVANCED_SPEED("advanced_speed", UpgradeFeatures.ADVANCED_SPEED_UPGRADE);


		private static Int2ObjectLinkedOpenHashMap<UpgradeType> cachedValues;
		private final Map<ItemStack, Integer> supportedMax = new HashMap<>();
		private final String id;
		private final IFeature features;
		private final String translationKey;
		private final ModelResourceLocation model;
		private final int damageValue = this.ordinal();
		private boolean isRegistered;
		private Item itemInstance;
		private IStackSrc stackSrc;

		UpgradeType(String id, IFeature features) {
			this.id = id;
			this.features = features;
			this.translationKey = "item." + Tags.MODID + ".upgrade." + id;
			this.model = new ModelResourceLocation(new ResourceLocation(Tags.MODID, "upgrade/" + id), "inventory");
		}

		public static Int2ObjectLinkedOpenHashMap<UpgradeType> getCachedValues() {
			if (cachedValues == null) {
				cachedValues = new Int2ObjectLinkedOpenHashMap<>();
				Arrays.stream(values()).forEach((upgradeType -> cachedValues.put(upgradeType.ordinal(),
					upgradeType)));
			}
			return cachedValues;
		}

		@SuppressWarnings("deprecation")
		public String getLocalizedCardType() {
			switch (this) {
				default -> {
					return "INVALID_CARD_TYPE";
				}

				case STACKS -> {
					return I18n.translateToLocal("item.crazyae.upgrade.stacks.name");
				}

				case IMPROVED_SPEED -> {
					return I18n.translateToLocal("item.crazyae.upgrade.improved_speed.name");
				}

				case ADVANCED_SPEED -> {
					return I18n.translateToLocal("item.crazyae.upgrade.advanced_speed.name");
				}
			}
		}

		public String getId() {
			return this.id;
		}

		public IFeature getFeature() {
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

		public boolean isEnabled() {
			return this.features.isEnabled();
		}

		public void markReady() {
			this.isRegistered = true;
		}

		public int getDamageValue() {
			return this.damageValue;
		}

		public Item getItemInstance() {
			return this.itemInstance;
		}

		public void setItemInstance(final Item itemInstance) {
			this.itemInstance = itemInstance;
		}

		public UpgradeStackSrc getStackSrc() {
			return (UpgradeStackSrc) this.stackSrc;
		}

		public void setStackSrc(final UpgradeStackSrc stackSrc) {
			this.stackSrc = stackSrc;
		}

		public ModelResourceLocation getModel() {
			return this.model;
		}

		public Map<ItemStack, Integer> getSupported() {
			return ImmutableMap.copyOf(this.supportedMax);
		}

		public void registerItem(IItemDefinition item, FeatureSet inf) {
			if (item != null) {
				item.maybeStack(1).ifPresent(is -> {
					List<UpgradesFeatureSetParser.FeatureEntry> entry = UpgradesFeatureSetParser.parse(inf);

					for (UpgradesFeatureSetParser.FeatureEntry e : entry) {
						this.supportedMax.put(is, e.upgradesCount);
						UpgradesInfoProvider.addUpgradeInfo(item, e);
					}
				});
			}
		}

		public void addCheckedInformation(ItemStack stack, World world, List<String> lines, ITooltipFlag advancedTooltips) {}
	}

	public static class UpgradeStackSrc implements IStackSrc {
		private final UpgradeType src;
		private final boolean enabled;

		public UpgradeStackSrc(UpgradeType src, boolean enabled) {
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

	public static class UpgradesFeatureSetParser {
		public static class FeatureEntry {
			public Upgrades.UpgradeType upgradeType;
			public int upgradesCount;
			public String type;
			public int[] values;

			public FeatureEntry(Upgrades.UpgradeType upgradeType, int upgradesCount, String type, int[] values) {
				this.upgradeType = upgradeType;
				this.upgradesCount = upgradesCount;
				this.type = type;
				this.values = values;
			}
		}

		public static List<FeatureEntry> parse(FeatureSet fs) {
			List<Object> set = new ArrayList<>(fs.get());

			if (set.size() < 3) {
				throw new IllegalArgumentException("Invalid FeatureSet syntax");
			}

			String upgradeToken = set.get(0).toString();
			UpgradeType upgradeType;
			int upgradesCount;

			if (upgradeToken.contains(":")) {
				String[] parts = upgradeToken.split(":", 2);
				switch (parts[0]) {
					default -> {
						throw new IllegalArgumentException("Invalid FeatureSet syntax");
					}

					case "stacks" -> {
						upgradeType = UpgradeType.STACKS;
					}

					case "imp" -> {
						upgradeType = UpgradeType.IMPROVED_SPEED;
					}

					case "adv" -> {
						upgradeType = UpgradeType.ADVANCED_SPEED;
					}
				}
				try {
					upgradesCount = Integer.parseInt(parts[1]);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid FeatureSet syntax");
				}
			} else {
				throw new IllegalArgumentException("Invalid FeatureSet syntax");
			}

			List<FeatureEntry> entries = new ArrayList<>();

			for (int i = 1; i < set.size() - 1; i += 2) {
				String property = set.get(i).toString();
				String numericToken = set.get(i + 1).toString();

				String[] numParts = numericToken.split(";");
				int[] values = new int[numParts.length];
				for (int j = 0; j < numParts.length; j++) {
					try {
						values[j] = Integer.parseInt(numParts[j]);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Current FeatureSet accepts only integer values");
					}
				}

				FeatureEntry entry = new FeatureEntry(upgradeType, upgradesCount, property, values);
				entries.add(entry);
			}
			return entries;
		}

	}

}
