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
import dev.beecube31.crazyae2.common.registration.upgrades.UpgradeInfo;
import dev.beecube31.crazyae2.common.registration.upgrades.UpgradesInfoProvider;
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
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().ioPortImp(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{2048 * 32}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{2048 * 512}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{2048 * 4096}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{2048}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 96}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 192}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 384}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 512}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{256}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 96}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 192}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 384}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{8 * 512}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{256}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedImportFluidBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{4000 * 8}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{8000 * 16}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{16000 * 24}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{32000 * 32}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
								},
								new double[]{8000}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().improvedExportFluidBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{4000 * 8}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{8000 * 16}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{16000 * 24}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
										},
										new double[]{32000 * 32}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_FLUIDS_PER_TICK
								},
								new double[]{8000}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().improvedMolecularAssembler(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{16}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{48}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{96}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{192}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{320}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{8}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{64}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{192}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{384}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{768}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{1152}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{1}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaImportBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 64}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 512}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 2048}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 8192}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
								},
								new double[]{32}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().parts().manaExportBus(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 64}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 512}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 2048}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
										},
										new double[]{256 * 8192}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_MANA_PER_TICK
								},
								new double[]{32}
						)
				));

				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{8, 8, 10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{24, 24, 25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{48, 48, 50}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{192, 192, 100}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{320, 320, 100}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
										UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1, 1, 1}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{8, 8, 10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{24, 24, 25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{48, 48, 50}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{192, 192, 100}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{320, 320, 100}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
										UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1, 1, 1}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{8, 8, 10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{24, 24, 25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{48, 48, 50}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{192, 192, 100}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{320, 320, 100}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
										UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1, 1, 1}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{8, 8, 10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{24, 24, 25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{48, 48, 50}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{192, 192, 100}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{320, 320, 100}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
										UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1, 1, 1}
						)
				));
				UpgradeType.STACKS.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{8, 8, 10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{24, 24, 25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{48, 48, 50}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{192, 192, 100}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
												UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{320, 320, 100}
								)
						},
						UpgradeType.STACKS,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK,
										UpgradeInfo.UpgradeType.TASKS_STORAGE_CAPACITY,
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1, 1, 1}
						)
				));
			});
		}

		this.improvedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.IMPROVED_SPEED);
		if (this.improvedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{25}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{50}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));


				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{2}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{5}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{20}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{2}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{5}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{20}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{2}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{5}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{20}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{2}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{5}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{20}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.IMPROVED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{2}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{5}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{10}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{15}
								),
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{20}
								)
						},
						UpgradeType.IMPROVED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
			});
		}

		this.advancedSpeedUpgrade = this.createUpgrade(this.upgrade, UpgradeType.ADVANCED_SPEED);
		if (this.advancedSpeedUpgrade.isEnabled()) {
			registry.addBootstrapComponent((IPostInitComponent) r -> {
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().bigCrystalCharger(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
										},
										new double[]{64}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.MORE_ITEMS_PER_TICK
								},
								new double[]{1}
						)
				));


				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalElventrade(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{100}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalRunealtar(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{100}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPuredaisy(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{100}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalPetal(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{100}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
				UpgradeType.ADVANCED_SPEED.registerItem(CrazyAE.definitions().blocks().mechanicalManapool(), new UpgradeInfo(
						new UpgradeInfo.LevelInfo[]{
								new UpgradeInfo.LevelInfo(
										new UpgradeInfo.UpgradeType[]{
												UpgradeInfo.UpgradeType.WORK_SPEED
										},
										new double[]{100}
								)
						},
						UpgradeType.ADVANCED_SPEED,
						new UpgradeInfo.LevelInfo(
								new UpgradeInfo.UpgradeType[]{
										UpgradeInfo.UpgradeType.WORK_SPEED
								},
								new double[]{1}
						)
				));
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

		public void registerItem(IItemDefinition item, UpgradeInfo inf) {
			if (item != null) {
				item.maybeStack(1).ifPresent((is) -> {
                    this.supportedMax.put(is, inf.getLevelsInfo().size());
					UpgradesInfoProvider.addUpgradeInfo(
							item,
							inf
					);
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

}
