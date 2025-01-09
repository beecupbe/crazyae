package dev.beecube31.crazyae2.common.registration.definitions;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IItems;
import appeng.bootstrap.components.IPostInitComponent;
import appeng.core.AEConfig;
import appeng.core.features.ItemDefinition;
import dev.beecube31.crazyae2.client.rendering.*;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.items.*;
import dev.beecube31.crazyae2.common.items.cells.DenseFluidCell;
import dev.beecube31.crazyae2.common.items.cells.DenseItemCell;
import dev.beecube31.crazyae2.common.items.cells.ImprovedPortableCell;
import dev.beecube31.crazyae2.common.items.cells.ManaItemCell;
import dev.beecube31.crazyae2.common.items.internal.ManaAsAEStack;
import dev.beecube31.crazyae2.common.items.patterns.*;
import dev.beecube31.crazyae2.common.registration.registry.Registry;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.Definitions;
import dev.beecube31.crazyae2.integrations.jei.JEIPlugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Items implements Definitions<IItemDefinition> {
	private final Object2ObjectOpenHashMap<String, IItemDefinition> byId = new Object2ObjectOpenHashMap<>();
	private final IItemDefinition storageCell256k;
	private final IItemDefinition storageCell1mb;
	private final IItemDefinition storageCell4mb;
	private final IItemDefinition storageCell16mb;
	private final IItemDefinition storageCell64mb;
	private final IItemDefinition storageCell256mb;
	private final IItemDefinition storageCell1gb;
	private final IItemDefinition storageCell2gb;

	private final IItemDefinition manaCell1k;
	private final IItemDefinition manaCell4k;
	private final IItemDefinition manaCell16k;
	private final IItemDefinition manaCell64k;
	private final IItemDefinition manaCell256k;
	private final IItemDefinition manaCell1mb;
	private final IItemDefinition manaCell4mb;
	private final IItemDefinition manaCell16mb;
	private final IItemDefinition manaCell64mb;
	private final IItemDefinition manaCell256mb;
	private final IItemDefinition manaCell1gb;
	private final IItemDefinition manaCell2gb;

	private final IItemDefinition improvedPortableCell;
	private final IItemDefinition advancedPortableCell;
	private final IItemDefinition perfectPortableCell;

	private final IItemDefinition fluidCell256k;
	private final IItemDefinition fluidCell1mb;
	private final IItemDefinition fluidCell4mb;
	private final IItemDefinition fluidCell16mb;
	private final IItemDefinition fluidCell64mb;
	private final IItemDefinition fluidCell256mb;
	private final IItemDefinition fluidCell1gb;
	private final IItemDefinition fluidCell2gb;

	private final IItemDefinition quantumWirelessBooster;
	private final IItemDefinition manaConnector;
//	private final IItemDefinition patternsUSBStick;

	private final IItemDefinition manaAsAEStack;

	private final IItemDefinition elventradeEncodedPattern;
	private final IItemDefinition manapoolEncodedPattern;
	private final IItemDefinition petalEncodedPattern;
	private final IItemDefinition puredaisyEncodedPattern;
	private final IItemDefinition runealtarEncodedPattern;

	private final IItemDefinition colorizer;


	public Items(Registry registry) {
		this.manaConnector = this.registerById(registry.item("mana_connector", ManaConnector::new)
				.features(Features.MANA_BUSES)
				.ifModPresent("botania")
				.build());

//		this.patternsUSBStick = this.registerById(registry.item("patterns_usb_stick", PatternsUSBStick::new)
//				.features(Features.STUB)
//				.build());

		this.colorizer = this.registerById(registry.item("gui_colorizer", Colorizer::new)
				.features(Features.STUB)
				.build());



		this.elventradeEncodedPattern = this.registerById(registry.item("elventrade_encoded_pattern", ElventradeEncodedPattern::new)
				.features(Features.STUB)
				.rendering(new ElventradeEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.manapoolEncodedPattern = this.registerById(registry.item("manapool_encoded_pattern", ManapoolEncodedPattern::new)
				.features(Features.STUB)
				.rendering(new ManapoolEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.petalEncodedPattern = this.registerById(registry.item("petal_encoded_pattern", PetalEncodedPattern::new)
				.features(Features.STUB)
				.rendering(new PetalEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.puredaisyEncodedPattern = this.registerById(registry.item("puredaisy_encoded_pattern", PuredaisyEncodedPattern::new)
				.features(Features.STUB)
				.rendering(new PuredaisyEncodedPatternRendering())
				.ifModPresent("botania")
				.build());

		this.runealtarEncodedPattern = this.registerById(registry.item("runealtar_encoded_pattern", RunealtarEncodedPattern::new)
				.features(Features.STUB)
				.rendering(new RunealtarEncodedPatternRendering())
				.ifModPresent("botania")
				.build());




		this.storageCell256k = this.registerById(registry.item("storage_cell_256k", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_256K, 256 * 1024, 4D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell1mb = this.registerById(registry.item("storage_cell_1mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_1MB, 1024 * 1024, 6D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell4mb = this.registerById(registry.item("storage_cell_4mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_4MB, 4096 * 1024, 8D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell16mb = this.registerById(registry.item("storage_cell_16mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_16MB, 16384 * 1024, 12D))
				.features(Features.DENSE_CELLS)
				.build());

		this.storageCell64mb = this.registerById(registry.item("storage_cell_64mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_64MB, 65536 * 1024, 24D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell256mb = this.registerById(registry.item("storage_cell_256mb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_256MB, 262144 * 1024, 40D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell1gb = this.registerById(registry.item("storage_cell_1gb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_1GB, 1048576 * 1024, 52D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.storageCell2gb = this.registerById(registry.item("storage_cell_2gb", () -> new DenseItemCell(Materials.MaterialType.CELL_PART_2GB, Integer.MAX_VALUE, 64D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());



		this.manaCell1k = this.registerById(registry.item("mana_cell_1k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1K, 1024, 1D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell4k = this.registerById(registry.item("mana_cell_4k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_4K, 4 * 1024, 2D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell16k = this.registerById(registry.item("mana_cell_16k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_16K, 16 * 1024, 3D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell64k = this.registerById(registry.item("mana_cell_64k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_64K, 64 * 1024, 4D))
				.ifModPresent("botania")
				.features(Features.MANA_CELLS)
				.build());

		this.manaCell256k = this.registerById(registry.item("mana_cell_256k", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_256K, 256 * 1024, 4D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell1mb = this.registerById(registry.item("mana_cell_1mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1MB, 1024 * 1024, 6D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell4mb = this.registerById(registry.item("mana_cell_4mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_4MB, 4096 * 1024, 8D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell16mb = this.registerById(registry.item("mana_cell_16mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_16MB, 16384 * 1024, 12D))
				.ifModPresent("botania")
				.features(Features.MANA_DENSE_CELLS)
				.build());

		this.manaCell64mb = this.registerById(registry.item("mana_cell_64mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_64MB, 65536 * 1024, 24D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell256mb = this.registerById(registry.item("mana_cell_256mb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_256MB, 262144 * 1024, 40D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell1gb = this.registerById(registry.item("mana_cell_1gb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_1GB, 1048576 * 1024, 52D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());

		this.manaCell2gb = this.registerById(registry.item("mana_cell_2gb", () -> new ManaItemCell(Materials.MaterialType.MANA_PART_2GB, Integer.MAX_VALUE, 64D))
				.ifModPresent("botania")
				.features(Features.MEGA_MANA_DENSE_CELLS)
				.build());



		this.improvedPortableCell = this.registerById(registry.item("improved_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 4, 1024 * 1024, 1, 2D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());
		this.advancedPortableCell = this.registerById(registry.item("advanced_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 8, 4096 * 1024, 1, 8D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());
		this.perfectPortableCell = this.registerById(registry.item("perfect_portable_cell", () -> new ImprovedPortableCell(AEConfig.instance().getPortableCellBattery() * 24, 16384 * 1024, 1, 16D))
				.features(Features.PORTABLE_DENSE_CELLS)
				.build());



		this.fluidCell256k = this.registerById(registry.item("fluid_storage_cell_256k", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_256K, 256 * 1024, 4D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell1mb = this.registerById(registry.item("fluid_storage_cell_1mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_1MB, 1024 * 1024, 6D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell4mb = this.registerById(registry.item("fluid_storage_cell_4mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_4MB, 4096 * 1024, 8D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell16mb = this.registerById(registry.item("fluid_storage_cell_16mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_16MB, 16384 * 1024, 12D))
				.features(Features.DENSE_CELLS)
				.build());

		this.fluidCell64mb = this.registerById(registry.item("fluid_storage_cell_64mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_64MB, 65536 * 1024, 24D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell256mb = this.registerById(registry.item("fluid_storage_cell_256mb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_256MB, 262144 * 1024, 40D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell1gb = this.registerById(registry.item("fluid_storage_cell_1gb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_1GB, 1048576 * 1024, 52D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.fluidCell2gb = this.registerById(registry.item("fluid_storage_cell_2gb", () -> new DenseFluidCell(Materials.MaterialType.CELL_FLUID_PART_2GB, Integer.MAX_VALUE, 64D))
				.features(Features.MEGA_DENSE_CELLS)
				.build());

		this.quantumWirelessBooster = this.registerById(registry.item("quantum_wireless_booster", QuantumWirelessBooster::new)
				.features(Features.STUB)
				.build());

		JEIPlugin.hideItemFromJEI(this.manaAsAEStack = this.registerById(registry.item("mana_as_aestack", ManaAsAEStack::new)
								.ifModPresent("botania")
								.features(Features.MANA_BUSES, Features.MANA_TERM, Features.MANA_CELLS, Features.MANA_DENSE_CELLS, Features.MEGA_MANA_DENSE_CELLS)
								.hide()
								.build())
		);

		registry.addBootstrapComponent((IPostInitComponent) r -> {
			IItems enabledItems = AEApi.instance().definitions().items();
			IItemDefinition itemCellsFeature = enabledItems.cell1k();
			if (itemCellsFeature.isEnabled()) {
				if (Features.DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(itemCellsFeature, new IItemDefinition[]{
							this.storageCell256k,
							this.storageCell1mb,
							this.storageCell4mb,
							this.storageCell16mb
					});
				}

				if (Features.MEGA_DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(itemCellsFeature, new IItemDefinition[]{
							this.storageCell64mb,
							this.storageCell256mb,
							this.storageCell1gb,
							this.storageCell2gb
					});
				}
			}

			IItemDefinition fluidCellsFeature = enabledItems.fluidCell1k();

			if (fluidCellsFeature.isEnabled()) {
				if (Features.DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(fluidCellsFeature, new IItemDefinition[]{
							this.fluidCell256k,
							this.fluidCell1mb,
							this.fluidCell4mb,
							this.fluidCell16mb
					});
				}

				if (Features.MEGA_DENSE_CELLS.isEnabled()) {
					mirrorCellUpgrades(fluidCellsFeature, new IItemDefinition[]{
							this.fluidCell64mb,
							this.fluidCell256mb,
							this.fluidCell1gb,
							this.fluidCell2gb
					});
				}
			}
        });
	}

	private static void mirrorCellUpgrades(Function<ItemStack, Boolean> predicate, IItemDefinition[] cells) {
		var supported = new java.util.HashMap<Upgrades, Integer>();
		Arrays.stream(Upgrades.values())
			.forEach(upgrade ->
				upgrade.getSupported().entrySet().stream()
					.filter(x -> predicate.apply(x.getKey()))
					.map(Map.Entry::getValue)
					.findFirst()
					.ifPresent(value -> supported.put(upgrade, value)));

		Arrays.stream(cells).forEach(iItemDefinition ->
			supported.forEach((key, value) ->
				key.registerItem(iItemDefinition, value)));
	}

	private static void mirrorCellUpgrades(IItemDefinition cellDef, IItemDefinition[] cells) {
		mirrorCellUpgrades(cellDef::isSameAs, cells);
	}

	private static void mirrorCellUpgrades(ItemStack itemStack, IItemDefinition[] cells) {
		mirrorCellUpgrades(itemStack::isItemEqual, cells);
	}

	private IItemDefinition registerById(ItemDefinition item) {
		this.byId.put(item.identifier(), item);
		return item;
	}

	@Override
	public Optional<IItemDefinition> getById(String id) {
		return Optional.ofNullable(this.byId.getOrDefault(id, null));
	}

	public IItemDefinition manaCell1k() {
		return this.manaCell1k;
	}

	public IItemDefinition manaCell4k() {
		return this.manaCell4k;
	}

	public IItemDefinition manaCell16k() {
		return this.manaCell16k;
	}

	public IItemDefinition manaCell64k() {
		return this.manaCell64k;
	}

	public IItemDefinition manaCell256k() {
		return this.manaCell256k;
	}

	public IItemDefinition manaCell1MB() {
		return this.manaCell1mb;
	}

	public IItemDefinition manaCell4MB() {
		return this.manaCell4mb;
	}

	public IItemDefinition manaCell16MB() {
		return this.manaCell16mb;
	}

	public IItemDefinition manaCell64MB() {
		return this.manaCell64mb;
	}

	public IItemDefinition manaCell256MB() {
		return this.manaCell256mb;
	}

	public IItemDefinition manaCell1GB() {
		return this.manaCell1gb;
	}

	public IItemDefinition manaCell2GB() {
		return this.manaCell2gb;
	}

	public IItemDefinition storageCell256K() {
		return this.storageCell256k;
	}

	public IItemDefinition storageCell1MB() {
		return this.storageCell1mb;
	}

	public IItemDefinition storageCell4MB() {
		return this.storageCell4mb;
	}

	public IItemDefinition storageCell16MB() {
		return this.storageCell16mb;
	}

	public IItemDefinition storageCell64MB() {
		return this.storageCell64mb;
	}

	public IItemDefinition storageCell256MB() {
		return this.storageCell256mb;
	}

	public IItemDefinition storageCell1GB() {
		return this.storageCell1gb;
	}

	public IItemDefinition storageCell2GB() {
		return this.storageCell2gb;
	}

	public IItemDefinition improvedPortableCell() {
		return this.improvedPortableCell;
	}

	public IItemDefinition advancedPortableCell() {
		return this.advancedPortableCell;
	}

	public IItemDefinition perfectPortableCell() {
		return this.perfectPortableCell;
	}

	public IItemDefinition fluidStorageCell256K() {
		return this.fluidCell256k;
	}

	public IItemDefinition fluidStorageCell1MB() {
		return this.fluidCell1mb;
	}

	public IItemDefinition fluidStorageCell4MB() {
		return this.fluidCell4mb;
	}

	public IItemDefinition fluidStorageCell16MB() {
		return this.fluidCell16mb;
	}

	public IItemDefinition fluidStorageCell64MB() {
		return this.fluidCell64mb;
	}

	public IItemDefinition fluidStorageCell256MB() {
		return this.fluidCell256mb;
	}

	public IItemDefinition fluidStorageCell1GB() {
		return this.fluidCell1gb;
	}

	public IItemDefinition fluidStorageCell2GB() {
		return this.fluidCell2gb;
	}

	public IItemDefinition quantumWirelessBooster() {
		return this.quantumWirelessBooster;
	}

	public IItemDefinition manaConnector() {
		return this.manaConnector;
	}

	public IItemDefinition manaAsAEStack() {
		return this.manaAsAEStack;
	}

	public IItemDefinition elventradeEncodedPattern() {
		return this.elventradeEncodedPattern;
	}

	public IItemDefinition manapoolEncodedPattern() {
		return this.manapoolEncodedPattern;
	}

	public IItemDefinition petalEncodedPattern() {
		return this.petalEncodedPattern;
	}

	public IItemDefinition runealtarEncodedPattern() {
		return this.runealtarEncodedPattern;
	}

	public IItemDefinition colorizer() {
		return this.colorizer;
	}

//	public IItemDefinition patternsUSBStick() {
//		return this.patternsUSBStick;
//	}

	public IItemDefinition puredaisyEncodedPattern() {
		return this.puredaisyEncodedPattern;
	}
}
