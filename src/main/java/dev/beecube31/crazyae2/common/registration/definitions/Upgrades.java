package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.bootstrap.components.IPostInitComponent;
import appeng.core.features.DamagedItemDefinition;
import appeng.core.features.IStackSrc;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.IFeature;
import dev.beecube31.crazyae2.common.features.subfeatures.UpgradeFeatures;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.DamagedDefinitions;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEDamagedItemRendering;
import dev.beecube31.crazyae2.common.registration.registry.rendering.CrazyAEIModelProvider;
import dev.beecube31.crazyae2.core.CrazyAE;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().ioPortImp(), 3);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 4);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 4);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 4);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 4);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().improvedMolecularAssembler(), 5);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), 5);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaImportBus(), 4);
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaExportBus(), 4);
			});
		}

		this.improvedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.IMPROVED_SPEED);
		if (this.improvedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().manaImportBus(), 4);
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().parts().manaExportBus(), 4);
			});
		}

		this.advancedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.ADVANCED_SPEED);
		if (this.advancedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().manaImportBus(), 1);
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().parts().manaExportBus(), 1);
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

			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), 2);
			appeng.api.config.Upgrades.CAPACITY.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), 2);

			appeng.api.config.Upgrades.FUZZY.registerItem(CrazyAE.definitions().parts().improvedExportBus(), 1);
			appeng.api.config.Upgrades.FUZZY.registerItem(CrazyAE.definitions().parts().improvedImportBus(), 1);
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
			return this.supportedMax;
		}

		public void registerItem(IItemDefinition item, int maxSupported) {
			if (item != null) {
				item.maybeStack(1).ifPresent((is) -> this.registerItem(is, maxSupported));
			}
		}

		public void registerItem(ItemStack stack, int maxSupported) {
			if (stack != null) {
				this.supportedMax.put(stack, maxSupported);
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

}
